package toutouchien.niveriaholograms.commands.hologram.edit.text;

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
import toutouchien.niveriaholograms.configurations.TextHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

import java.util.*;

public class HologramEditBackgroundCommand extends SubCommand {
    public HologramEditBackgroundCommand() {
        super(new CommandData("background", "niveriaholograms")
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

        if (!(hologram.configuration() instanceof TextHologramConfiguration)) {
            TextComponent errorMessage = MessageUtils.errorMessage(
                    Component.text("Cette comande ne peut être utilisée que sur des hologrammes de texte.")
            );

            player.sendMessage(errorMessage);
            return;
        }

        if (args.length == 0) {
            TextComponent errorMessage = MessageUtils.errorMessage(
                    Component.text("Tu dois spécifier la couleur de l'arrière plan.")
            );

            player.sendMessage(errorMessage);
            return;
        }

        String option = args[0].toLowerCase(Locale.ROOT);
        TextColor background;
        switch (option) {
            case "default", "reset" -> background = null;
            case "transparent", "none" -> background = Hologram.TRANSPARENT;

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

                background = textColor;
            }
        }

        hologram.editConfig((TextHologramConfiguration config) -> {
            config.background(background);
        });

        TextComponent successMessage = MessageUtils.successMessage(
                Component.text()
                        .append(Component.text("La couleur d'arrière plan a été changée à "))
                        .append(Component.text(option))
                        .append(Component.text(" avec succès !"))
                        .build()
        );

        player.sendMessage(successMessage);
    }

    @Override
    public List<String> complete(@NotNull Player player, String @NotNull [] args, String @NotNull [] fullArgs, int argIndex) {
        if (argIndex != 0)
            return Collections.emptyList();

        Hologram hologram = NiveriaHolograms.instance().hologramManager().hologramByName(fullArgs[1]);
        if (hologram == null || !(hologram.configuration() instanceof TextHologramConfiguration configuration))
            return Collections.emptyList();

        List<String> completion = new ArrayList<>(List.of("aqua", "black", "blue", "dark_aqua", "dark_blue", "dark_gray", "dark_green", "dark_purple", "dark_red", "gold", "gray", "green", "light_purple", "red", "white", "yellow"));
        TextColor background = configuration.background();
        if (background == null) {
            completion.add("transparent");
            return completion;
        }

        completion.addAll(Arrays.asList("default", "reset"));

        if (background != Hologram.TRANSPARENT) {
            completion.add("transparent");

            if (!(background instanceof NamedTextColor))
                completion.add(background.asHexString());
        }

        String currentArg = args[argIndex];
        return completion.stream()
                .filter(billboard -> billboard.toLowerCase(Locale.ROOT).startsWith(currentArg))
                .toList();
    }
}