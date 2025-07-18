package toutouchien.niveriaholograms.commands.hologram.edit.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.TextHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HologramEditSetLineCommand extends SubCommand {
	public HologramEditSetLineCommand() {
		super(new CommandData("setline", "niveriaholograms")
				.playerRequired(true)
				.usage("<ligne> <texte>"));
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

		if (!(hologram.configuration() instanceof TextHologramConfiguration configuration)) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Cette comande ne peut être utilisée que sur des hologrammes de texte.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		if (args.length == 0) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Tu dois spécifier la ligne que tu veux modifier.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		if (args.length == 1) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Tu dois spécifier le texte que tu veux mettre.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		int lineNumber;
		try {
			lineNumber = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Le numéro de la ligne n'est pas valide.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		List<String> text = configuration.text();
		if (lineNumber < 1 || lineNumber > text.size()) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Le numéro de la ligne n'est pas valide.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		String newText = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		hologram.editConfig((TextHologramConfiguration config) -> {
			config.text(lineNumber - 1, newText);
		});

		TextComponent successMessage = MessageUtils.successMessage(
				Component.text()
						.append(Component.text("La contenu de la ligne "))
						.append(Component.text(lineNumber))
						.append(Component.text(" a été changé avec succès !"))
						.build()
		);

		player.sendMessage(successMessage);
	}

	@Override
	public List<String> complete(@NotNull Player player, String @NotNull [] args, String @NotNull [] fullArgs, int argIndex) {
		if (argIndex > 1)
			return Collections.emptyList();

		Hologram hologram = NiveriaHolograms.instance().hologramManager().hologramByName(fullArgs[1]);
		if (hologram == null)
			return Collections.emptyList();

		if (!(hologram.configuration() instanceof TextHologramConfiguration configuration))
			return Collections.emptyList();

		List<String> text = configuration.text();
		List<String> suggestions = new ArrayList<>();

		if (argIndex == 0) {
			for (int i = 1; i <= text.size(); i++) {
				suggestions.add(Integer.toString(i));
			}
		} else if (argIndex == 1 && args.length > 0) {
			try {
				int lineNumber = Integer.parseInt(args[0]);
				if (lineNumber >= 1 && lineNumber <= text.size()) {
					suggestions.add(text.get(lineNumber - 1));
				}
			} catch (NumberFormatException ignored) {
				// Ignore invalid number format
			}
		}

		return suggestions;
	}
}