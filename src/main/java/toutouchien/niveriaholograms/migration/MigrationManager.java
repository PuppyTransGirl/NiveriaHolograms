package toutouchien.niveriaholograms.migration;

import it.unimi.dsi.fastutil.objects.ObjectList;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.Task;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;
import toutouchien.niveriaholograms.migration.migrators.DecentHologramsMigrator;
import toutouchien.niveriaholograms.migration.migrators.FancyHologramsV2Migrator;

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

public class MigrationManager {
    private final NiveriaHolograms plugin;
    private final HologramManager hologramManager;

    public MigrationManager(NiveriaHolograms plugin, HologramManager hologramManager) {
        this.plugin = plugin;
        this.hologramManager = hologramManager;
    }

    public void convertFromDecentHolograms(@NotNull Player player) {
        DecentHologramsMigrator migrator = new DecentHologramsMigrator();
        if (!migrator.canRun()) {
            LANG.sendMessage(player, "niveriaholograms.migrator.decentholograms.cannot_run");
            return;
        }

        Task.async(task -> {
            ObjectList<Hologram> migratedHolograms = migrator.migrate(player);
            for (Hologram hologram : migratedHolograms) {
                hologram.create();
                hologram.createForAllPlayers();

                this.hologramManager.saveHologram(hologram);
                this.hologramManager.addHologram(hologram);
            }

            LANG.sendMessage(player, "niveriaholograms.migrator.decentholograms.migrated",
                    Lang.numberPlaceholder("niveriaholograms_migrated_amount", migratedHolograms.size())
            );
        }, this.plugin);
    }

    public void convertFromFancyHologramsV2(@NotNull Player player) {
        FancyHologramsV2Migrator migrator = new FancyHologramsV2Migrator();
        if (!migrator.canRun()) {
            LANG.sendMessage(player, "niveriaholograms.migrator.fancyholograms.cannot_run");
            return;
        }

        Task.async(task -> {
            ObjectList<Hologram> migratedHolograms = migrator.migrate(player);
            for (Hologram hologram : migratedHolograms) {
                hologram.create();
                hologram.createForAllPlayers();

                this.hologramManager.saveHologram(hologram);
                this.hologramManager.addHologram(hologram);
            }

            LANG.sendMessage(player, "niveriaholograms.migrator.fancyholograms.migrated",
                    Lang.numberPlaceholder("niveriaholograms_migrated_amount", migratedHolograms.size())
            );
        }, this.plugin);
    }
}
