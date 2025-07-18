package toutouchien.niveriaholograms.commands.hologram.edit.other;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.BlockHologramConfiguration;
import toutouchien.niveriaholograms.configurations.ItemHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HologramEditGlowingCommand extends SubCommand {
    public HologramEditGlowingCommand() {
        super(new CommandData("glowing", "niveriaholograms")
                .playerRequired(true)
                .usage("<couleur|transparent|none|#FFFFFF>"));
    }

    @Override
    public void execute(@NotNull Player player, String @NotNull [] args, String[] fullArgs, @NotNull String label) {
        HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
        Hologram hologram = hologramManager.hologramByName(fullArgs[1]);
        if (hologram == null) {
            TextComponent errorMessage = MessageUtils.errorMessage(
                    Component.text("Cet hologramme n'existe pas.")
            );

            player.sendMessage(errorMessage);
            return;
        }

        if (!(hologram.configuration() instanceof BlockHologramConfiguration) && !(hologram.configuration() instanceof ItemHologramConfiguration)) {
            TextComponent errorMessage = MessageUtils.errorMessage(
                    Component.text("Cette comande ne peut être utilisée que sur des hologrammes d'item et de bloc.")
            );

            player.sendMessage(errorMessage);
            return;
        }

        if (args.length == 0) {
            TextComponent errorMessage = MessageUtils.errorMessage(
                    Component.text("Tu dois spécifier la couleur de glowing.")
            );

            player.sendMessage(errorMessage);
            return;
        }

        String option = args[0].toLowerCase(Locale.ROOT);
        TextColor glowingColor;
        switch (option) {
            case "none" -> glowingColor = null;
            case "default" -> glowingColor = NamedTextColor.WHITE;

            default -> {
                TextColor textColor = option.startsWith("#")
                        ? TextColor.fromHexString(option)
                        : NamedTextColor.NAMES.value(option);

                if (textColor == null) {
                    TextComponent errorMessage = MessageUtils.errorMessage(
                            Component.text("Cette couleur est invalide.")
                    );

                    player.sendMessage(errorMessage);
                    return;
                }

                glowingColor = textColor;
            }
        }
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

        TextComponent successMessage = MessageUtils.successMessage(
                Component.text("Le glowing a été changé avec succès !")
        );

        player.sendMessage(successMessage);
    }

    @Override
    public List<String> complete(@NotNull Player player, String @NotNull [] args, String @NotNull [] fullArgs, int argIndex) {
        if (argIndex != 0)
            return Collections.emptyList();

        Hologram hologram = NiveriaHolograms.instance().hologramManager().hologramByName(fullArgs[1]);
        if (hologram == null || (!(hologram.configuration() instanceof BlockHologramConfiguration) && !(hologram.configuration() instanceof ItemHologramConfiguration)))
            return Collections.emptyList();

        List<String> completion = new ArrayList<>(List.of("aqua", "black", "blue", "dark_aqua", "dark_blue", "dark_gray", "dark_green", "dark_purple", "dark_red", "gold", "gray", "green", "light_purple", "red", "white", "yellow"));
        boolean glowing = hologram.configuration() instanceof BlockHologramConfiguration
                ? ((BlockHologramConfiguration) hologram.configuration()).glowing()
                : ((ItemHologramConfiguration) hologram.configuration()).glowing();

        TextColor glowingColor = hologram.configuration() instanceof BlockHologramConfiguration
                ? ((BlockHologramConfiguration) hologram.configuration()).glowingColor()
                : ((ItemHologramConfiguration) hologram.configuration()).glowingColor();

        if (glowingColor == null || !glowing) {
            completion.add("default");
            return completion;
        }

        completion.add("none");

        if (glowingColor != NamedTextColor.WHITE) {
            completion.add("default");

            if (!(glowingColor instanceof NamedTextColor))
                completion.add(glowingColor.asHexString());
        }

        String currentArg = args[argIndex];
        return completion.stream()
                .filter(billboard -> billboard.toLowerCase(Locale.ROOT).startsWith(currentArg))
                .toList();
    }
}