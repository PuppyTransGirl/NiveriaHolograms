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

public class HologramEditShadowRadiusCommand {
    private HologramEditShadowRadiusCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("shadowradius")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.shadowradius"))
                .then(Commands.argument("shadowradius", FloatArgumentType.floatArg(0))
                        .executes(ctx -> {
                            CommandSender sender = CommandUtils.sender(ctx);
                            String hologramName = ctx.getArgument("hologram", String.class);
                            float shadowRadius = ctx.getArgument("shadowradius", float.class);

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            Hologram hologram = hologramManager.hologramByName(hologramName);
                            if (hologram == null) {
                                LANG.sendMessage(sender, "niveriaholograms.hologram.edit.doesnt_exist",
                                        Lang.unparsedPlaceholder("niveriaholograms_hologram_name", hologramName)
                                );
                                return Command.SINGLE_SUCCESS;
                            }

                            hologram.editConfig(config ->
                                    config.shadowRadius(shadowRadius)
                            );

                            LANG.sendMessage(sender, "niveriaholograms.hologram.edit.shadowradius.edited",
                                    Lang.unparsedPlaceholder("niveriaholograms_hologram_name", hologramName),
                                    Lang.numberPlaceholder("niveriaholograms_hologram_shadow_radius", shadowRadius)
                            );
                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }
}