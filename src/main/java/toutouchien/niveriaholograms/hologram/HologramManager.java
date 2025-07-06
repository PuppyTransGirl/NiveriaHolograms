package toutouchien.niveriaholograms.hologram;

import net.kyori.adventure.text.Component;
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
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class HologramManager {
	private final NiveriaHolograms plugin;
	private final HologramLoader hologramLoader;

	private final List<Hologram> holograms;
	private final Map<String, List<String>> pendingHolograms;

	public HologramManager(NiveriaHolograms plugin) {
		this.plugin = plugin;
		this.hologramLoader = new HologramLoader(plugin);

		this.holograms = new CopyOnWriteArrayList<>();
		this.pendingHolograms = new HashMap<>();
	}

	public void initialize() {
		this.holograms.forEach(Hologram::deleteForAllPlayers);
		this.loadHolograms();
	}

	public Hologram createHologram(HologramType type, HologramConfiguration configuration, String name, CustomLocation location, UUID owner) {
		return new Hologram(type, configuration, name, location, owner);
	}

	public Hologram create(Player player, HologramType type, String name) {
		HologramConfiguration configuration = switch (type) {
			case BLOCK -> new BlockHologramConfiguration();
			case ITEM -> new ItemHologramConfiguration();
			case TEXT -> new TextHologramConfiguration();
		};

		Hologram hologram = createHologram(type, configuration, name, new CustomLocation(player.getLocation()), player.getUniqueId());

		if (type == HologramType.TEXT)
			((TextHologramConfiguration) configuration).text(Component.text("Utilise /holo edit " + name + " setline 1 <ton texte>"));

		hologram.create();
		hologram.createForAllPlayers();

		this.saveHologram(hologram);
		this.holograms.add(hologram);

		return hologram;
	}

	public void delete(Hologram hologram) {
		File file = new File(plugin.getDataFolder(), "holograms.yml");
		if (!file.exists())
			return;

		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if (!config.contains("holograms"))
			return;

		config.set("holograms." + hologram.name(), null);
		hologram.deleteForAllPlayers();
		this.holograms.remove(hologram);

		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		this.holograms.forEach(this::saveHologram);
	}

	public void loadHologram(ConfigurationSection section) {
		String worldName = ((CustomLocation) section.get("location")).world();
		if (Bukkit.getWorld(worldName) == null) {
			List<String> holograms = pendingHolograms.computeIfAbsent(worldName, w -> new ArrayList<>());
			holograms.add(section.getName());
			return;
		}

		Hologram hologram = hologramLoader.load(section);
		hologram.create();
		hologram.createForAllPlayers();
		this.holograms.add(hologram);
	}

	public void saveHologram(Hologram hologram) {
		File file = new File(plugin.getDataFolder(), "holograms.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		ConfigurationSection section = config.getConfigurationSection("holograms." + hologram.name());
		if (section == null)
			section = config.createSection("holograms." + hologram.name());

		hologramLoader.save(section, hologram);

		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Hologram hologramByName(String name) {
		return this.holograms.stream().filter(hologram -> hologram.name().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public List<Hologram> holograms() {
		return Collections.unmodifiableList(holograms);
	}

	public Map<String, List<String>> pendingHolograms() {
		return pendingHolograms;
	}

	public void reload() {
		initialize();
	}

	public void shutdown() {
//		saveHolograms();
	}
}
