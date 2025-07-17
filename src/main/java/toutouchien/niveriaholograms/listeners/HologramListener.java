package toutouchien.niveriaholograms.listeners;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.hologram.Hologram;
import toutouchien.niveriaholograms.hologram.HologramManager;

import java.io.File;
import java.util.List;
import java.util.Map;

public class HologramListener implements Listener {
	private final NiveriaHolograms plugin;
	private final HologramManager hologramManager;

	public HologramListener(NiveriaHolograms plugin) {
		this.plugin = plugin;
		this.hologramManager = plugin.hologramManager();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		sendHolograms(event.getPlayer());
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		sendHolograms(event.getPlayer());
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		String worldName = event.getWorld().getName();
		Map<String, List<String>> pendingHolograms = this.hologramManager.pendingHolograms();
		if (!pendingHolograms.containsKey(worldName))
			return;

		File file = new File(plugin.getDataFolder(), "holograms.yml");
		if (!file.exists())
			return;

		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if (!config.contains("holograms"))
			return;

		ConfigurationSection hologramsSection = config.getConfigurationSection("holograms");
		for (String hologramName : pendingHolograms.remove(worldName)) {
			ConfigurationSection section = hologramsSection.getConfigurationSection(hologramName);
			if (section == null)
				return;

			this.hologramManager.loadHologram(section);
		}
	}

	private void sendHolograms(Player player) {
		for (Hologram hologram : this.hologramManager.holograms()) {
			if (hologram.location().world().equals(player.getWorld().getName())) {
				hologram.create(player);
			} else {
				hologram.delete(player);
			}
		}
	}
}
