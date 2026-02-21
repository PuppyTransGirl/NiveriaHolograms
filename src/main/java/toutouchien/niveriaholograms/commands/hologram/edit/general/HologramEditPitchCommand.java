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

public class HologramEditPitchCommand {
    private HologramEditPitchCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("pitch")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.pitch"))
                .then(Commands.argument("pitch", FloatArgumentType.floatArg(-90F, 90F))
                        .executes(ctx -> {
                            CommandSender sender = CommandUtils.sender(ctx);
                            String hologramName = ctx.getArgument("hologram", String.class);
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
                                    location.pitch(pitch)
                            );

                            LANG.sendMessage(sender, "command.hologram.edit.pitch.edited",
                                    Lang.unparsedPlaceholder("hologram_name", hologramName),
                                    Lang.numberPlaceholder("hologram_pitch", pitch)
                            );
                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }
}