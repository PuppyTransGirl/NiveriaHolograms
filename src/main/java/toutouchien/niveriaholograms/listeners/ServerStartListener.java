package toutouchien.niveriaholograms.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import toutouchien.niveriaholograms.NiveriaHolograms;

public class ServerStartListener implements Listener {
	private final NiveriaHolograms plugin;

	public ServerStartListener(NiveriaHolograms plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onServerLoad(ServerLoadEvent event) {
		this.plugin.hologramManager().loadHolograms();
	}
}
