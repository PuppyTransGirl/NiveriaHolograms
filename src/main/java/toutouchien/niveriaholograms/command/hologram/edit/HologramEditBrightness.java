package toutouchien.niveriaholograms.command.hologram.edit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
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

public class HologramEditBrightness extends SubCommand {
    private static final List<String> BRIGHTNESS_TYPES = Arrays.asList("sky", "block");
    private static final List<String> DEFAULT_VALUES = Arrays.asList("0", "5", "10", "15");
    
    public HologramEditBrightness() {
        super(new CommandData("brightness", "niveriaholograms")
              .playerRequired(true)
              .usage("<block|sky> <0-15>"));
    }
    
    @Override
    public void execute(Player player, String[] args, String[] fullArgs, String label) {
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
            player.sendMessage(Component.text("/" + label + " edit " + fullArgs[1] + " <block|sky> <0-15>", NamedTextColor.RED));
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

        HologramConfiguration config = hologram.configuration();

        Display.Brightness currentBrightness = config.brightness();
        int blockBrightness = type.equalsIgnoreCase("block") ? value : currentBrightness == null ? 0 : currentBrightness.getBlockLight();
        int skyBrightness = type.equalsIgnoreCase("sky") ? value : currentBrightness == null ? 0 : currentBrightness.getSkyLight();
        config.brightness(new Display.Brightness(blockBrightness, skyBrightness));

        hologram.update();
        hologram.updateForAllPlayers();
        hologramManager.saveHologram(hologram);

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
    public List<String> complete(Player player, String[] args, String[] fullArgs, int argIndex) {
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