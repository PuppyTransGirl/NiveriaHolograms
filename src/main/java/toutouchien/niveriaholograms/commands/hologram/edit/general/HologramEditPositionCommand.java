package toutouchien.niveriaholograms.commands.hologram.edit.general;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.math.FinePosition;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

@SuppressWarnings("UnstableApiUsage")
public class HologramEditPositionCommand {
    private HologramEditPositionCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("position")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.position"))
                .then(Commands.argument("player", ArgumentTypes.player()).executes(HologramEditPositionCommand::getAfter))
                .then(Commands.argument("position", ArgumentTypes.finePosition())
                        .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.position", true)) // position argument requires a player
                        .executes(HologramEditPositionCommand::getAfter)
                ).build();
    }

    @SuppressWarnings({"DataFlowIssue", "java:S2259"})
    private static Location locationFromContext(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        try {
            FinePositionResolver resolver = ctx.getArgument("position", FinePositionResolver.class);
            FinePosition finePosition = resolver.resolve(ctx.getSource());

            // Player cannot be null if the provided argument was position since it requires a player
            Player player = (Player) ctx.getSource().getExecutor();
            return finePosition.toLocation(player.getWorld());
        } catch (IllegalArgumentException e) {
            PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
            Player target = targetResolver.resolve(ctx.getSource()).getFirst();
            return target.getLocation();
        }
    }

    @SuppressWarnings("SameReturnValue")
    public static int getAfter(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = CommandUtils.sender(ctx);
        String hologramName = ctx.getArgument("hologram", String.class);

        HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
        Hologram hologram = hologramManager.hologramByName(hologramName);
        if (hologram == null) {
            LANG.sendMessage(sender, "niveriaholograms.hologram.edit.doesnt_exist",
                    Lang.unparsedPlaceholder("niveriaholograms_hologram_name", hologramName)
            );
            return Command.SINGLE_SUCCESS;
        }

        Location newLocation = locationFromContext(ctx);
        hologram.editLocation(location ->
                location.world(newLocation.getWorld().getName())
                        .x(newLocation.x())
                        .y(newLocation.y())
                        .z(newLocation.z())
        );

        LANG.sendMessage(sender, "niveriaholograms.hologram.edit.position.edited",
                Lang.unparsedPlaceholder("niveriaholograms_hologram_name", hologramName)
        );
        return Command.SINGLE_SUCCESS;
    }
}