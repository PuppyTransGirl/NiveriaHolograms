package toutouchien.niveriaholograms.command.hologram.edit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.joml.Vector3f;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.hologram.Hologram;
import toutouchien.niveriaholograms.hologram.HologramManager;

public class HologramEditScaleCommand extends SubCommand {
	public HologramEditScaleCommand() {
		super(new CommandData("scale", "niveriaholograms")
				.playerRequired(true)
				.usage("<scale|x y z>"));
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
			player.sendMessage(Component.text("/" + label + " " + String.join(" ", fullArgs) + " <scale|x y z>", NamedTextColor.RED));
			return;
		}

		if (args.length == 1) {
			try {
				Vector3f currentScale = hologram.configuration().scale();
				double scale = parseScale(args[0]);

				currentScale.set(scale, scale, scale);

				hologram.update();
				hologram.updateForAllPlayers();
				hologramManager.saveHologram(hologram);

				TextComponent successMessage = MessageUtils.successMessage(
						Component.text("La taille de l'hologramme a été modifiée avec succès !")
				);

				player.sendMessage(successMessage);
			} catch (NumberFormatException e) {
				TextComponent errorMessage = MessageUtils.errorMessage(
						Component.text("La taille est invalide.")
				);

				player.sendMessage(errorMessage);
			}

			return;
		}

		// New coordinate parsing logic
		if (args.length == 3) {
			try {
				Vector3f currentScale = hologram.configuration().scale();
				double x = parseScale(args[0]);
				double y = parseScale(args[1]);
				double z = parseScale(args[2]);

				currentScale.set(x, y, z);

				hologram.update();
				hologram.updateForAllPlayers();
				hologramManager.saveHologram(hologram);

				TextComponent successMessage = MessageUtils.successMessage(
						Component.text("La taille de l'hologramme a été modifiée avec succès !")
				);

				player.sendMessage(successMessage);
			} catch (NumberFormatException e) {
				TextComponent errorMessage = MessageUtils.errorMessage(
						Component.text("Les tailles sont invalides.")
				);

				player.sendMessage(errorMessage);
			}

			return;
		}

		player.sendMessage(Component.text("/" + label + " " + String.join(" ", fullArgs) + " <here|joueur|x y z>", NamedTextColor.RED));
	}

	private double parseScale(String coord) {
		double value = Double.parseDouble(coord);
		if (value <= 0 || !Double.isFinite(value))
			throw new NumberFormatException("Coordinate is not finite: " + coord);

		return value;
	}
}