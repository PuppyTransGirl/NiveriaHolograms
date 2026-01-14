package toutouchien.niveriaholograms;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaholograms.commands.hologram.HologramCommand;
import toutouchien.niveriaholograms.commands.niveriaholograms.NiveriaHologramsCommand;
import toutouchien.niveriaholograms.listeners.HologramListener;
import toutouchien.niveriaholograms.managers.HologramManager;
import toutouchien.niveriaholograms.utils.CustomLocation;

import java.util.Arrays;

public class NiveriaHolograms extends JavaPlugin {
    private static NiveriaHolograms instance;

    private HologramManager hologramManager;

    static {
        ConfigurationSerialization.registerClass(CustomLocation.class, "CustomLocation");
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            Commands registrar = commands.registrar();
            Arrays.asList(
                    NiveriaHologramsCommand.get(),
                    HologramCommand.get()
            ).forEach(registrar::register);
        });

        Lang.load(this);

        (this.hologramManager = new HologramManager(this)).initialize();

        getServer().getPluginManager().registerEvents(new HologramListener(this), this);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);

        this.hologramManager.shutdown();
    }

    public void reload() {
        Lang.reload(this);
        this.hologramManager.reload();
    }

    public HologramManager hologramManager() {
        return hologramManager;
    }

    public static NiveriaHolograms instance() {
        return instance;
    }
}
