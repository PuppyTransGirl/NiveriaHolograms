package toutouchien.niveriaholograms.commands.hologram;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.common.MathUtils;
import toutouchien.niveriaapi.utils.ui.ColorUtils;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.BlockHologramConfiguration;
import toutouchien.niveriaholograms.configurations.HologramConfiguration;
import toutouchien.niveriaholograms.configurations.ItemHologramConfiguration;
import toutouchien.niveriaholograms.configurations.TextHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;
import toutouchien.niveriaholograms.utils.CustomLocation;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static net.kyori.adventure.text.Component.text;

public class HologramInfoCommand extends SubCommand {
    HologramInfoCommand() {
        super(new CommandData("info", "niveriaholograms")
                .aliases("i")
                .usage("<hologram>"));
    }

    @Override
    public void execute(@NotNull CommandSender sender, String[] args, @NotNull String label) {
        if (args.length == 0) {
            TextComponent errorMessage = MessageUtils.errorMessage(
                    text("Tu dois spécifier le nom de l'hologramme auquel tu veux te téléporter.")
            );

            sender.sendMessage(errorMessage);
            return;
        }

        HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
        Hologram hologram = hologramManager.hologramByName(args[0]);
        if (hologram == null) {
            TextComponent errorMessage = MessageUtils.errorMessage(
                    text("Cet hologramme n'existe pas.")
            );

            sender.sendMessage(errorMessage);
            return;
        }

        HologramConfiguration config = hologram.configuration();

        CustomLocation location = hologram.location();
        Component infoMessage = text("Informations sur l'hologramme " + hologram.name() + ":", ColorUtils.primaryColor())
                .appendNewline()
                .append(text("Nom: ", ColorUtils.secondaryColor()))
                .append(text(hologram.name(), NamedTextColor.WHITE))
                .appendNewline()
                .append(text("Type: ", ColorUtils.secondaryColor()))
                .append(text(hologram.type().name(), NamedTextColor.WHITE))
                .appendNewline()
                .append(text("Position: ", ColorUtils.secondaryColor()))
                .append(text(
                        MathUtils.decimalRound(location.x(), 2) + "/"
                                + MathUtils.decimalRound(location.y(), 2) + "/"
                                + MathUtils.decimalRound(location.z(), 2)
                                + " in " + location.world(), NamedTextColor.WHITE
                ))
                .appendNewline()
                .append(text("Rotation: ", ColorUtils.secondaryColor()))
                .append(text(
                        MathUtils.decimalRound(location.yaw(), 2) + "°/"
                                + MathUtils.decimalRound(location.pitch(), 2) + "°", NamedTextColor.WHITE
                ))
                .appendNewline()
                .append(text("Taille: ", ColorUtils.secondaryColor()))
                .append(text(scaleText(config.scale()), NamedTextColor.WHITE))
                .appendNewline()
                .append(text("Billboard: ", ColorUtils.secondaryColor()))
                .append(text(config.billboard().name(), NamedTextColor.WHITE))
                .appendNewline()
                .append(text("Rayon d'ombre: ", ColorUtils.secondaryColor()))
                .append(text(config.shadowRadius(), NamedTextColor.WHITE))
                .appendNewline()
                .append(text("Force de l'ombre: ", ColorUtils.secondaryColor()))
                .append(text(config.shadowStrength(), NamedTextColor.WHITE))
                .appendNewline();

        switch (config) {
            case BlockHologramConfiguration blockConfig ->
                    infoMessage = infoMessage.append(text("Bloc: ", ColorUtils.secondaryColor()))
                            .append(text(blockConfig.material().name(), NamedTextColor.WHITE));

            case ItemHologramConfiguration itemConfig ->
                    infoMessage = infoMessage.append(text("Item: ", ColorUtils.secondaryColor()))
                            .append(text(itemConfig.itemStack().getType().name(), NamedTextColor.WHITE));

            case TextHologramConfiguration textConfig -> {
                for (String line : textConfig.text()) {
                    infoMessage = infoMessage.append(text(" - " + line, NamedTextColor.WHITE))
                            .appendNewline();
                }

                infoMessage = infoMessage.append(text("Fond: ", ColorUtils.secondaryColor()))
                        .append(text(backgroundText(textConfig.background()), NamedTextColor.WHITE))
                        .appendNewline()
                        .append(text("Alignement du texte: ", ColorUtils.secondaryColor()))
                        .append(text(textConfig.textAlignment().name(), NamedTextColor.WHITE))
                        .appendNewline()
                        .append(text("Transparent: ", ColorUtils.secondaryColor()))
                        .append(text(textConfig.seeThrough() ? "Oui" : "Non", NamedTextColor.WHITE))
                        .appendNewline()
                        .append(text("Ombre du texte: ", ColorUtils.secondaryColor()))
                        .append(text(textConfig.textShadow() ? "Oui" : "Non", NamedTextColor.WHITE));
            }

            default -> infoMessage = infoMessage.append(text("Configuration inconnue.", NamedTextColor.WHITE));
        }

        sender.sendMessage(infoMessage);
    }

    @NotNull
    private String scaleText(@NotNull Vector3f scale) {
        boolean allEquals = scale.x() == scale.y() && scale.y() == scale.z();
        return allEquals ? Float.toString(scale.x()) : scale.x() + " " + scale.y() + " " + scale.z();
    }

    @NotNull
    private String backgroundText(@Nullable TextColor background) {
        return background == null ? "Par défaut" : background == Hologram.TRANSPARENT ? "Transparent" : background.asHexString().toUpperCase(Locale.ROOT);
    }

    @Override
    public List<String> complete(@NotNull Player player, String @NotNull [] args, int argIndex) {
        if (argIndex != 0)
            return Collections.emptyList();

        String currentArg = args[argIndex].toLowerCase(Locale.ROOT);
        return NiveriaHolograms.instance().hologramManager().holograms().stream()
                .map(Hologram::name)
                .filter(hologramName -> hologramName.toLowerCase(Locale.ROOT).startsWith(currentArg))
                .toList();
    }
}
