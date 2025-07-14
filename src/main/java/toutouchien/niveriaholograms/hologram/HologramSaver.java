package toutouchien.niveriaholograms.hologram;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;
import org.joml.Vector3f;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configuration.BlockHologramConfiguration;
import toutouchien.niveriaholograms.configuration.HologramConfiguration;
import toutouchien.niveriaholograms.configuration.ItemHologramConfiguration;
import toutouchien.niveriaholograms.configuration.TextHologramConfiguration;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HologramSaver {
    private final NiveriaHolograms plugin;
    private final ExecutorService saveExecutor;
    private final Set<String> currentlySaving = ConcurrentHashMap.newKeySet();
    private volatile boolean shutdown = false;

    public HologramSaver(NiveriaHolograms plugin) {
        this.plugin = plugin;
        this.saveExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "NiveriaHolograms-Saver");
            thread.setDaemon(true);
            thread.setPriority(Thread.NORM_PRIORITY - 1);
            return thread;
        });
    }

    public void saveHologram(Hologram hologram) {
        if (shutdown) {
            return;
        }

        String name = hologram.name();

        // Skip if already saving this hologram
        if (!currentlySaving.add(name)) {
            return;
        }

        Hologram snapshot = new Hologram(hologram);

        this.saveExecutor.submit(() -> {
            try {
                this.saveToFile(snapshot);
            } catch (Exception e) {
                this.plugin.getSLF4JLogger().warn("Failed to save hologram {}: {}", name, e.getMessage(), e);
            } finally {
                this.currentlySaving.remove(name); // Clean up
            }
        });
    }

    /**
     * Saves the hologram data to a YAML file using a temporary file.
     * This approach ensures that if the process is interrupted or fails midway,
     * only the temporary file may be corrupted, not the main holograms.yml file.
     */
    private void saveToFile(Hologram hologram) throws Exception {
        File file = new File(this.plugin.getDataFolder(), "holograms.yml");
        File tempFile = new File(file.getParent(), file.getName() + ".tmp");

        // Load the configuration
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("holograms." + hologram.name());
        if (section == null) {
            section = config.createSection("holograms." + hologram.name());
        }

        this.save(section, hologram);
        config.save(tempFile);

        // Delete the original holograms.yml file
        if (file.exists() && !file.delete()) {
            throw new Exception("Failed to delete original holograms file: " + file.getAbsolutePath());
        }

        // Replace the original file with the temporary file by renaming it
        if (!tempFile.renameTo(file)) {
            throw new Exception("Failed to rename temporary holograms file to: " + file.getAbsolutePath());
        }
    }

    public void shutdown() {
        this.shutdown = true;
        this.saveExecutor.shutdown();

        try {
            if (!this.saveExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                this.plugin.getSLF4JLogger().warn("Hologram saver did not terminate in time, forcing shutdown.");
                this.saveExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            this.saveExecutor.shutdownNow();
            Thread.currentThread().interrupt();
            this.plugin.getSLF4JLogger().warn("Hologram saver interrupted during shutdown: {}", e.getMessage(), e);
        }
    }

    public void save(ConfigurationSection section, Hologram hologram) {
        HologramType type = hologram.type();
        section.set("type", type.name());
        section.set("name", hologram.name());
        section.set("location", hologram.location());
        section.set("owner", hologram.owner().toString());

        HologramConfiguration configuration = hologram.configuration();

        saveVector(section, "scale.", configuration.scale());
        saveVector(section, "translation.", configuration.translation());

        section.set("billboard", configuration.billboard().name());
        section.set("shadow-radius", configuration.shadowRadius());
        section.set("shadow-strength", configuration.shadowStrength());
        section.set("visibility-distance", configuration.visibilityDistance());

        Display.Brightness brightness = configuration.brightness();
        if (brightness != null) {
            section.set("brightness.block", brightness.getBlockLight());
            section.set("brightness.sky", brightness.getSkyLight());
        }

        switch (type) {
            case BLOCK -> saveBlockConfiguration(section, (BlockHologramConfiguration) configuration);
            case ITEM -> saveItemConfiguration(section, (ItemHologramConfiguration) configuration);
            case TEXT -> saveTextConfiguration(section, (TextHologramConfiguration) configuration);
        }
    }

    private void saveBlockConfiguration(ConfigurationSection section, BlockHologramConfiguration configuration) {
        section.set("material", configuration.material().name());
    }

    private void saveItemConfiguration(ConfigurationSection section, ItemHologramConfiguration configuration) {
        section.set("itemstack", configuration.itemStack());
    }

    private void saveTextConfiguration(ConfigurationSection section, TextHologramConfiguration configuration) {
        section.set("text-background", backgroundColor(configuration.background()));
        section.set("text-alignment", configuration.textAlignment().name());
        section.set("see-through", configuration.seeThrough());
        section.set("text-shadow", configuration.textShadow());

        section.set("text", configuration.text());
    }

    private void saveVector(ConfigurationSection section, String path, Vector3f vector) {
        section.set(path + "x", vector.x());
        section.set(path + "y", vector.y());
        section.set(path + "z", vector.z());
    }

    private String backgroundColor(TextColor backgroundColor) {
        if (backgroundColor == null) {
            return "default";
        } else if (backgroundColor == Hologram.TRANSPARENT) {
            return "transparent";
        } else if (backgroundColor instanceof NamedTextColor namedTextColor) {
            return namedTextColor.toString();
        } else {
            return backgroundColor.asHexString();
        }
    }
}
