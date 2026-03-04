package toutouchien.niveriaholograms;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.updatechecker.UpdateChecker;
import toutouchien.niveriaholograms.commands.NiveriaHologramsCommand;
import toutouchien.niveriaholograms.nms.NMSManager;

public class NiveriaHolograms extends JavaPlugin {
    private static final String MODRINTH_PROJECT_ID = "j3tHqIoj";
    private static final int BSTATS_PLUGIN_ID = 29011;

    private static NiveriaHolograms instance;

    @SuppressWarnings({"java:S1104", "java:S1444", "java:S3008"})
    public static Lang LANG;

    private NMSManager nmsManager;

    private Metrics bStats;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.bStats = new Metrics(this, BSTATS_PLUGIN_ID);

        LANG = Lang.builder(this)
                .addDefaultLanguageFiles("en_US.yml", "fr_FR.yml")
                .build();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            Commands registrar = commands.registrar();
            registrar.register(NiveriaHologramsCommand.get());
        });

        this.nmsManager = new NMSManager(this);

        if (this.getConfig().getBoolean("update-checker.enabled", true))
            new UpdateChecker(this, MODRINTH_PROJECT_ID);
    }

    public void reload() {
        this.getSLF4JLogger().info("Reloading NiveriaHolograms...");

        this.reloadConfig();
        LANG.reload();

        this.getSLF4JLogger().info("NiveriaHolograms reloaded.");
    }

    @Override
    public void onDisable() {
        this.bStats.shutdown();

        Bukkit.getScheduler().cancelTasks(this);
    }

    public NMSManager nmsManager() {
        return this.nmsManager;
    }

    public static NiveriaHolograms instance() {
        return instance;
    }
}
