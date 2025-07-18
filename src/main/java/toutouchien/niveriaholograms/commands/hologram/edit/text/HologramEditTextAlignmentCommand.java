package toutouchien.niveriaholograms.commands.hologram.edit.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.TextHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HologramEditTextAlignmentCommand extends SubCommand {
	public HologramEditTextAlignmentCommand() {
		super(new CommandData("textalignment", "niveriaholograms")
				.playerRequired(true)
				.usage("<textalignment>"));
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
					Component.text("Tu dois spécifier l'alignement du texte.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		TextDisplay.TextAlignment textAlignment;

		try {
			textAlignment = TextDisplay.TextAlignment.valueOf(args[0]);
		} catch (IllegalArgumentException e) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Ce type d'alignement de texte n'existe pas.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		hologram.editConfig((TextHologramConfiguration config) -> {
			config.textAlignment(textAlignment);
		});

		TextComponent successMessage = MessageUtils.successMessage(
				Component.text()
						.append(Component.text("L'alignement du texte a été mit à "))
						.append(Component.text(textAlignment.name()))
						.append(Component.text("."))
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

		String currentArg = args[argIndex].toLowerCase(Locale.ROOT);
		return Arrays.stream(TextDisplay.TextAlignment.values())
				.filter(textAlignment -> textAlignment != configuration.textAlignment())
				.map(Enum::name)
				.filter(textAlignment -> textAlignment.toLowerCase(Locale.ROOT).startsWith(currentArg))
				.toList();
	}
}