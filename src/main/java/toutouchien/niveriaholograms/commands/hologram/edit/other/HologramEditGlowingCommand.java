package toutouchien.niveriaholograms.commands.hologram.edit.other;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.BlockHologramConfiguration;
import toutouchien.niveriaholograms.configurations.ItemHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

import java.util.List;
import java.util.Locale;

public class HologramEditGlowingCommand {
    private HologramEditGlowingCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("glowing")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.glowing"))
                .then(Commands.argument("color", StringArgumentType.greedyString())
                        .suggests((ctx, builder) -> {
                            List<String> suggestions = List.of("aqua", "black", "blue", "dark_aqua", "dark_blue", "dark_gray", "dark_green", "dark_purple", "dark_red", "gold", "gray", "green", "light_purple", "red", "white", "yellow", "transparent", "none", "default");

                            suggestions.forEach(builder::suggest);

                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            CommandSender sender = CommandUtils.sender(ctx);
                            String hologramName = ctx.getArgument("hologram", String.class);
                            String colorName = ctx.getArgument("color", String.class);

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            Hologram hologram = hologramManager.hologramByName(hologramName);
                            if (hologram == null) {
                                Lang.sendMessage(sender, "niveriaholograms.hologram.edit.doesnt_exist", hologramName);
                                return Command.SINGLE_SUCCESS;
                            }

                            if (!(hologram.configuration() instanceof BlockHologramConfiguration) && !(hologram.configuration() instanceof ItemHologramConfiguration)) {
                                Lang.sendMessage(sender, "niveriaholograms.hologram.edit.only_block_and_item");
                                return Command.SINGLE_SUCCESS;
                            }

                            String option = colorName.toLowerCase(Locale.ROOT);
                            TextColor glowingColor;
                            switch (option) {
                                case "none" -> glowingColor = null;
                                case "default" -> glowingColor = NamedTextColor.WHITE;

                                default -> {
                                    TextColor textColor = option.startsWith("#")
                                            ? TextColor.fromHexString(option)
                                            : NamedTextColor.NAMES.value(option);

                                    if (textColor == null) {
                                        Lang.sendMessage(sender, "niveriaholograms.hologram.edit.glowing.invalid_color", colorName);
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    glowingColor = textColor;
                                }
                            }

                            applyGlowing(hologram, glowingColor);

                            Lang.sendMessage(sender, "niveriaholograms.hologram.edit.glowing.edited", hologramName);
                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }

    private static void applyGlowing(Hologram hologram, TextColor glowingColor) {
        if (hologram.configuration() instanceof BlockHologramConfiguration) {
            hologram.editConfig((BlockHologramConfiguration config) -> {
                if (glowingColor == null) {
                    config.glowing(false);
                    return;
                }

                config.glowing(true);
                config.glowingColor(glowingColor);
            });
        } else if (hologram.configuration() instanceof ItemHologramConfiguration) {
            hologram.editConfig((ItemHologramConfiguration config) -> {
                if (glowingColor == null) {
                    config.glowing(false);
                    return;
                }

                config.glowing(true);
                config.glowingColor(glowingColor);
            });
        }
    }
}