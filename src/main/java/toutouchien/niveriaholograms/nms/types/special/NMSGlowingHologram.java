package toutouchien.niveriaholograms.nms.types.special;

import org.jspecify.annotations.NullMarked;
import toutouchien.niveriaholograms.core.Hologram;

@NullMarked
public interface NMSGlowingHologram extends NMSDisplayHologram {
    void updateHologram(Hologram hologram);
}
