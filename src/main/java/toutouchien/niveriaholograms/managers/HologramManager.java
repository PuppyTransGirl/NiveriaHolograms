package toutouchien.niveriaholograms.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.HologramConfiguration;
import toutouchien.niveriaholograms.configurations.TextHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.core.HologramType;
import toutouchien.niveriaholograms.persistence.HologramLoader;
import toutouchien.niveriaholograms.persistence.HologramSaver;
import toutouchien.niveriaholograms.utils.CustomLocation;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

public class HologramManager {
    private final NiveriaHolograms plugin;
    private final HologramLoader hologramLoader;
    private final HologramSaver hologramSaver;

    private final Map<String, Hologram> holograms;
    private final Map<String, List<String>> pendingHolograms;

    public HologramManager(NiveriaHolograms plugin) {
        this.plugin = plugin;
        this.hologramLoader = new HologramLoader(this);
        this.hologramSaver = new HologramSaver(plugin.getDataFolder(), plugin.getSLF4JLogger());

        this.holograms = new ConcurrentHashMap<>();
        this.pendingHolograms = new ConcurrentHashMap<>();
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
        HologramConfiguration configuration = type.createConfiguration();
        Hologram hologram = createHologram(type, configuration, name, player.getUniqueId(), new CustomLocation(player.getLocation()));

        if (type == HologramType.TEXT)
            ((TextHologramConfiguration) configuration).text(List.of(LANG.getString("niveriaholograms.default_text").replace("<niveriaholograms_hologram_name>", name)));

        hologram.create();
        hologram.createForAllPlayers();

        this.saveHologram(hologram);
        this.addHologram(hologram);

        return hologram;
    }

    public void cloneHologram(Hologram hologram, Player player, String newHologramName) {
        Hologram clone = new Hologram(hologram, player, newHologramName);
        clone.create();
        clone.createForAllPlayers();

        this.saveHologram(clone);
        this.addHologram(clone);
    }

    public void delete(Hologram hologram) {
        this.holograms.remove(hologram.name().toLowerCase(Locale.ROOT));
        this.hologramSaver.deleteHologram(hologram);
    }

    public void addHologram(Hologram hologram) {
        this.holograms.put(hologram.name().toLowerCase(Locale.ROOT), hologram);
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

    public void loadHologram(ConfigurationSection section) {
        String worldName = section.getObject("location", CustomLocation.class).world();
        if (Bukkit.getWorld(worldName) == null) {
            pendingHolograms.computeIfAbsent(worldName, w -> Collections.synchronizedList(new ArrayList<>()))
                    .add(section.getName());
            return;
        }

        Hologram hologram = hologramLoader.load(section);
        hologram.create();
        hologram.createForAllPlayers();
        this.holograms.put(hologram.name().toLowerCase(Locale.ROOT), hologram);
    }

    public void saveHologram(Hologram hologram) {
        this.hologramSaver.saveHologram(hologram);
    }

    public void clearCache(Player player) {
        for (Hologram hologram : this.holograms.values())
            hologram.clearCache(player);
    }

    public Hologram hologramByName(String name) {
        return this.holograms.get(name.toLowerCase(Locale.ROOT));
    }

    public boolean hologramExists(String name) {
        return this.hologramByName(name) != null;
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
