package toutouchien.niveriaholograms.commands.hologram.edit.text;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
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

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

public class HologramEditAddLineCommand {
    private HologramEditAddLineCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("addline")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.addline"))
                .then(Commands.argument("text", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            CommandSender sender = CommandUtils.sender(ctx);
                            String hologramName = ctx.getArgument("hologram", String.class);
                            String text = ctx.getArgument("text", String.class);

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            Hologram hologram = hologramManager.hologramByName(hologramName);
                            if (hologram == null) {
                                LANG.sendMessage(sender, "niveriaholograms.hologram.edit.doesnt_exist",
                                        Lang.unparsedPlaceholder("niveriaholograms_hologram_name", hologramName)
                                );
                                return Command.SINGLE_SUCCESS;
                            }

                            if (!(hologram.configuration() instanceof TextHologramConfiguration)) {
                                LANG.sendMessage(sender, "niveriaholograms.hologram.edit.only_text");
                                return Command.SINGLE_SUCCESS;
                            }

                            hologram.editConfig((TextHologramConfiguration config) ->
                                    config.addText(text)
                            );

                            LANG.sendMessage(sender, "niveriaholograms.hologram.edit.addline.edited",
                                    Lang.unparsedPlaceholder("niveriaholograms_hologram_name", hologramName)
                            );
                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }
}