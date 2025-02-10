package toutouchien.niveriaholograms;

import org.bukkit.plugin.java.JavaPlugin;
import toutouchien.niveriaapi.NiveriaAPI;
import toutouchien.niveriaholograms.hologram.HologramManager;
import toutouchien.niveriaholograms.listeners.ServerStartListener;

public class NiveriaHolograms extends JavaPlugin {
	private static NiveriaHolograms INSTANCE;

	private HologramManager hologramManager;

	@Override
	public void onEnable() {
		INSTANCE = this;

		(this.hologramManager = new HologramManager(this)).initialize();

		NiveriaAPI.instance().commandManager().registerCommand(
				new toutouchien.niveriaholograms.command.niveriaholograms.NiveriaHolograms(this)
		);

		getServer().getPluginManager().registerEvents(new ServerStartListener(this), this);
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);

		this.hologramManager.shutdown();
	}

	public void reload() {

	}

	public HologramManager hologramManager() {
		return hologramManager;
	}

	public static NiveriaHolograms instance() {
		return INSTANCE;
	}
}
