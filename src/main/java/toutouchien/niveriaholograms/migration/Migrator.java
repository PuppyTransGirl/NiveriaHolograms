package toutouchien.niveriaholograms.migration;

import it.unimi.dsi.fastutil.objects.ObjectList;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaholograms.core.Hologram;

public interface Migrator {
    @NotNull
    String name();

    boolean canRun();

    @NotNull
    ObjectList<Hologram> migrate(@NotNull Player player);
}
