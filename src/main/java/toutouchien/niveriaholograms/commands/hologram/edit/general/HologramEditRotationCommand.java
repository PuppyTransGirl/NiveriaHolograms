package toutouchien.niveriaholograms.commands.hologram.edit.general;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

public class HologramEditRotationCommand {
    private HologramEditRotationCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("rotation")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.rotation"))
                .then(Commands.argument("yaw", FloatArgumentType.floatArg(-180F, 180F))
                        .then(Commands.argument("pitch", FloatArgumentType.floatArg(-90F, 90F))
                                .executes(ctx -> {
                                    CommandSender sender = CommandUtils.sender(ctx);
                                    String hologramName = ctx.getArgument("hologram", String.class);
                                    float yaw = ctx.getArgument("yaw", Float.class);
                                    float pitch = ctx.getArgument("pitch", Float.class);

                                    HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                                    Hologram hologram = hologramManager.hologramByName(hologramName);
                                    if (hologram == null) {
                                        Lang.sendMessage(sender, "niveriaholograms.hologram.edit.doesnt_exist", hologramName);
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    hologram.editLocation(location ->
                                            location.yaw(yaw)
                                                    .pitch(pitch)
                                    );

                                    Lang.sendMessage(sender, "niveriaholograms.hologram.edit.rotation.edited", hologramName, yaw, pitch);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                ).build();
    }
}
