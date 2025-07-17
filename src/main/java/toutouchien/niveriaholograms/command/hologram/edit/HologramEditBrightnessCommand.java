package toutouchien.niveriaholograms.command.hologram.edit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configuration.HologramConfiguration;
import toutouchien.niveriaholograms.hologram.Hologram;
import toutouchien.niveriaholograms.hologram.HologramManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class HologramEditBrightnessCommand extends SubCommand {
    private static final List<String> BRIGHTNESS_TYPES = Arrays.asList("sky", "block");
    private static final List<String> DEFAULT_VALUES = Arrays.asList("0", "5", "10", "15");
    
    public HologramEditBrightnessCommand() {
        super(new CommandData("brightness", "niveriaholograms")
              .playerRequired(true)
              .usage("<block|sky> <0-15>"));
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

        if (args.length == 0) {
            player.sendMessage(Component.text("/" + label + " " + String.join(" ", fullArgs) + " <block|sky> <0-15>", NamedTextColor.RED));
            return;
        }
        
        String type = args[0].toLowerCase(Locale.ROOT);
        if (!BRIGHTNESS_TYPES.contains(type)) {
            TextComponent errorMessage = MessageUtils.errorMessage(
                    Component.text("Type de luminosité invalide. Utilisez 'sky' ou 'block'.")
            );

            player.sendMessage(errorMessage);
            return;
        }
        
        int value;
        try {
            value = Integer.parseInt(args[1]);
            if (value < 0 || value > 15) {
                TextComponent errorMessage = MessageUtils.errorMessage(
                        Component.text("La valeur de luminosité doit être entre 0 et 15.")
                );

                player.sendMessage(errorMessage);
                return;
            }
        } catch (NumberFormatException e) {
            TextComponent errorMessage = MessageUtils.errorMessage(
                    Component.text("La valeur de luminosité doit être entre 0 et 15.")
            );

            player.sendMessage(errorMessage);
            return;
        }

        HologramConfiguration configuration = hologram.configuration();

        Display.Brightness currentBrightness = configuration.brightness();
        int blockBrightness = type.equalsIgnoreCase("block") ? value : currentBrightness == null ? 0 : currentBrightness.getBlockLight();
        int skyBrightness = type.equalsIgnoreCase("sky") ? value : currentBrightness == null ? 0 : currentBrightness.getSkyLight();

        hologram.editConfig(config -> {
            config.brightness(new Display.Brightness(blockBrightness, skyBrightness));
        });

        TextComponent successMessage = MessageUtils.successMessage(
                Component.text()
                        .append(Component.text("La luminosité de type '"))
                        .append(Component.text(type))
                        .append(Component.text("' a été mise à "))
                        .append(Component.text(value))
                        .append(Component.text(" avec succès !"))
                        .build()
        );

        player.sendMessage(successMessage);
    }
    
    @Override
    public List<String> complete(@NotNull Player player, String[] args, String @NotNull [] fullArgs, int argIndex) {
        String currentArg = args[argIndex];
        if (argIndex == 0)
            return Stream.of("block", "sky")
                    .filter(billboard -> billboard.toLowerCase(Locale.ROOT).startsWith(currentArg))
                    .toList();
        else if (argIndex == 1)
            return DEFAULT_VALUES;
        
        return Collections.emptyList();
    }
}