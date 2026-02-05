package toutouchien.niveriaholograms.commands.hologram.edit.general;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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

public class HologramEditVisibilityDistanceCommand {
    private HologramEditVisibilityDistanceCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("visibilitydistance")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.visibilitydistance"))
                .then(Commands.argument("visibilitydistance", IntegerArgumentType.integer(-1))
                        .executes(ctx -> {
                            CommandSender sender = CommandUtils.sender(ctx);
                            String hologramName = ctx.getArgument("hologram", String.class);
                            int visibilityDistance = ctx.getArgument("visibilitydistance", int.class);

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            Hologram hologram = hologramManager.hologramByName(hologramName);
                            if (hologram == null) {
                                LANG.sendMessage(sender, "niveriaholograms.hologram.edit.doesnt_exist",
                                        Lang.unparsedPlaceholder("niveriaholograms_hologram_name", hologramName)
                                );
                                return Command.SINGLE_SUCCESS;
                            }

                            hologram.editConfig(config ->
                                    config.visibilityDistance(visibilityDistance)
                            );

                            LANG.sendMessage(sender, "niveriaholograms.hologram.edit.visibilitydistance.edited",
                                    Lang.unparsedPlaceholder("niveriaholograms_hologram_name", hologramName),
                                    Lang.numberPlaceholder("niveriaholograms_hologram_visibility_distance", visibilityDistance)
                            );
                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }
}