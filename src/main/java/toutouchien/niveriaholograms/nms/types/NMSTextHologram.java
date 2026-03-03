package toutouchien.niveriaholograms.nms.types;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.nms.types.special.NMSDisplayHologram;

@NullMarked
public interface NMSTextHologram extends NMSDisplayHologram {
    void createHologram(Hologram hologram);

    void updateHologram(Hologram hologram);

    void updateHologramForPlayer(Hologram hologram, Player player);
}
