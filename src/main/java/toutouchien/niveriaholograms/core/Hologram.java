package toutouchien.niveriaholograms.core;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import toutouchien.niveriaholograms.config.special.HologramConfig;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@NullMarked
public class Hologram {
    private static final ExecutorService EXECUTOR = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual()
                    .name("NiveriaHolograms-Hologram-Sender-", 0)
                    .factory()
    );

    private final HologramType type;
    private final HologramConfig config;
    private final String id;
    private final UUID owner;
    private Object entity;
    private Location location;

    @Nullable private ScheduledTask updateTask;

    private boolean locationDirty;

    public Hologram(HologramType type, HologramConfig config, String id, UUID owner, Location location) {
        this.type = type;
        this.config = config;
        this.id = id;
        this.owner = owner;
        this.location = location;
    }

    public <T> T entity(Class<T> clazz) {
        return clazz.cast(this.entity);
    }

    public HologramConfig config() {
        return this.config;
    }

    public <T> T config(Class<T> clazz) {
        return clazz.cast(this.config);
    }

    public Location location() {
        return this.location.clone();
    }

    public boolean locationDirty() {
        return this.locationDirty;
    }

    public Hologram entity(Object entity) {
        this.entity = entity;
        return this;
    }

    public Hologram updateTask(@Nullable ScheduledTask updateTask) {
        if (this.updateTask != null && !this.updateTask.isCancelled())
            this.updateTask.cancel();

        this.updateTask = updateTask;
        return this;
    }

    public Hologram locationDirty(boolean locationDirty) {
        this.locationDirty = locationDirty;
        return this;
    }

    public static ExecutorService hologramSender() {
        return EXECUTOR;
    }
}
