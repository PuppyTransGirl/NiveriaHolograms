package toutouchien.niveriaholograms.command.hologram.edit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.hologram.Hologram;
import toutouchien.niveriaholograms.hologram.HologramManager;
import toutouchien.niveriaholograms.utils.CustomLocation;

public class HologramEditRotationCommand extends SubCommand {
	public HologramEditRotationCommand() {
		super(new CommandData("rotation", "niveriaholograms")
				.playerRequired(true)
				.usage("<yaw> <pitch>"));
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

		if (args.length != 2) {
			player.sendMessage(Component.text("/" + label + " " + String.join(" ", fullArgs) + " <yaw> <pitch>", NamedTextColor.RED));
			return;
		}

		float yaw;
		float pitch;
		try {
			yaw = Float.parseFloat(args[0]);
			if (yaw < -180 || yaw > 180 || !Float.isFinite(yaw)) {
				TextComponent errorMessage = MessageUtils.errorMessage(
						Component.text("Le yaw doit être entre -180 et 180.")
				);

				player.sendMessage(errorMessage);
				return;
			}

			pitch = Float.parseFloat(args[1]);
			if (pitch < -90 || pitch > 90 || !Float.isFinite(pitch)) {
				TextComponent errorMessage = MessageUtils.errorMessage(
						Component.text("Le pitch doit être entre -90 et 90.")
				);

				player.sendMessage(errorMessage);
				return;
			}
		} catch (NumberFormatException e) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("La rotation n'est pas valide.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		CustomLocation location = hologram.location();
		location.yaw(yaw)
				.pitch(pitch);

		hologram.teleportTo(location.bukkitLocation());
		hologram.updateForAllPlayers();
		hologramManager.saveHologram(hologram);

		TextComponent successMessage = MessageUtils.successMessage(
				Component.text("La rotation a été modifiée avec succès !")
		);

		player.sendMessage(successMessage);
	}
}