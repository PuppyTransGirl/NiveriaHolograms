package toutouchien.niveriaholograms.commands.hologram.edit.text;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.TextDisplay;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaapi.utils.EnumUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.TextHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

public class HologramEditTextAlignmentCommand {
    private HologramEditTextAlignmentCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("textalignment")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.textalignment"))
                .then(Commands.argument("textalignment", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            String hologramName = ctx.getArgument("hologram", String.class);

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            Hologram hologram = hologramManager.hologramByName(hologramName);
                            if (hologram == null)
                                return builder.buildFuture();

                            if (!(hologram.configuration() instanceof TextHologramConfiguration configuration))
                                return builder.buildFuture();

                            TextDisplay.TextAlignment currentTextAlignment = configuration.textAlignment();
                            for (TextDisplay.TextAlignment textAlignment : TextDisplay.TextAlignment.values())
                                if (textAlignment != currentTextAlignment)
                                    builder.suggest(textAlignment.name());

                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            CommandSender sender = CommandUtils.sender(ctx);
                            String hologramName = ctx.getArgument("hologram", String.class);
                            String textAlignmentName = ctx.getArgument("textalignment", String.class);

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            Hologram hologram = hologramManager.hologramByName(hologramName);
                            if (hologram == null) {
                                LANG.sendMessage(sender, "command.hologram.edit.doesnt_exist",
                                        Lang.unparsedPlaceholder("hologram_name", hologramName)
                                );
                                return Command.SINGLE_SUCCESS;
                            }

                            if (!(hologram.configuration() instanceof TextHologramConfiguration)) {
                                LANG.sendMessage(sender, "command.hologram.edit.only_text");
                                return Command.SINGLE_SUCCESS;
                            }

                            TextDisplay.TextAlignment textAlignment = EnumUtils.match(textAlignmentName, TextDisplay.TextAlignment.class, null);
                            if (textAlignment == null) {
                                LANG.sendMessage(sender, "command.hologram.edit.textalignment.invalid_textalignment",
                                        Lang.unparsedPlaceholder("input_alignment", textAlignmentName)
                                );
                                return Command.SINGLE_SUCCESS;
                            }

                            hologram.editConfig((TextHologramConfiguration config) ->
                                    config.textAlignment(textAlignment)
                            );

                            LANG.sendMessage(sender, "command.hologram.edit.textalignment.edited",
                                    Lang.unparsedPlaceholder("hologram_name", hologramName)
                            );
                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }
}