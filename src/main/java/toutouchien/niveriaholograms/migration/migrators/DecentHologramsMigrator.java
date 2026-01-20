package toutouchien.niveriaholograms.migration.migrators;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.ComponentUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.TextHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.core.HologramType;
import toutouchien.niveriaholograms.migration.Migrator;
import toutouchien.niveriaholograms.utils.CustomLocation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DecentHologramsMigrator implements Migrator {
    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.builder()
                    .character('&')
                    .hexColors() // enables parsing of hex formats like &#rrggbb and &x&r&r&g&g&b&b
                    .build();

    @NotNull
    @Override
    public String name() {
        return "DecentHolograms";
    }

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
                    .filter(p -> p.toString().toLowerCase().endsWith(".yml"))
                    .toList();

            for (Path path : ymlFiles) {
                File file = path.toFile();

                Hologram hologram = migrateHologram(player, file);
                if (hologram == null)
                    continue;

                holograms.add(hologram);
            }
        } catch (IOException e) {
            NiveriaHolograms.instance().getSLF4JLogger().error("The DecentHolograms holograms couldn't be migrated", e);
        }

        return holograms;
    }

    @Nullable
    private Hologram migrateHologram(@NotNull Player player, @NotNull File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        String fileName = file.getName();
        String name = fileName.substring(0, fileName.length() - 4);
        CustomLocation location = parseLocation(name, player, config.getString("location"));
        if (location == null)
            return null;

        TextHologramConfiguration configuration = new TextHologramConfiguration();
        configuration.visibilityDistance(config.getInt("display-range", 48));

        List<String> text = parseLines(name, player, config);
        if (text == null)
            return null;

        configuration.text(text);

        // DecentHolograms default update-interval to 20, it would be too inefficient to set all holograms to 20 update interval so we just set them to 0
        int updateInterval = config.getInt("update-interval", 20);
        configuration.updateInterval(updateInterval == 20 ? 0 : updateInterval);

        return new Hologram(HologramType.TEXT, configuration, name, player.getUniqueId(), location);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private List<String> parseLines(@NotNull String name, @NotNull Player player, @NotNull FileConfiguration config) {
        List<Map<?, ?>> pages = config.getMapList("pages");
        if (pages.isEmpty()) {
            Lang.sendMessage(player, "niveriaholograms.migrators.decentholograms.malformed_pages", name);
            return null;
        }

        if (pages.size() > 1)
            Lang.sendMessage(player, "niveriaholograms.migrators.decentholograms.too_many_pages", name, pages.size());

        Object linesObj = pages.getFirst().get("lines");
        if (!(linesObj instanceof List<?> linesList)) {
            Lang.sendMessage(player, "niveriaholograms.migrators.decentholograms.malformed_pages", name);
            return null;
        }

        List<Map<String, String>> firstPage = (List<Map<String, String>>) linesList;
        return firstPage
                .stream()
                .map(line -> legacyToMM(line.get("content")))
                .toList();
    }

    @Nullable
    private CustomLocation parseLocation(@NotNull String name, @NotNull Player player, @Nullable String line) {
        if (line == null) {
            Lang.sendMessage(player, "niveriaholograms.migrators.decentholograms.malformed_location", name);
            return null;
        }

        String[] splitLine = line.split(":");
        if (splitLine.length != 4) {
            Lang.sendMessage(player, "niveriaholograms.migrators.decentholograms.malformed_location", name);
            return null;
        }

        String world = splitLine[0];
        double x, y, z;

        try {
            x = Double.parseDouble(splitLine[1]);
            y = Double.parseDouble(splitLine[2]);
            z = Double.parseDouble(splitLine[3]);
        } catch (NumberFormatException e) {
            Lang.sendMessage(player, "niveriaholograms.migrators.decentholograms.malformed_location", name);
            return null;
        }

        return new CustomLocation(world, x, y, z, 0, 0);
    }

    @NotNull
    private String legacyToMM(@NotNull String legacy) {
        Component comp = LEGACY.deserialize(legacy);
        return ComponentUtils.serializeMM(comp);
    }
}
