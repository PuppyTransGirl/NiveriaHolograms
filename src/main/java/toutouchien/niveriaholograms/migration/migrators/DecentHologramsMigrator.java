package toutouchien.niveriaholograms.migration.migrators;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.world.entity.Display;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.TextHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.core.HologramType;
import toutouchien.niveriaholograms.migration.Migrator;
import toutouchien.niveriaholograms.utils.CustomLocation;
import toutouchien.niveriaholograms.utils.LegacyToMiniMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

public class DecentHologramsMigrator implements Migrator {
    @Override
    public boolean canRun() {
        return new File("plugins/DecentHolograms/holograms").isDirectory();
    }

    @NotNull
    @Override
    public ObjectList<Hologram> migrate(@NotNull Player player) {
        Path hologramsPath = Path.of("plugins/DecentHolograms/holograms");

        ObjectList<Hologram> holograms = new ObjectArrayList<>();
        try (Stream<Path> stream = Files.walk(hologramsPath)) {
            List<Path> ymlFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase(Locale.ROOT).endsWith(".yml"))
                    .toList();

            for (Path path : ymlFiles) {
                File file = path.toFile();

                String fileName = file.getName();
                String name = fileName.substring(0, fileName.length() - 4);

                fileToHologram(player, name, file, holograms);
            }
        } catch (IOException e) {
            NiveriaHolograms.instance().getSLF4JLogger().error("The DecentHolograms holograms couldn't be migrated", e);
        }

        return holograms;
    }

    private void fileToHologram(@NotNull Player player, @NotNull String name, @NotNull File file, @NotNull ObjectList<Hologram> holograms) {
        try {
            Hologram hologram = migrateHologram(name, player, file);
            if (hologram == null)
                return;

            holograms.add(hologram);
        } catch (Exception e) {
            LANG.sendMessage(player, "migrator.decentholograms.cannot_migrate_hologram",
                    Lang.unparsedPlaceholder("hologram_name", name)
            );
            NiveriaHolograms.instance().getSLF4JLogger().error("The hologram '{}' couldn't be migrated", name, e);
        }
    }

    @Nullable
    private Hologram migrateHologram(@NotNull String name, @NotNull Player player, @NotNull File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<Map<String, Object>> rawLines = firstPageLines(name, player, config);
        if (rawLines == null)
            return null;

        // Sum all heights from first to last - 1
        double heightOffset = 0;
        for (int i = 0; i < rawLines.size() - 1; i++)
            heightOffset += lineHeight(rawLines.get(i));

        CustomLocation location = parseLocation(name, player, config.getString("location"));
        if (location == null)
            return null;

        // Apply the calculated height offset to the Y coordinate
        location.y(location.y() - heightOffset);

        TextHologramConfiguration configuration = new TextHologramConfiguration();
        configuration.billboard(Display.BillboardConstraints.VERTICAL);
        configuration.visibilityDistance(config.getInt("display-range", 48));

        // Legacy conversion & spacing logic
        List<String> text = processPageLines(rawLines);
        configuration.text(text);

        int updateInterval = config.getInt("update-interval", 0);
        configuration.updateInterval(Math.max(updateInterval, 0));

        return new Hologram(HologramType.TEXT, configuration, name, player.getUniqueId(), location);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private List<Map<String, Object>> firstPageLines(@NotNull String name, @NotNull Player player, @NotNull FileConfiguration config) {
        List<Map<?, ?>> pages = config.getMapList("pages");
        if (pages.isEmpty()) {
            LANG.sendMessage(player, "migrator.decentholograms.no_page",
                    Lang.unparsedPlaceholder("hologram_name", name)
            );
            return null;
        }

        if (pages.size() > 1)
            LANG.sendMessage(player, "migrator.decentholograms.too_many_pages",
                    Lang.unparsedPlaceholder("hologram_name", name),
                    Lang.numberPlaceholder("migration_page_amount", pages.size())
            );

        Object linesObj = pages.getFirst().get("lines");
        if (!(linesObj instanceof List<?> linesList)) {
            LANG.sendMessage(player, "migrator.decentholograms.malformed_pages",
                    Lang.unparsedPlaceholder("hologram_name", name)
            );
            return null;
        }

        List<Map<String, Object>> firstPage = (List<Map<String, Object>>) linesList;
        for (Map<String, Object> line : firstPage) {
            if (line.get("content") == null) {
                LANG.sendMessage(player, "migrator.decentholograms.malformed_pages",
                        Lang.unparsedPlaceholder("hologram_name", name)
                );
                return null;
            }
        }

        return firstPage;
    }

    private double lineHeight(Map<String, Object> line) {
        Object heightObj = line.get("height");
        if (heightObj == null)
            return 0.0;

        if (heightObj instanceof Number number)
            return number.doubleValue();

        try {
            return Double.parseDouble(String.valueOf(heightObj));
        } catch (NumberFormatException ignored) {
            return 0.0;
        }
    }

    /**
     * Processes raw lines: Converts content and adds filler lines based on height.
     */
    private List<String> processPageLines(@NotNull List<Map<String, Object>> lines) {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            Map<String, Object> line = lines.get(i);

            // Convert content
            String content = String.valueOf(line.get("content"));
            String converted = LegacyToMiniMessage.convert(content);
            result.add(converted);

            // Check conditions to skip spacing logic
            Object heightObj = line.get("height");
            if (heightObj == null || i >= lines.size() - 1)
                continue;

            double height = lineHeight(line);

            // Calculate number of filler lines
            // Logic: 0.5-0.9 -> 1, 1.0-1.4 -> 2, etc. => floor(height * 2)
            int linesToAdd = (int) (height * 2);

            for (int j = 0; j < linesToAdd; j++)
                result.add("<white></white>");
        }

        return result;
    }

    @Nullable
    private CustomLocation parseLocation(@NotNull String name, @NotNull Player player, @Nullable String line) {
        if (line == null) {
            LANG.sendMessage(player, "migrator.decentholograms.malformed_location",
                    Lang.unparsedPlaceholder("hologram_name", name)
            );
            return null;
        }

        String[] splitLine = line.split(":");
        if (splitLine.length != 4) {
            LANG.sendMessage(player, "migrator.decentholograms.malformed_location",
                    Lang.unparsedPlaceholder("hologram_name", name)
            );
            return null;
        }

        String world = splitLine[0];
        double x, y, z;

        Optional<String> loadedWorld = Bukkit.getWorlds().stream()
                .map(World::getName)
                .filter(worldName -> worldName.equals(world))
                .findAny();

        if (loadedWorld.isEmpty()) {
            LANG.sendMessage(player, "migrator.decentholograms.invalid_world",
                    Lang.unparsedPlaceholder("hologram_name", name),
                    Lang.unparsedPlaceholder("hologram_world", String.valueOf(world))
            );
            return null;
        }

        try {
            x = Double.parseDouble(splitLine[1]);
            y = Double.parseDouble(splitLine[2]);
            z = Double.parseDouble(splitLine[3]);
        } catch (NumberFormatException e) {
            LANG.sendMessage(player, "migrator.decentholograms.malformed_location",
                    Lang.unparsedPlaceholder("hologram_name", name)
            );
            return null;
        }

        return new CustomLocation(world, x, y, z, 0, 0);
    }
}