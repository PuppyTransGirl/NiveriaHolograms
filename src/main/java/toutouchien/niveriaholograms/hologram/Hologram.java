package toutouchien.niveriaholograms.hologram;

import com.mojang.math.Transformation;
import io.netty.buffer.Unpooled;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.joml.Quaternionf;
import toutouchien.niveriaholograms.configuration.BlockHologramConfiguration;
import toutouchien.niveriaholograms.configuration.HologramConfiguration;
import toutouchien.niveriaholograms.configuration.ItemHologramConfiguration;
import toutouchien.niveriaholograms.configuration.TextHologramConfiguration;
import toutouchien.niveriaholograms.utils.CustomLocation;
import toutouchien.niveriaholograms.utils.ReflectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Hologram {
    private static final ConcurrentHashMap<UUID, PacketStats> playerPacketStats = new ConcurrentHashMap<>();
    public static final TextColor TRANSPARENT = () -> 0;
    private Display display = null;

    private HologramType type;
    private HologramConfiguration configuration;
    private String name;
    private CustomLocation location;
    private UUID owner;

    public Hologram(HologramType type, HologramConfiguration configuration, String name, CustomLocation location, UUID owner) {
        this.type = type;
        this.configuration = configuration;
        this.name = name;
        this.location = location;
        this.owner = owner;
    }

    public Hologram(Hologram original, Player player, String newName) {
        this.type = original.type;
        this.name = newName;
        this.owner = original.owner;
        this.location = new CustomLocation(player.getLocation());
        this.configuration = original.configuration.clone();
        this.display = null;
    }

    public void createForAllPlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!player.getWorld().getName().equals(location.world()))
                return;

            this.create(player);
        });
    }

    public void deleteForAllPlayers() {
        Bukkit.getOnlinePlayers().forEach(this::delete);
    }

    public void updateForAllPlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> update(player, false));
    }

    public void create(Player player) {
        send(player, new ClientboundAddEntityPacket(display, 0, display.blockPosition()));
        update(player, true);
    }

    public void delete(Player player) {
        send(player, new ClientboundRemoveEntitiesPacket(display.getId()));
    }

    public void update(Player player, boolean join) {
        send(player, new ClientboundTeleportEntityPacket(display.getId(), PositionMoveRotation.of(display), Set.of(), false));

        if (display instanceof Display.TextDisplay textDisplay)
            textDisplay.setText(PaperAdventure.asVanilla(((TextHologramConfiguration) configuration).serializedText()));

        List<SynchedEntityData.DataValue<?>> values = join ? display.getEntityData().packAll() : display.getEntityData().packDirty();
        if (values == null)
            return;

        send(player, new ClientboundSetEntityDataPacket(display.getId(), values));
    }

    public void create() {
        ServerLevel level = ((CraftWorld) location.bukkitLocation().getWorld()).getHandle();
        switch (type) {
            case BLOCK -> this.display = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level);
            case ITEM -> this.display = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, level);
            case TEXT -> this.display = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
        }

        display.setTransformationInterpolationDuration(1);
        display.setTransformationInterpolationDelay(0);

        updateLocation();
        update();
    }

    public void updateLocation() {
        display.setPosRaw(location.x(), location.y(), location.z());
        display.setYRot(location.yaw()); // These are correct Y = Yaw, X = Pitch
        display.setXRot(location.pitch());
    }

    public void update() {
        display.setBillboardConstraints(switch (configuration.billboard()) {
            case FIXED -> Display.BillboardConstraints.FIXED;
            case VERTICAL -> Display.BillboardConstraints.VERTICAL;
            case HORIZONTAL -> Display.BillboardConstraints.HORIZONTAL;
            case CENTER -> Display.BillboardConstraints.CENTER;
        });

        if (display instanceof Display.BlockDisplay blockDisplay && configuration instanceof BlockHologramConfiguration blockConfiguration) {
            ResourceLocation blockResource = ResourceLocation.parse(blockConfiguration.material().key().asString());
            Optional<Holder.Reference<Block>> blockHolder = BuiltInRegistries.BLOCK.get(blockResource);
            if (blockHolder.isEmpty())
                throw new IllegalArgumentException("Invalid block material: " + blockResource);

            Block block = blockHolder.get().value();
            blockDisplay.setBlockState(block.defaultBlockState());
        } else if (display instanceof Display.ItemDisplay itemDisplay && configuration instanceof ItemHologramConfiguration itemConfiguration) {
            itemDisplay.setItemStack(ItemStack.fromBukkitCopy(itemConfiguration.itemStack()));
        } else if (display instanceof Display.TextDisplay textDisplay && configuration instanceof TextHologramConfiguration textConfiguration) {
            display.getEntityData().set(Display.TextDisplay.DATA_LINE_WIDTH_ID, 1403);

            TextColor background = textConfiguration.background();
            int newBackground = background == null ? Display.TextDisplay.INITIAL_BACKGROUND : background == TRANSPARENT ? 0 : background.value() | 0xC8000000;
            display.getEntityData().set(Display.TextDisplay.DATA_BACKGROUND_COLOR_ID, newBackground);

            byte flags = textDisplay.getFlags();
            flags = (byte) (textConfiguration.textShadow() ? flags | Display.TextDisplay.FLAG_SHADOW : (flags & ~Display.TextDisplay.FLAG_SHADOW));
            flags = (byte) (textConfiguration.textAlignment() == TextDisplay.TextAlignment.LEFT ? (flags | Display.TextDisplay.FLAG_ALIGN_LEFT) : (flags & ~Display.TextDisplay.FLAG_ALIGN_LEFT));
            flags = (byte) (textConfiguration.seeThrough() ? flags | Display.TextDisplay.FLAG_SEE_THROUGH : (flags & ~Display.TextDisplay.FLAG_SEE_THROUGH));
            flags = (byte) (textConfiguration.textAlignment() == TextDisplay.TextAlignment.RIGHT ? (flags | Display.TextDisplay.FLAG_ALIGN_RIGHT) : (flags & ~Display.TextDisplay.FLAG_ALIGN_RIGHT));
            textDisplay.setFlags(flags);
        }

        org.bukkit.entity.Display.Brightness brightness = configuration.brightness();
        if (brightness != null)
            display.setBrightnessOverride(new Brightness(brightness.getBlockLight(), brightness.getSkyLight()));

        display.setTransformation(new Transformation(
                configuration.translation(),
                new Quaternionf(),
                configuration.scale(),
                new Quaternionf()
        ));

        display.setShadowRadius(configuration.shadowRadius());
        display.setShadowStrength(configuration.shadowStrength());
    }

    private void send(Player player, Packet<?> packet) {
        if (packet instanceof ClientboundSetEntityDataPacket setEntityDataPacket) {
            ServerLevel level = ((CraftWorld) player.getWorld()).getHandle();
            RegistryAccess registryAccess = level.registryAccess();
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(new FriendlyByteBuf(Unpooled.buffer()), registryAccess);
            ReflectionUtils.callMethod(setEntityDataPacket, "write", Set.of(RegistryFriendlyByteBuf.class), buf);
            int packetSize = buf.readableBytes();

            playerPacketStats.computeIfAbsent(player.getUniqueId(), id -> new PacketStats()).update(packetSize);

            System.out.println("[Hologram] Sent Data Packet to " + player.getName() + " | Size: " + packetSize + " bytes");
        }

        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    public static void printPacketStats() {
        System.out.println("[Hologram] Packet Statistics:");
        playerPacketStats.forEach((uuid, stats) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                System.out.println("  - " + player.getName() + ":");
                System.out.println("      Total Size: " + stats.getTotalSize() + " bytes");
                System.out.println("      Count: " + stats.getCount());
                System.out.println("      Min: " + stats.getMinSize() + " bytes");
                System.out.println("      Max: " + stats.getMaxSize() + " bytes");
                System.out.println("      Avg: " + String.format("%.2f", stats.getAverageSize()) + " bytes");
            }
        });
    }

    public void teleportTo(Location location) {
        boolean worldChanged = !this.location.world().equals(location.getWorld().getName());
        this.location = new CustomLocation(location);
        this.updateLocation();

        if (!worldChanged)
            return;

        this.deleteForAllPlayers();
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getWorld().getName().equals(location.getWorld().getName()))
                .forEach(player -> {
                    this.create();
                    this.create(player);
                });
    }

    public HologramType type() {
        return type;
    }

    public Hologram type(HologramType type) {
        this.type = type;
        return this;
    }

    public HologramConfiguration configuration() {
        return configuration;
    }

    public Hologram configuration(HologramConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    public String name() {
        return name;
    }

    public Hologram name(String name) {
        this.name = name;
        return this;
    }

    public CustomLocation location() {
        return location;
    }

    public Hologram location(CustomLocation location) {
        this.location = location;
        return this;
    }

    public UUID owner() {
        return owner;
    }

    public Hologram owner(UUID owner) {
        this.owner = owner;
        return this;
    }

    private static class PacketStats {
        private final AtomicLong totalSize = new AtomicLong();
        private final AtomicInteger count = new AtomicInteger();
        private final AtomicLong minSize = new AtomicLong(Long.MAX_VALUE);
        private final AtomicLong maxSize = new AtomicLong();

        public void update(long packetSize) {
            totalSize.addAndGet(packetSize);
            count.incrementAndGet();

            minSize.updateAndGet(currentMin -> Math.min(currentMin, packetSize));
            maxSize.updateAndGet(currentMax -> Math.max(currentMax, packetSize));
        }

        public long getTotalSize() {
            return totalSize.get();
        }

        public int getCount() {
            return count.get();
        }

        public long getMinSize() {
            return (count.get() == 0) ? 0 : minSize.get();
        }

        public long getMaxSize() {
            return maxSize.get();
        }

        public double getAverageSize() {
            return (count.get() == 0) ? 0 : (double) totalSize.get() / count.get();
        }
    }
}
