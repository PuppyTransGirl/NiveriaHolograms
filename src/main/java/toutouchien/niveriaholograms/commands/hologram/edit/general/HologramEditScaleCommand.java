package toutouchien.niveriaholograms.commands.hologram.edit.general;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

public class HologramEditScaleCommand {
    private HologramEditScaleCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("scale")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.scale"))
                .then(Commands.argument("scale", FloatArgumentType.floatArg())
                        .executes(ctx -> {
                            float scale = ctx.getArgument("scale", float.class);
                            return getAfter(ctx, scale, scale, scale);
                        })
                ).then(Commands.argument("scaleX", FloatArgumentType.floatArg())
                        .then(Commands.argument("scaleY", FloatArgumentType.floatArg())
                                .then(Commands.argument("scaleZ", FloatArgumentType.floatArg())
                                        .executes(ctx -> {
                                            float scaleX = ctx.getArgument("scaleX", float.class);
                                            float scaleY = ctx.getArgument("scaleY", float.class);
                                            float scaleZ = ctx.getArgument("scaleZ", float.class);
                                            return getAfter(ctx, scaleX, scaleY, scaleZ);
                                        })
                                )
                        )
                ).build();
    }

    @SuppressWarnings({"SameReturnValue", "java:S3516"})
    public static int getAfter(CommandContext<CommandSourceStack> ctx, float scaleX, float scaleY, float scaleZ) {
        CommandSender sender = CommandUtils.sender(ctx);
        String hologramName = ctx.getArgument("hologram", String.class);

        HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
        Hologram hologram = hologramManager.hologramByName(hologramName);
        if (hologram == null) {
            Lang.sendMessage(sender, "niveriaholograms.hologram.edit.doesnt_exist", hologramName);
            return Command.SINGLE_SUCCESS;
        }

        hologram.editConfig(config ->
                config.scale().set(scaleX, scaleY, scaleZ)
        );

        Lang.sendMessage(sender, "niveriaholograms.hologram.edit.scale.edited", hologramName);
        return Command.SINGLE_SUCCESS;
    }
}