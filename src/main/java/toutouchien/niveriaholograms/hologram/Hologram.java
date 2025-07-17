package toutouchien.niveriaholograms.hologram;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PositionMoveRotation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import toutouchien.niveriaapi.utils.game.NMSUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configuration.BlockHologramConfiguration;
import toutouchien.niveriaholograms.configuration.HologramConfiguration;
import toutouchien.niveriaholograms.configuration.ItemHologramConfiguration;
import toutouchien.niveriaholograms.configuration.TextHologramConfiguration;
import toutouchien.niveriaholograms.updater.BlockHologramUpdater;
import toutouchien.niveriaholograms.updater.HologramUpdater;
import toutouchien.niveriaholograms.updater.ItemHologramUpdater;
import toutouchien.niveriaholograms.updater.TextHologramUpdater;
import toutouchien.niveriaholograms.utils.CustomLocation;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Hologram {
    public static final TextColor TRANSPARENT = TextColor.color(0);
    public static final int MAX_LINE_LENGTH = 1403;

    private static final ThreadFactory VIRTUAL_THREAD_FACTORY = Thread.ofVirtual()
            .name("NiveriaHolograms-Hologram-Sender-", 0)
            .factory();

    private static final ExecutorService EXECUTOR = Executors.newThreadPerTaskExecutor(VIRTUAL_THREAD_FACTORY);

    private Display display;
    private final HologramType type;
    private final HologramConfiguration config;
    private HologramUpdater updater;
    private final String name;
    private final UUID owner;
    private CustomLocation location;
    private boolean locationDirty;

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
        this.display = switch (type) {
            case BLOCK -> new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level);
            case ITEM -> new Display.ItemDisplay(EntityType.ITEM_DISPLAY, level);
            case TEXT -> new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
        };

        this.updater = switch (type) {
            case BLOCK -> new BlockHologramUpdater((Display.BlockDisplay) display, (BlockHologramConfiguration) config);
            case ITEM -> new ItemHologramUpdater((Display.ItemDisplay) display, (ItemHologramConfiguration) config);
            case TEXT -> new TextHologramUpdater((Display.TextDisplay) display, (TextHologramConfiguration) config);
        };

        display.setTransformationInterpolationDuration(1);
        display.setTransformationInterpolationDelay(0);

        updateLocation();
        update();
    }

    public void create(Player player) {
        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(display, 0, display.blockPosition());

        ClientboundTeleportEntityPacket teleportPacket = new ClientboundTeleportEntityPacket(
                display.getId(),
                PositionMoveRotation.of(display),
                Set.of(),
                false
        );

        // getNonDefaultValues sends less data than packAll
        // It is used when the player haven't received any data from the display yet
        List<SynchedEntityData.DataValue<?>> data = display.getEntityData().getNonDefaultValues();
        ClientboundSetEntityDataPacket dataPacket = data != null ? new ClientboundSetEntityDataPacket(display.getId(), data) : null;

        if (display instanceof Display.TextDisplay textDisplay)
            textDisplay.setText(PaperAdventure.asVanilla(((TextHologramConfiguration) config).serializedText()));

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
        if (display instanceof Display.TextDisplay textDisplay)
            textDisplay.setText(PaperAdventure.asVanilla(((TextHologramConfiguration) config).serializedText()));

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

        // packDirty sends only the dirty values, which is more efficient when updating
        // It's a lot more optimized than sending packAll everytime
        // Reduces packet size by approximately 93.44% per update on a default text hologram
        List<SynchedEntityData.DataValue<?>> data = display.getEntityData().packDirty();
        ClientboundSetEntityDataPacket dataPacket = data != null ? new ClientboundSetEntityDataPacket(display.getId(), data) : null;

        List<Player> targets = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getWorld().getName().equals(location.world()))
                .collect(Collectors.toList());

        EXECUTOR.submit(() -> {
            for (Player player : targets) {
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
}