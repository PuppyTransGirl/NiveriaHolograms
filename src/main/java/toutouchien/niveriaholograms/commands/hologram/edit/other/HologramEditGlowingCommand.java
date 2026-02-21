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
import toutouchien.niveriaholograms.configurations.special.GlowingHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

import java.util.List;
import java.util.Locale;

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

public class HologramEditGlowingCommand {
    private HologramEditGlowingCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("glowing")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.glowing"))
                .then(Commands.argument("color", StringArgumentType.greedyString())
                        .suggests((ctx, builder) -> {
                            List<String> suggestions = List.of("aqua", "black", "blue", "dark_aqua", "dark_blue", "dark_gray", "dark_green", "dark_purple", "dark_red", "gold", "gray", "green", "light_purple", "red", "white", "yellow", "none", "default");

                            suggestions.stream()
                                    .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                    .forEach(builder::suggest);

                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            CommandSender sender = CommandUtils.sender(ctx);
                            String hologramName = ctx.getArgument("hologram", String.class);
                            String colorName = ctx.getArgument("color", String.class);

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            Hologram hologram = hologramManager.hologramByName(hologramName);
                            if (hologram == null) {
                                LANG.sendMessage(sender, "command.hologram.edit.doesnt_exist",
                                        Lang.unparsedPlaceholder("hologram_name", hologramName)
                                );
                                return Command.SINGLE_SUCCESS;
                            }

                            if (!(hologram.configuration() instanceof GlowingHologramConfiguration)) {
                                LANG.sendMessage(sender, "command.hologram.edit.only_block_and_item");
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
                                        LANG.sendMessage(sender, "command.hologram.edit.glowing.invalid_color",
                                                Lang.unparsedPlaceholder("input_glowing_color", colorName)
                                        );
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    glowingColor = textColor;
                                }
                            }

                            hologram.editConfig((GlowingHologramConfiguration config) -> {
                                if (glowingColor == null) {
                                    config.glowing(false);
                                    return;
                                }

                                config.glowing(true);
                                config.glowingColor(glowingColor);
                            });

                            LANG.sendMessage(sender, "command.hologram.edit.glowing.edited",
                                    Lang.unparsedPlaceholder("hologram_name", hologramName)
                            );
                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }
}