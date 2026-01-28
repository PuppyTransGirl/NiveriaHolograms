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
            Lang.sendMessage(player, "niveriaholograms.migrator.decentholograms.cannot_migrate_hologram", name);
            NiveriaHolograms.instance().getSLF4JLogger().error("The hologram '{}' couldn't be migrated", name, e);
        }
    }

    @Nullable
    private Hologram migrateHologram(@NotNull String name, @NotNull Player player, @NotNull File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        CustomLocation location = parseLocation(name, player, config.getString("location"));
        if (location == null)
            return null;

        TextHologramConfiguration configuration = new TextHologramConfiguration();
        configuration.billboard(Display.BillboardConstraints.VERTICAL);
        configuration.visibilityDistance(config.getInt("display-range", 48));

        List<String> text = parseLines(name, player, config);
        if (text == null)
            return null;

        configuration.text(text);

        // DecentHolograms default update-interval to 20, it would be too inefficient to set all holograms to 20 update interval so we just set them to 0 if they are at the default value
        int updateInterval = config.getInt("update-interval", 20);
        configuration.updateInterval(updateInterval == 20 ? 0 : updateInterval);

        return new Hologram(HologramType.TEXT, configuration, name, player.getUniqueId(), location);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private List<String> parseLines(@NotNull String name, @NotNull Player player, @NotNull FileConfiguration config) {
        List<Map<?, ?>> pages = config.getMapList("pages");
        if (pages.isEmpty()) {
            Lang.sendMessage(player, "niveriaholograms.migrator.decentholograms.no_page", name);
            return null;
        }

        if (pages.size() > 1)
            Lang.sendMessage(player, "niveriaholograms.migrator.decentholograms.too_many_pages", name, pages.size());

        Object linesObj = pages.getFirst().get("lines");
        if (!(linesObj instanceof List<?> linesList)) {
            Lang.sendMessage(player, "niveriaholograms.migrator.decentholograms.malformed_pages", name);
            return null;
        }

        List<Map<String, Object>> firstPage = (List<Map<String, Object>>) linesList;
        for (Map<String, Object> line : firstPage) {
            if (line.get("content") == null) {
                Lang.sendMessage(player, "niveriaholograms.migrator.decentholograms.malformed_pages", name);
                return null;
            }
        }

        List<String> result = new ArrayList<>();
        for (int i = 0; i < firstPage.size(); i++) {
            Map<String, Object> line = firstPage.get(i);

            // Convert content
            String content = String.valueOf(line.get("content"));
            String converted = LegacyToMiniMessage.convert(content);
            result.add(converted);

            // If this line has a height value equal to 0.5 and there is a following line
            // insert the filler empty line represented by "<white></white>"
            Object heightObj = line.get("height");
            if (heightObj == null || i >= firstPage.size() - 1)
                continue;

            boolean isHalf = false;
            if (heightObj instanceof Number number) {
                isHalf = Math.abs(number.doubleValue() - 0.5) < 1e-9;
            } else {
                try {
                    double d = Double.parseDouble(String.valueOf(heightObj));
                    isHalf = Math.abs(d - 0.5) < 1e-9;
                } catch (NumberFormatException ignored) {
                    // ignore malformed height
                    // it's not critical for migration
                }
            }

            if (isHalf)
                result.add("<white></white>");
        }

        return result;
    }

    @Nullable
    private CustomLocation parseLocation(@NotNull String name, @NotNull Player player, @Nullable String line) {
        if (line == null) {
            Lang.sendMessage(player, "niveriaholograms.migrator.decentholograms.malformed_location", name);
            return null;
        }

        String[] splitLine = line.split(":");
        if (splitLine.length != 4) {
            Lang.sendMessage(player, "niveriaholograms.migrator.decentholograms.malformed_location", name);
            return null;
        }

        String world = splitLine[0];
        double x, y, z;

        Optional<String> loadedWorld = Bukkit.getWorlds().stream()
                .map(World::getName)
                .filter(worldName -> worldName.equals(world))
                .findAny();

        if (loadedWorld.isEmpty()) {
            Lang.sendMessage(player, "niveriaholograms.migrator.decentholograms.invalid_world", name, world);
            return null;
        }

        try {
            x = Double.parseDouble(splitLine[1]);
            y = Double.parseDouble(splitLine[2]);
            z = Double.parseDouble(splitLine[3]);
        } catch (NumberFormatException e) {
            Lang.sendMessage(player, "niveriaholograms.migrator.decentholograms.malformed_location", name);
            return null;
        }

        return new CustomLocation(world, x, y, z, 0, 0);
    }
}
