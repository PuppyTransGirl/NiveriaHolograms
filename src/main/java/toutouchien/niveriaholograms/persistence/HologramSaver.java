package toutouchien.niveriaholograms.persistence;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.util.Brightness;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.joml.Vector3f;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.*;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.core.HologramType;
import toutouchien.niveriaholograms.exceptions.HologramSaveException;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.*;

public class HologramSaver {
    private final NiveriaHolograms plugin;
    private final ExecutorService saveExecutor;
    private final Set<String> currentlySaving = ConcurrentHashMap.newKeySet();
    private final Set<String> currentlyDeleting = ConcurrentHashMap.newKeySet();
    private volatile boolean shutdown = false;

    public HologramSaver(NiveriaHolograms plugin) {
        this.plugin = plugin;
        ThreadFactory vtFactory = Thread.ofVirtual()
                .name("NiveriaHolograms-Saver-", 0)
                .factory();

        this.saveExecutor = Executors.newSingleThreadExecutor(vtFactory);
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

        Hologram snapshot = hologram.copy();

        this.saveExecutor.submit(() -> {
            try {
                this.saveToFile(snapshot);
            } catch (Exception e) {
                this.plugin.getSLF4JLogger().warn("Failed to save hologram {}: {}", name, e.getMessage(), e);
            } finally {
                // Clean up
                this.currentlySaving.remove(name);
            }
        });
    }

    public void deleteHologram(Hologram hologram) {
        if (shutdown) {
            return;
        }

        String name = hologram.name();

        // Skip if already deleting this hologram
        if (!currentlyDeleting.add(name)) {
            return;
        }

        hologram.deleteForAllPlayers(false);

        this.saveExecutor.submit(() -> {
            try {
                this.deleteFromFile(name);
            } catch (Exception e) {
                this.plugin.getSLF4JLogger().warn("Failed to delete hologram {}: {}", name, e.getMessage(), e);
            } finally {
                // Clean up
                this.currentlyDeleting.remove(name);
            }
        });
    }

    /**
     * Saves the hologram data to a YAML file using a temporary file.
     * This approach ensures that if the process is interrupted or fails midway,
     * only the temporary file may be corrupted, not the main holograms.yml file.
     */
    private void saveToFile(Hologram hologram) throws HologramSaveException {
        File file = new File(this.plugin.getDataFolder(), "holograms.yml");
        File tempFile = new File(file.getParent(), file.getName() + ".tmp");

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("holograms." + hologram.name());
        if (section == null) {
            section = config.createSection("holograms." + hologram.name());
        }

        this.save(section, hologram);
        try {
            config.save(tempFile);
        } catch (IOException e) {
            throw new HologramSaveException("Failed to save hologram to temporary file: " + tempFile.getAbsolutePath(), e);
        }

        // Delete the original holograms.yml file
        if (file.exists() && !file.delete()) {
            throw new HologramSaveException("Failed to delete original holograms file: " + file.getAbsolutePath());
        }

        // Replace the original file with the temporary file by renaming it
        if (!tempFile.renameTo(file)) {
            throw new HologramSaveException("Failed to rename temporary holograms file to: " + file.getAbsolutePath());
        }
    }

    /**
     * Deletes the hologram data from the YAML file using a temporary file.
     * This approach ensures that if the process is interrupted or fails midway,
     * only the temporary file may be corrupted, not the main holograms.yml file.
     */
    private void deleteFromFile(String hologramName) throws HologramSaveException {
        File file = new File(plugin.getDataFolder(), "holograms.yml");
        if (!file.exists()) {
            plugin.getSLF4JLogger().warn("Holograms file does not exist, nothing to delete: {}", file.getAbsolutePath());
            return;
        }

        File tempFile = new File(file.getParent(), file.getName() + ".tmp");

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.contains("holograms")) {
            plugin.getSLF4JLogger().warn("No holograms section found in the configuration file.");
            return;
        }

        config.set("holograms." + hologramName, null);
        try {
            config.save(tempFile);
        } catch (IOException e) {
            throw new HologramSaveException("Failed to save hologram deletion to temporary file: " + tempFile.getAbsolutePath(), e);
        }

        // Delete the original holograms.yml file
        if (file.exists() && !file.delete()) {
            throw new HologramSaveException("Failed to delete original holograms file: " + file.getAbsolutePath());
        }

        // Replace the original file with the temporary file by renaming it
        if (!tempFile.renameTo(file)) {
            throw new HologramSaveException("Failed to rename temporary holograms file to: " + file.getAbsolutePath());
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

        Brightness brightness = configuration.brightness();
        if (brightness != null) {
            section.set("brightness.block", brightness.block());
            section.set("brightness.sky", brightness.sky());
        }

        switch (type) {
            case BLOCK -> saveBlockConfiguration(section, (BlockHologramConfiguration) configuration);
            case ITEM -> saveItemConfiguration(section, (ItemHologramConfiguration) configuration);
            case LEADERBOARD -> saveLeaderboardConfiguration(section, (LeaderboardHologramConfiguration) configuration);
            case TEXT -> saveTextConfiguration(section, (TextHologramConfiguration) configuration);
        }
    }

    private void saveBlockConfiguration(ConfigurationSection section, BlockHologramConfiguration configuration) {
        section.set("material", configuration.material().name());
        section.set("glowing", glowingColor(configuration.glowingColor()));
    }

    private void saveItemConfiguration(ConfigurationSection section, ItemHologramConfiguration configuration) {
        section.set("itemstack", configuration.itemStack());
        section.set("glowing", glowingColor(configuration.glowingColor()));
    }

    private void saveLeaderboardConfiguration(ConfigurationSection section, LeaderboardHologramConfiguration configuration) {
        section.set("text-background", backgroundColor(configuration.background()));
        section.set("see-through", configuration.seeThrough());
        section.set("text-shadow", configuration.textShadow());
        section.set("update-interval", configuration.updateInterval());

        section.set("placeholder", configuration.placeholder());
        section.set("main-color", color(configuration.mainColor()));
        section.set("first-color", color(configuration.firstColor()));
        section.set("second-color", color(configuration.secondColor()));
        section.set("third-color", color(configuration.thirdColor()));
        section.set("other-color", color(configuration.otherColor()));
        section.set("value-color", color(configuration.valueColor()));
        section.set("suffix-color", color(configuration.suffixColor()));
        section.set("title", configuration.title());
        section.set("suffix", configuration.suffix());
        section.set("show-suffix", configuration.showSuffix());
        section.set("show-empty-places", configuration.showEmptyPlaces());
        section.set("max-lines", configuration.maxLines());
        section.set("reverse-order", configuration.reverseOrder());
    }

    private void saveTextConfiguration(ConfigurationSection section, TextHologramConfiguration configuration) {
        section.set("text-background", backgroundColor(configuration.background()));
        section.set("text-alignment", configuration.textAlignment().name());
        section.set("see-through", configuration.seeThrough());
        section.set("text-shadow", configuration.textShadow());
        section.set("update-interval", configuration.updateInterval());

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
        }

        if (backgroundColor == Hologram.TRANSPARENT) {
            return "transparent";
        }

        if (backgroundColor instanceof NamedTextColor namedTextColor) {
            return namedTextColor.toString();
        }

        return backgroundColor.asHexString();
    }

    private String color(TextColor color) {
        if (color == null) {
            return "default";
        }

        if (color instanceof NamedTextColor namedTextColor) {
            return namedTextColor.toString();
        }

        return color.asHexString();
    }

    private String glowingColor(TextColor glowingColor) {
        // "default" is NamedTextColor.WHITE so NamedTextColor case is used for "default"
        return switch (glowingColor) {
            case null -> "none";
            case NamedTextColor namedTextColor -> namedTextColor.toString();
            default -> glowingColor.asHexString();
        };
    }
}
