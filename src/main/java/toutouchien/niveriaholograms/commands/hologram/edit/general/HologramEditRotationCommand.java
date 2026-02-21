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

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

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
                                    float yaw = ctx.getArgument("yaw", float.class);
                                    float pitch = ctx.getArgument("pitch", float.class);

                                    HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                                    Hologram hologram = hologramManager.hologramByName(hologramName);
                                    if (hologram == null) {
                                        LANG.sendMessage(sender, "command.hologram.edit.doesnt_exist",
                                                Lang.unparsedPlaceholder("hologram_name", hologramName)
                                        );
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    hologram.editLocation(location ->
                                            location.yaw(yaw)
                                                    .pitch(pitch)
                                    );

                                    LANG.sendMessage(sender, "command.hologram.edit.rotation.edited",
                                            Lang.unparsedPlaceholder("hologram_name", hologramName),
                                            Lang.numberPlaceholder("hologram_yaw", yaw),
                                            Lang.numberPlaceholder("hologram_pitch", pitch)
                                    );
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                ).build();
    }
}
