package toutouchien.niveriaholograms.commands.hologram.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

public class HologramTeleportCommand {
    private HologramTeleportCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("teleport")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.teleport", true))
                .then(Commands.argument("hologram", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();

                            hologramManager.holograms().stream()
                                    .map(Hologram::name)
                                    .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                    .forEach(builder::suggest);

                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            Player player = (Player) ctx.getSource().getExecutor();
                            String hologramName = StringArgumentType.getString(ctx, "hologram");

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            Hologram hologram = hologramManager.hologramByName(hologramName);
                            if (hologram == null) {
                                Lang.sendMessage(player, "niveriaholograms.hologram.teleport.doesnt_exist", hologramName);
                                return Command.SINGLE_SUCCESS;
                            }

                            player.teleportAsync(hologram.location().bukkitLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN)
                                    .thenAccept(ignored ->
                                            Lang.sendMessage(player, "niveriaholograms.hologram.teleport.teleported", hologram.name())
                                    );
                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }
}