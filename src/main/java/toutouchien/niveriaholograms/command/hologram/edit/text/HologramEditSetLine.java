package toutouchien.niveriaholograms.command.hologram.edit.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configuration.TextHologramConfiguration;
import toutouchien.niveriaholograms.hologram.Hologram;
import toutouchien.niveriaholograms.hologram.HologramManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HologramEditSetLine extends SubCommand {
	public HologramEditSetLine() {
		super(new CommandData("removeline", "niveriaholograms")
				.playerRequired(true)
				.usage("<ligne>"));
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

		File file = new File(NiveriaHolograms.instance().getDataFolder(), "holograms.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		ConfigurationSection hologramSection = config.getConfigurationSection("holograms." + hologram.name());

		List<String> text = hologramSection.getStringList("text");
		if (lineNumber < 1 || lineNumber > text.size()) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Le numéro de la ligne n'est pas valide.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		MiniMessage miniMessage = MiniMessage.miniMessage();
		Component newText = miniMessage.deserialize(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));

		String[] textLines = text.toArray(new String[0]);
		TextComponent.Builder builder = Component.text();

		boolean isFirst = true;
		for (int i = 0; i < textLines.length; i++) {
			if (!isFirst)
				builder.appendNewline();

			if (i + 1 == lineNumber)
				builder.append(newText);
			else
				builder.append(miniMessage.deserialize(textLines[i]));

			isFirst = false;
		}

		configuration.text(builder.build());
		hologram.update();
		hologram.updateForAllPlayers();
		hologramManager.saveHologram(hologram);

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
	public List<String> complete(Player player, String[] args, String[] fullArgs, int argIndex) {
		if (argIndex > 1)
			return Collections.emptyList();

		Hologram hologram = NiveriaHolograms.instance().hologramManager().hologramByName(fullArgs[1]);
		if (hologram == null)
			return Collections.emptyList();

		if (!(hologram.configuration() instanceof TextHologramConfiguration))
			return Collections.emptyList();

		File file = new File(NiveriaHolograms.instance().getDataFolder(), "holograms.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		ConfigurationSection hologramSection = config.getConfigurationSection("holograms." + hologram.name());

		List<String> text = hologramSection.getStringList("text");
		List<String> suggestions = new ArrayList<>();

		if (argIndex == 0) {
			for (int i = 1; i <= text.size(); i++)
				suggestions.add(String.valueOf(i));
		} else if (argIndex == 1 && args.length > 0) {
			try {
				int lineNumber = Integer.parseInt(args[0]);
				if (lineNumber >= 1 && lineNumber <= text.size()) {
					String currentText = text.get(lineNumber - 1);
					suggestions.add(currentText);
				}
			} catch (NumberFormatException ignored) {
			}
		}

		return suggestions;
	}
}