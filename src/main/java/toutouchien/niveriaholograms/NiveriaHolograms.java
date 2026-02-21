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
import toutouchien.niveriaholograms.migration.MigrationManager;
import toutouchien.niveriaholograms.utils.CustomLocation;

import java.util.List;

public class NiveriaHolograms extends JavaPlugin {
    private static final int BSTATS_PLUGIN_ID = 29011;
    private static NiveriaHolograms instance;

    public static Lang LANG;

    private HologramManager hologramManager;
    private MigrationManager migrationManager;

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
            registrar.register(NiveriaHologramsCommand.get());
            registrar.register(HologramCommand.get(), List.of("holo", "nholo"));
        });

        LANG = Lang.builder(this)
                .addDefaultLanguageFiles("en_US.yml", "fr_FR.yml")
                .build();

        saveDefaultConfig();

        (this.hologramManager = new HologramManager(this)).initialize();
        this.migrationManager = new MigrationManager(this, this.hologramManager);

        this.bStats = new Metrics(this, BSTATS_PLUGIN_ID);
        this.bStats.addCustomChart(new SingleLineChart("holograms_amount", () -> this.hologramManager.holograms().size()));

        getServer().getPluginManager().registerEvents(new HologramListener(hologramManager, this.getDataFolder()), this);

        new UpdateChecker(this, "j3tHqIoj");
    }

    @Override
    public void onDisable() {
        this.bStats.shutdown();

        this.hologramManager.shutdown();

        getServer().getScheduler().cancelTasks(this);
    }

    public void reload() {
        this.getSLF4JLogger().info("Reloading NiveriaAPI...");

        this.reloadConfig();
        LANG.reload();

        this.hologramManager.reload();

        this.getSLF4JLogger().info("NiveriaAPI reloaded.");
    }

    public HologramManager hologramManager() {
        return hologramManager;
    }

    public MigrationManager migrationManager() {
        return migrationManager;
    }

    public static NiveriaHolograms instance() {
        return instance;
    }
}
