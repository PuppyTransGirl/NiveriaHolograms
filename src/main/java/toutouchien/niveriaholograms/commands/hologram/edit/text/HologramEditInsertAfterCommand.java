package toutouchien.niveriaholograms.commands.hologram.edit.text;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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

import java.util.List;

public class HologramEditInsertAfterCommand {
    private HologramEditInsertAfterCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("insertAfter")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.insertafter"))
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
                            for (int i = 1; i <= lines.size(); i++)
                                builder.suggest(Integer.toString(i));

                            return builder.buildFuture();
                        })
                        .then(Commands.argument("text", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    CommandSender sender = CommandUtils.sender(ctx);
                                    String hologramName = ctx.getArgument("hologram", String.class);
                                    int line = ctx.getArgument("line", int.class);
                                    String text = ctx.getArgument("text", String.class);

                                    HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                                    Hologram hologram = hologramManager.hologramByName(hologramName);
                                    if (hologram == null) {
                                        Lang.sendMessage(sender, "niveriaholograms.hologram.edit.doesnt_exist", hologramName);
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    if (!(hologram.configuration() instanceof TextHologramConfiguration configuration)) {
                                        Lang.sendMessage(sender, "niveriaholograms.hologram.edit.only_text");
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    List<String> lines = configuration.text();
                                    if (line > lines.size()) {
                                        Lang.sendMessage(sender, "niveriaholograms.hologram.edit.insertAfter.invalid_line", line);
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    hologram.editConfig((TextHologramConfiguration config) ->
                                            config.addTextAfter(line - 1, text)
                                    );

                                    Lang.sendMessage(sender, "niveriaholograms.hologram.edit.insertAfter.edited", hologramName);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                ).build();
    }
}