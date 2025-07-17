package toutouchien.niveriaholograms.hologram;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configuration.BlockHologramConfiguration;
import toutouchien.niveriaholograms.configuration.HologramConfiguration;
import toutouchien.niveriaholograms.configuration.ItemHologramConfiguration;
import toutouchien.niveriaholograms.configuration.TextHologramConfiguration;
import toutouchien.niveriaholograms.utils.CustomLocation;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HologramManager {
    private final NiveriaHolograms plugin;
    private final HologramLoader hologramLoader;
    private final HologramSaver hologramSaver;

    private final Map<String, Hologram> holograms;
    private final Map<String, List<String>> pendingHolograms;

    public HologramManager(NiveriaHolograms plugin) {
        this.plugin = plugin;
        this.hologramLoader = new HologramLoader(plugin);
        this.hologramSaver = new HologramSaver(plugin);

        this.holograms = new ConcurrentHashMap<>();
        this.pendingHolograms = new HashMap<>();
    }

    public void initialize() {
        for (Hologram hologram : this.holograms.values()) {
            hologram.deleteForAllPlayers(false);
        }

        this.loadHolograms();
    }

    public Hologram createHologram(HologramType type, HologramConfiguration configuration, String name, UUID owner, CustomLocation location) {
        return new Hologram(type, configuration, name, owner, location);
    }

    public Hologram create(Player player, HologramType type, String name) {
        HologramConfiguration configuration = switch (type) {
            case BLOCK -> new BlockHologramConfiguration();
            case ITEM -> new ItemHologramConfiguration();
            case TEXT -> new TextHologramConfiguration();
        };

        Hologram hologram = createHologram(type, configuration, name, player.getUniqueId(), new CustomLocation(player.getLocation()));

        if (type == HologramType.TEXT)
            ((TextHologramConfiguration) configuration).text(new ArrayList<>(List.of("Utilise /holo edit " + name + " setline 1 <ton texte>")));

        hologram.create();
        hologram.createForAllPlayers();

        this.saveHologram(hologram);
        this.addHologram(hologram);

        return hologram;
    }

    public void delete(Hologram hologram) {
        this.holograms.remove(hologram);
        this.hologramSaver.deleteHologram(hologram);
    }

    public void addHologram(Hologram hologram) {
        this.holograms.put(hologram.name(), hologram);
    }

    public void loadHolograms() {
        this.holograms.clear();

        File file = new File(plugin.getDataFolder(), "holograms.yml");
        if (!file.exists())
            return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.contains("holograms"))
            return;

        ConfigurationSection hologramsSection = config.getConfigurationSection("holograms");
        for (String key : hologramsSection.getKeys(false)) {
            ConfigurationSection section = hologramsSection.getConfigurationSection(key);
            if (section == null)
                continue;

            this.loadHologram(section);
        }
    }

    public void saveHolograms() {
        for (Hologram hologram : this.holograms.values()) {
            this.saveHologram(hologram);
        }
    }

    public void loadHologram(ConfigurationSection section) {
        String worldName = ((CustomLocation) section.get("location")).world();
        if (Bukkit.getWorld(worldName) == null) {
            List<String> loadedHolograms = pendingHolograms.computeIfAbsent(worldName, w -> new ArrayList<>());
            loadedHolograms.add(section.getName());
            return;
        }

        Hologram hologram = hologramLoader.load(section);
        hologram.create();
        hologram.createForAllPlayers();
        this.holograms.put(hologram.name(), hologram);
    }

    public void saveHologram(Hologram hologram) {
        this.hologramSaver.saveHologram(hologram);
    }

    public Hologram hologramByName(String name) {
        return this.holograms.get(name);
    }

    public boolean hologramExists(String name) {
        for (String holoName : holograms.keySet()) {
            if (holoName.equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public List<Hologram> holograms() {
        return new ArrayList<>(holograms.values());
    }

    public Map<String, List<String>> pendingHolograms() {
        return pendingHolograms;
    }

    public void reload() {
        initialize();
    }

    public void shutdown() {
        this.hologramSaver.shutdown();
    }
}
