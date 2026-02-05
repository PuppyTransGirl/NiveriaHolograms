package toutouchien.niveriaholograms.commands.hologram.edit.text;

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
import toutouchien.niveriaholograms.configurations.TextHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;
import toutouchien.niveriaholograms.utils.HologramUtils;

import java.util.List;
import java.util.Locale;

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

public class HologramEditBackgroundCommand {
    private static final List<String> COLORS = List.of("aqua", "black", "blue", "dark_aqua", "dark_blue", "dark_gray", "dark_green", "dark_purple", "dark_red", "gold", "gray", "green", "light_purple", "red", "white", "yellow", "transparent", "none", "default", "reset");

    private HologramEditBackgroundCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("background")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.background"))
                .then(Commands.argument("color", StringArgumentType.greedyString())
                        .suggests((ctx, builder) -> {
                            COLORS.stream()
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
                                LANG.sendMessage(sender, "niveriaholograms.hologram.edit.doesnt_exist",
                                        Lang.unparsedPlaceholder("niveriaholograms_hologram_name", hologramName)
                                );
                                return Command.SINGLE_SUCCESS;
                            }

                            if (!(hologram.configuration() instanceof TextHologramConfiguration)) {
                                LANG.sendMessage(sender, "niveriaholograms.hologram.edit.only_text");
                                return Command.SINGLE_SUCCESS;
                            }

                            String option = colorName.toLowerCase(Locale.ROOT);
                            TextColor backgroundColor;
                            switch (option) {
                                case "default", "reset" -> backgroundColor = null;
                                case "transparent", "none" -> backgroundColor = HologramUtils.TRANSPARENT;

                                default -> {
                                    TextColor textColor = option.startsWith("#")
                                            ? TextColor.fromHexString(option)
                                            : NamedTextColor.NAMES.value(option);

                                    if (textColor == null) {
                                        LANG.sendMessage(sender, "niveriaholograms.hologram.edit.background.invalid_color",
                                                Lang.unparsedPlaceholder("niveriaholograms_input_background", colorName)
                                        );
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    backgroundColor = textColor;
                                }
                            }

                            hologram.editConfig((TextHologramConfiguration config) ->
                                    config.background(backgroundColor)
                            );

                            LANG.sendMessage(sender, "niveriaholograms.hologram.edit.background.edited",
                                    Lang.unparsedPlaceholder("niveriaholograms_hologram_name", hologramName)
                            );
                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }
}