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

public class HologramEditTranslateCommand extends SubCommand {
	public HologramEditTranslateCommand() {
		super(new CommandData("translate", "niveriaholograms")
				.playerRequired(true)
				.usage("<x y z>"));
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

		if (args.length != 3) {
			player.sendMessage(Component.text("/" + label + " " + String.join(" ", fullArgs) + " <x y z>", NamedTextColor.RED));
			return;
		}

		try {
			Vector3f currentTranslation = hologram.configuration().translation();
			double x = parseDouble(args[0]);
			double y = parseDouble(args[1]);
			double z = parseDouble(args[2]);

			currentTranslation.set(x, y, z);

			hologram.update();
			hologram.updateForAllPlayers();
			hologramManager.saveHologram(hologram);

			TextComponent successMessage = MessageUtils.successMessage(
					Component.text("La translation de l'hologramme a été modifiée avec succès !")
			);

			player.sendMessage(successMessage);
		} catch (NumberFormatException e) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Les coordonnées de translation sont invalides.")
			);

			player.sendMessage(errorMessage);
		}
	}

	private double parseDouble(String coord) {
		double value = Double.parseDouble(coord);
		if (!Double.isFinite(value))
			throw new NumberFormatException("Translation Coordinates is not finite: " + coord);

		return value;
	}
}