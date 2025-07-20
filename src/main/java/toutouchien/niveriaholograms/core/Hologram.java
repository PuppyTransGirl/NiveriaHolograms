package toutouchien.niveriaholograms.core;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.PositionMoveRotation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import toutouchien.niveriaapi.utils.base.Task;
import toutouchien.niveriaapi.utils.game.NMSUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.HologramConfiguration;
import toutouchien.niveriaholograms.configurations.LeaderboardHologramConfiguration;
import toutouchien.niveriaholograms.configurations.TextHologramConfiguration;
import toutouchien.niveriaholograms.updater.HologramUpdater;
import toutouchien.niveriaholograms.utils.CustomLocation;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Hologram {
    public static final TextColor TRANSPARENT = TextColor.color(0);
    public static final int MAX_LINE_LENGTH = 1403;

    private static final ExecutorService EXECUTOR = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual()
            .name("NiveriaHolograms-Hologram-Sender-", 0)
            .factory()
    );
    private final HologramType type;
    private final HologramConfiguration config;
    private final String name;
    private final UUID owner;
    private Display display;
    private HologramUpdater updater;
    private CustomLocation location;
    private boolean locationDirty;

    private BukkitTask updateTask;

    public Hologram(HologramType type, HologramConfiguration config, String name, UUID owner, CustomLocation location) {
        this.type = type;
        this.config = config;
        this.name = name;
        this.location = location;
        this.owner = owner;
    }

    public Hologram(Hologram original, Player player, String newName) {
        this.type = original.type;
        this.name = newName;
        this.owner = original.owner;
        this.config = original.config.copy();
        this.location = new CustomLocation(player.getLocation());
    }

    public void create() {
        ServerLevel level = ((CraftWorld) location.bukkitLocation().getWorld()).getHandle();
        this.display = type.createDisplay(level);
        this.updater = type.createUpdater(display, config);

        display.setTransformationInterpolationDuration(1);
        display.setTransformationInterpolationDelay(0);

        updateLocation();
        update();

        if (config instanceof LeaderboardHologramConfiguration leaderboardConfig) {
            updateTask = Task.asyncRepeat(
                    this::updateForAllPlayers,
                    NiveriaHolograms.instance(),
                    Math.max(40L, leaderboardConfig.updateInterval()),
                    leaderboardConfig.updateInterval()
            );
        }

        if (config instanceof TextHologramConfiguration textConfig) {
            updateTask = Task.asyncRepeat(
                    this::updateForAllPlayers,
                    NiveriaHolograms.instance(),
                    Math.max(40L, textConfig.updateInterval()),
                    textConfig.updateInterval()
            );
        }
    }

    public void create(Player player) {
        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(display, 0, display.blockPosition());

        ClientboundTeleportEntityPacket teleportPacket = new ClientboundTeleportEntityPacket(
                display.getId(),
                PositionMoveRotation.of(display),
                Set.of(),
                false
        );

        if (display instanceof Display.TextDisplay textDisplay && config instanceof LeaderboardHologramConfiguration leaderboardConfiguration) {
            textDisplay.setText(PaperAdventure.asVanilla(leaderboardConfiguration.serializedText()));
        }

        if (display instanceof Display.TextDisplay textDisplay && config instanceof TextHologramConfiguration textConfig) {
            textDisplay.setText(PaperAdventure.asVanilla(textConfig.serializedText(player)));
        }

        // getNonDefaultValues sends less data than packAll
        // It is used when the player haven't received any data from the display yet
        List<SynchedEntityData.DataValue<?>> data = display.getEntityData().getNonDefaultValues();
        ClientboundSetEntityDataPacket dataPacket = data != null ? new ClientboundSetEntityDataPacket(display.getId(), data) : null;

        NMSUtils.sendNonNullPackets(player, addEntityPacket, teleportPacket, dataPacket);
    }

    public void createForAllPlayers() {
        List<Player> targets = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getWorld().getName().equals(location.world()))
                .collect(Collectors.toList());

        EXECUTOR.submit(() -> {
            for (Player player : targets) {
                this.create(player);
            }
        });
    }

    public void delete(Player player) {
        NMSUtils.sendPacket(player, new ClientboundRemoveEntitiesPacket(display.getId()));
    }

    public void deleteForAllPlayers(boolean worldChanged) {
        if (Bukkit.getOnlinePlayers().isEmpty())
            return;

        List<Player> targets = Bukkit.getOnlinePlayers().stream()
                .filter(p -> worldChanged || p.getWorld().getName().equals(location.world()))
                .collect(Collectors.toList());

        EXECUTOR.submit(() -> {
            for (Player player : targets) {
                this.delete(player);
            }
        });
    }

    public void update() {
        this.updater.update();
    }

    public void updateForAllPlayers() {
        if (Bukkit.getOnlinePlayers().isEmpty())
            return;

        ClientboundTeleportEntityPacket teleportPacket;
        if (locationDirty) {
            teleportPacket = new ClientboundTeleportEntityPacket(
                    display.getId(),
                    PositionMoveRotation.of(display),
                    Set.of(),
                    false
            );

            locationDirty = false;
        } else {
            teleportPacket = null;
        }

        List<Player> targets = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getWorld().getName().equals(location.world()))
                .collect(Collectors.toList());

        if (config instanceof LeaderboardHologramConfiguration leaderboardConfig && (leaderboardConfig.textDirty() || leaderboardConfig.updateIntervalDirty())) {
            if (updateTask != null && !updateTask.isCancelled()) {
                updateTask.cancel();
            }

            updateTask = Task.asyncRepeat(
                    this::updateForAllPlayers,
                    NiveriaHolograms.instance(),
                    Math.max(40L, leaderboardConfig.updateInterval()),
                    leaderboardConfig.updateInterval()
            );

            leaderboardConfig.updateIntervalDirty(false)
                    .textDirty(false);
        }

        if (config instanceof TextHologramConfiguration textConfig && (textConfig.textDirty() || textConfig.updateIntervalDirty())) {
            if (updateTask != null && !updateTask.isCancelled()) {
                updateTask.cancel();
            }

            updateTask = Task.asyncRepeat(
                    this::updateForAllPlayers,
                    NiveriaHolograms.instance(),
                    Math.max(40L, textConfig.updateInterval()),
                    textConfig.updateInterval()
            );

            textConfig.updateIntervalDirty(false)
                    .textDirty(false);
        }

        if (display instanceof Display.TextDisplay textDisplay && config instanceof LeaderboardHologramConfiguration leaderboardConfiguration) {
            textDisplay.setText(PaperAdventure.asVanilla(leaderboardConfiguration.serializedText()));
        }

        EXECUTOR.submit(() -> {
            for (Player player : targets) {
                if (display instanceof Display.TextDisplay textDisplay && config instanceof TextHologramConfiguration textConfig) {
                    textDisplay.setText(PaperAdventure.asVanilla(textConfig.serializedText(player)));
                }

                // packDirty sends only the dirty values, which is more efficient when updating
                // It's a lot more optimized than sending packAll everytime
                // Reduces packet size by approximately 93.44% per update on a default text hologram
                List<SynchedEntityData.DataValue<?>> data = display.getEntityData().packDirty();
                // This packet can't be created before because the text is unique to each player
                ClientboundSetEntityDataPacket dataPacket = data != null ? new ClientboundSetEntityDataPacket(display.getId(), data) : null;

                NMSUtils.sendNonNullPackets(player, teleportPacket, dataPacket);
            }
        });
    }

    private void updateLocation() {
        display.setPosRaw(location.x(), location.y(), location.z());
        display.setYRot(location.yaw()); // These are correct Y = Yaw, X = Pitch
        display.setXRot(location.pitch());
        locationDirty = true;
    }

    public void teleportTo(Location location) {
        boolean worldChanged = !this.location.world().equals(location.getWorld().getName());
        this.location = new CustomLocation(location);
        this.updateLocation();

        if (!worldChanged)
            return;

        this.deleteForAllPlayers(worldChanged);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().getName().equals(location.getWorld().getName()))
                continue;

            NMSUtils.sendPacket(player, new ClientboundTeleportEntityPacket(
                    display.getId(),
                    PositionMoveRotation.of(display),
                    Set.of(),
                    false
            ));
        }
    }

    public void editLocation(Consumer<CustomLocation> consumer) {
        if (consumer == null)
            return;

        consumer.accept(location);
        teleportTo(location.bukkitLocation());
        updateForAllPlayers();
        NiveriaHolograms.instance().hologramManager().saveHologram(this);
    }

    @SuppressWarnings("unchecked")
    public <T extends HologramConfiguration> void editConfig(Consumer<T> consumer) {
        if (consumer == null)
            return;

        consumer.accept((T) config);
        update();
        updateForAllPlayers();
        NiveriaHolograms.instance().hologramManager().saveHologram(this);
    }

    public HologramType type() {
        return type;
    }

    public HologramConfiguration configuration() {
        return config;
    }

    public String name() {
        return name;
    }

    public UUID owner() {
        return owner;
    }

    public CustomLocation location() {
        return location;
    }

    public Hologram location(CustomLocation location) {
        this.location = location;
        return this;
    }

    public Hologram copy() {
        return new Hologram(
                this.type,
                this.config.copy(),
                this.name,
                this.owner,
                this.location.copy()
        );
    }

    public void clearCache(Player player) {
        if (config instanceof TextHologramConfiguration textConfig) {
            textConfig.clearCache(player);
        }
    }
}