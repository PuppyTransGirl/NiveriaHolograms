package toutouchien.niveriaholograms.nms;

import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PositionMoveRotation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import toutouchien.niveriaapi.utils.NMSUtils;
import toutouchien.niveriaapi.utils.Task;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.config.TextHologramConfig;
import toutouchien.niveriaholograms.core.Hologram;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@NullMarked
public class v1_21_11NMSAdapter implements NMSAdapter {
    @Override
    public void sendToPlayer(Hologram hologram, Player player) {
        Entity entity = hologram.entity(Entity.class);
        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(entity, 0, entity.blockPosition());

        ClientboundTeleportEntityPacket teleportPacket = new ClientboundTeleportEntityPacket(
                entity.getId(),
                PositionMoveRotation.of(entity),
                Collections.emptySet(),
                false
        );

        if (entity instanceof Display.TextDisplay textDisplay && hologram.config() instanceof TextHologramConfig textConfig)
            textDisplay.setText(PaperAdventure.asVanilla(textConfig.serializedText(player)));

        List<SynchedEntityData.DataValue<?>> data = entity.getEntityData().getNonDefaultValues();
        ClientboundSetEntityDataPacket dataPacket = data != null ? new ClientboundSetEntityDataPacket(entity.getId(), data) : null;

        NMSUtils.sendNonNullPackets(player, addEntityPacket, teleportPacket, dataPacket);
    }

    @Override
    public void updateForAllPlayers(Hologram hologram) {
        if (Bukkit.getOnlinePlayers().isEmpty())
            return;

        Entity entity = hologram.entity(Entity.class);

        ClientboundTeleportEntityPacket teleportPacket = null;
        if (hologram.locationDirty()) {
            teleportPacket = new ClientboundTeleportEntityPacket(
                    entity.getId(),
                    PositionMoveRotation.of(entity),
                    Collections.emptySet(),
                    false
            );

            hologram.locationDirty(false);
        }

        List<Player> players = Bukkit.getOnlinePlayers().stream()
                .filter(player ->
                        player.getWorld().getName().equals(hologram.location().getWorld().getName())
                )
                .collect(Collectors.toList());

        if (hologram.config() instanceof TextHologramConfig textConfig && textConfig.updateIntervalDirty()) {
            if (textConfig.updateInterval() > 0)
                hologram.updateTask(Task.asyncRepeat(
                        ignored -> hologram.updateForAllPlayers(),
                        NiveriaHolograms.instance(),
                        Math.max(40L, textConfig.updateInterval()) * 50L,
                        textConfig.updateInterval() * 50L,
                        TimeUnit.MILLISECONDS
                ));

            textConfig.updateIntervalDirty(false);
        }

        sendDataPackets(players, teleportPacket);
    }

    private void sendDataPackets(List<Player> players, @Nullable ClientboundTeleportEntityPacket teleportPacket) {
        Hologram.hologramSender().submit(() -> {
            for (Player player : players) {

            }
        })
    }

    @Override
    public void deleteForPlayer(Hologram hologram, Player player) {
        Entity entity = hologram.entity(Entity.class);
        NMSUtils.sendPacket(player, new ClientboundRemoveEntitiesPacket(entity.getId()));
    }

    public static Display.BillboardConstraints billboardBukkitToNMS(org.bukkit.entity.Display.Billboard bukkitBillboard) {
        return switch (bukkitBillboard) {
            case FIXED -> Display.BillboardConstraints.FIXED;
            case VERTICAL -> Display.BillboardConstraints.VERTICAL;
            case HORIZONTAL -> Display.BillboardConstraints.HORIZONTAL;
            case CENTER -> Display.BillboardConstraints.CENTER;
        };
    }

    @Nullable
    public static Brightness brightnessBukkitToNMS(org.bukkit.entity.Display.@Nullable Brightness bukkitBrightness) {
        if (bukkitBrightness == null)
            return null;

        return new Brightness(bukkitBrightness.getBlockLight(), bukkitBrightness.getSkyLight());
    }
}
