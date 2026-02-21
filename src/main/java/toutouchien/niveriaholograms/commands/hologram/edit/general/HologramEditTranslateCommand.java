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

public class HologramEditTranslateCommand {
    private HologramEditTranslateCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("translate")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.translate"))
                .then(Commands.argument("translateX", FloatArgumentType.floatArg())
                        .then(Commands.argument("translateY", FloatArgumentType.floatArg())
                                .then(Commands.argument("translateZ", FloatArgumentType.floatArg())
                                        .executes(ctx -> {
                                            CommandSender sender = CommandUtils.sender(ctx);
                                            String hologramName = ctx.getArgument("hologram", String.class);
                                            float translateX = ctx.getArgument("translateX", float.class);
                                            float translateY = ctx.getArgument("translateY", float.class);
                                            float translateZ = ctx.getArgument("translateZ", float.class);

                                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                                            Hologram hologram = hologramManager.hologramByName(hologramName);
                                            if (hologram == null) {
                                                LANG.sendMessage(sender, "command.hologram.edit.doesnt_exist",
                                                        Lang.unparsedPlaceholder("hologram_name", hologramName)
                                                );
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            hologram.editConfig(config ->
                                                    config.translation().set(translateX, translateY, translateZ)
                                            );

                                            LANG.sendMessage(sender, "command.hologram.edit.translate.edited",
                                                    Lang.unparsedPlaceholder("hologram_name", hologramName)
                                            );
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                ).build();
    }
}