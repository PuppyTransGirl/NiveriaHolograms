package toutouchien.niveriaholograms.core;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.bukkit.Location;

import java.util.UUID;

public class Hologram {
    private final HologramType type;
    private final String id;
    private final UUID owner;
    private ObjectSet<HologramEntity> entities;
    private Location location;

    public Hologram(HologramType type, String id, UUID owner) {
        this.type = type;
        this.id = id;
        this.owner = owner;
    }
}
