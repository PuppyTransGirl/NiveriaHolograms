package toutouchien.niveriaholograms.commands.hologram.edit.text;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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

import java.util.List;

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

public class HologramEditRemoveLineCommand {
    private HologramEditRemoveLineCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("removeline")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.removeline"))
                .then(Commands.argument("line", IntegerArgumentType.integer(1))
                        .suggests((ctx, builder) -> {
                            String hologramName = ctx.getArgument("hologram", String.class);

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            Hologram hologram = hologramManager.hologramByName(hologramName);
                            if (hologram == null)
                                return builder.buildFuture();

                            if (!(hologram.configuration() instanceof TextHologramConfiguration configuration))
                                return builder.buildFuture();

                            List<String> lines = configuration.text();
                            for (int i = 1; i <= lines.size(); i++) {
                                String iString = Integer.toString(i);
                                if (iString.startsWith(builder.getRemainingLowerCase()))
                                    builder.suggest(iString);
                            }

                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            CommandSender sender = CommandUtils.sender(ctx);
                            String hologramName = ctx.getArgument("hologram", String.class);
                            int line = ctx.getArgument("line", int.class);

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            Hologram hologram = hologramManager.hologramByName(hologramName);
                            if (hologram == null) {
                                LANG.sendMessage(sender, "command.hologram.edit.doesnt_exist",
                                        Lang.unparsedPlaceholder("hologram_name", hologramName)
                                );
                                return Command.SINGLE_SUCCESS;
                            }

                            if (!(hologram.configuration() instanceof TextHologramConfiguration configuration)) {
                                LANG.sendMessage(sender, "command.hologram.edit.only_text");
                                return Command.SINGLE_SUCCESS;
                            }

                            List<String> lines = configuration.text();
                            if (line > lines.size()) {
                                LANG.sendMessage(sender, "command.hologram.edit.removeline.invalid_line",
                                        Lang.numberPlaceholder("input_line", line)
                                );
                                return Command.SINGLE_SUCCESS;
                            }

                            hologram.editConfig((TextHologramConfiguration config) ->
                                    config.removeText(line - 1)
                            );

                            LANG.sendMessage(sender, "command.hologram.edit.removeline.edited",
                                    Lang.unparsedPlaceholder("hologram_name", hologramName)
                            );
                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }
}