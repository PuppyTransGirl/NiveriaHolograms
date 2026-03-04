package toutouchien.niveriaholograms.nms;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import toutouchien.niveriaholograms.core.Hologram;

@NullMarked
public interface NMSAdapter {
    void sendToPlayer(Hologram hologram, Player player);

    void updateForAllPlayers(Hologram hologram);

    void deleteForPlayer(Hologram hologram, Player player);
}
