package toutouchien.niveriaholograms;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import toutouchien.niveriaapi.NiveriaAPI;
import toutouchien.niveriaholograms.command.hologram.HologramCommand;
import toutouchien.niveriaholograms.command.niveriaholograms.NiveriaHologramsCommand;
import toutouchien.niveriaholograms.hologram.HologramManager;
import toutouchien.niveriaholograms.listeners.HologramListener;
import toutouchien.niveriaholograms.utils.CustomLocation;

import java.util.Arrays;

public class NiveriaHolograms extends JavaPlugin {
	private static NiveriaHolograms INSTANCE;

	private HologramManager hologramManager;

	static {
		ConfigurationSerialization.registerClass(CustomLocation.class, "CustomLocation");
	}

	@Override
	public void onEnable() {
		INSTANCE = this;

		(this.hologramManager = new HologramManager(this)).initialize();

		NiveriaAPI.instance().commandManager().registerCommands(Arrays.asList(
				new NiveriaHologramsCommand(),

				new HologramCommand()
		));

		getServer().getPluginManager().registerEvents(new HologramListener(this), this);
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);

		this.hologramManager.shutdown();
	}

	public void reload() {
		this.hologramManager.reload();
	}

	public HologramManager hologramManager() {
		return hologramManager;
	}

	public static NiveriaHolograms instance() {
		return INSTANCE;
	}
}
