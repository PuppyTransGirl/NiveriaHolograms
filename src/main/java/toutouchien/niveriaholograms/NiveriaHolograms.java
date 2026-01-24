package toutouchien.niveriaholograms;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.updatechecker.UpdateChecker;
import toutouchien.niveriaholograms.commands.hologram.HologramCommand;
import toutouchien.niveriaholograms.commands.niveriaholograms.NiveriaHologramsCommand;
import toutouchien.niveriaholograms.listeners.HologramListener;
import toutouchien.niveriaholograms.managers.HologramManager;
import toutouchien.niveriaholograms.utils.CustomLocation;

import java.util.Arrays;

public class NiveriaHolograms extends JavaPlugin {
    private static final int BSTATS_PLUGIN_ID = 29011;
    private static NiveriaHolograms instance;

    private HologramManager hologramManager;

    private Metrics bStats;

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

        saveDefaultConfig();

        Lang.load(this);

        (this.hologramManager = new HologramManager(this)).initialize();

        this.bStats = new Metrics(this, BSTATS_PLUGIN_ID);
        this.bStats.addCustomChart(new SingleLineChart("holograms_amount", () -> this.hologramManager.holograms().size()));


        getServer().getPluginManager().registerEvents(new HologramListener(this), this);

        new UpdateChecker(this, "j3tHqIoj", "niveriaholograms.new_update");
    }

    @Override
    public void onDisable() {
        this.bStats.shutdown();

        this.hologramManager.shutdown();

        getServer().getScheduler().cancelTasks(this);
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
