package toutouchien.niveriaholograms.commands.hologram.edit.text;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.TextHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

public class HologramEditTextShadowCommand {
    private HologramEditTextShadowCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("textShadow")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.textshadow"))
                .then(Commands.argument("textShadow", BoolArgumentType.bool())
                        .executes(ctx -> {
                            CommandSender sender = CommandUtils.sender(ctx);
                            String hologramName = ctx.getArgument("hologram", String.class);
                            boolean textShadow = ctx.getArgument("textShadow", boolean.class);

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            Hologram hologram = hologramManager.hologramByName(hologramName);
                            if (hologram == null) {
                                Lang.sendMessage(sender, "niveriaholograms.hologram.edit.doesnt_exist", hologramName);
                                return Command.SINGLE_SUCCESS;
                            }

                            if (!(hologram.configuration() instanceof TextHologramConfiguration)) {
                                Lang.sendMessage(sender, "niveriaholograms.hologram.edit.only_text");
                                return Command.SINGLE_SUCCESS;
                            }

                            hologram.editConfig((TextHologramConfiguration config) ->
                                    config.textShadow(textShadow)
                            );

                            Lang.sendMessage(sender, "niveriaholograms.hologram.edit.textShadow.edited", hologramName);
                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }
}