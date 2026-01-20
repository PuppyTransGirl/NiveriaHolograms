package toutouchien.niveriaholograms.migration.migrators;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.utils.ComponentUtils;
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

    @Override
    public String name() {
        return "DecentHolograms";
    }

    @Override
    public boolean canRun() {
        return new File("plugins/DecentHolograms/holograms").isDirectory();
    }

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
                holograms.add(hologram);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return holograms;
    }

    @NotNull
    private Hologram migrateHologram(@NotNull Player player, File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        CustomLocation location = parseLocation(config.getString("location"));

        TextHologramConfiguration configuration = new TextHologramConfiguration();
        configuration.visibilityDistance(config.getInt("display-range", 48));
        configuration.updateInterval(0); // DecentHolograms default update-interval to 20, it would be too inefficient to set all holograms to 20 update interval so we just set them to 0
        configuration.text(parseLines(config));

        return new Hologram(HologramType.TEXT, configuration, file.getName(), player.getUniqueId(), location);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private List<String> parseLines(@NotNull FileConfiguration config) {
        List<Map<String, ?>> firstPage = (List<Map<String, ?>>) config.getMapList("pages")
                .stream()
                .findFirst()
                .orElseThrow()
                .get("lines");

        return firstPage
                .stream()
                .map(line -> legacyToMM((String) line.get("content")))
                .toList();
    }

    @NotNull
    private CustomLocation parseLocation(@NotNull String line) {
        String[] splitLine = line.split(":");

        String world = splitLine[0];
        double x = Double.parseDouble(splitLine[1]);
        double y = Double.parseDouble(splitLine[2]);
        double z = Double.parseDouble(splitLine[3]);

        return new CustomLocation(world, x, y, z, 0, 0);
    }

    @NotNull
    private String legacyToMM(@NotNull String legacy) {
        Component comp = LEGACY.deserialize(legacy);
        return ComponentUtils.serializeMM(comp);
    }
}
