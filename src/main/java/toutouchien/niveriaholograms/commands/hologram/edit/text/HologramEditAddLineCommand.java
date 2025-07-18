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

public class HologramEditAddLineCommand extends SubCommand {
	public HologramEditAddLineCommand() {
		super(new CommandData("addline", "niveriaholograms")
				.playerRequired(true)
				.usage("<texte>"));
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
					Component.text("Tu dois spécifier le texte que tu veux ajouter.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		String newText = String.join(" ", args);
		hologram.editConfig((TextHologramConfiguration config) -> {
			config.addText(newText);
		});

		TextComponent successMessage = MessageUtils.successMessage(
				Component.text("Le texte a été ajouté avec succès !")
		);

		player.sendMessage(successMessage);
	}
}