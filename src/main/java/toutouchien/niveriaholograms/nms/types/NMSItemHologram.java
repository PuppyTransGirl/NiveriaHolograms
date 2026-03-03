package toutouchien.niveriaholograms.nms.types;

import org.jspecify.annotations.NullMarked;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.nms.types.special.NMSGlowingHologram;

@NullMarked
public interface NMSItemHologram extends NMSGlowingHologram {
    void createHologram(Hologram hologram);

    void updateHologram(Hologram hologram);
}
