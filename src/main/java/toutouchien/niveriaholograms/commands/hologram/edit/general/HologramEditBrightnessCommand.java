package toutouchien.niveriaholograms.commands.hologram.edit.general;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.minecraft.util.Brightness;
import org.bukkit.command.CommandSender;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.HologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

import java.util.List;
import java.util.Locale;

public class HologramEditBrightnessCommand {
    private static final List<String> BRIGHTNESS_TYPES = List.of("block", "sky");

    private HologramEditBrightnessCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("brightness")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.brightness"))
                .then(Commands.argument("type", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            List.of("block", "sky").forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("light", IntegerArgumentType.integer(0, 15))
                                .suggests((ctx, builder) -> {
                                    List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15).forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    CommandSender sender = CommandUtils.sender(ctx);
                                    String hologramName = ctx.getArgument("hologram", String.class);
                                    String typeName = ctx.getArgument("type", String.class);
                                    int light = ctx.getArgument("ligt", int.class);

                                    HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                                    Hologram hologram = hologramManager.hologramByName(hologramName);
                                    if (hologram == null) {
                                        Lang.sendMessage(sender, "niveriaholograms.hologram.edit.doesnt_exist", hologramName);
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    String type = typeName.toLowerCase(Locale.ROOT);
                                    if (!BRIGHTNESS_TYPES.contains(type)) {
                                        Lang.sendMessage(sender, "niveriaholograms.hologram.edit.brightness.invalid_type", typeName);
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    HologramConfiguration configuration = hologram.configuration();

                                    Brightness current = configuration.brightness();
                                    int block = current == null ? 0 : current.block();
                                    int sky = current == null ? 0 : current.sky();

                                    if ("block".equalsIgnoreCase(type))
                                        block = light;
                                    else if ("sky".equalsIgnoreCase(type))
                                        sky = light;

                                    int finalBlock = block;
                                    int finalSky = sky;
                                    hologram.editConfig(config -> {
                                        config.brightness(new Brightness(finalBlock, finalSky));
                                    });

                                    Lang.sendMessage(sender, "niveriaholograms.hologram.edit.brightness.edited", hologramName);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                ).build();
    }
}