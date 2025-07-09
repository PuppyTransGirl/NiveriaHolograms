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

public class HologramEditYawCommand extends SubCommand {
	public HologramEditYawCommand() {
		super(new CommandData("yaw", "niveriaholograms")
				.playerRequired(true)
				.usage("<yaw>"));
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
			player.sendMessage(Component.text("/" + label + " " + String.join(" ", fullArgs) + " <yaw>", NamedTextColor.RED));
			return;
		}

		float yaw;
		try {
			yaw = Float.parseFloat(args[0]);
			if (yaw < -180 || yaw > 180 || !Float.isFinite(yaw)) {
				TextComponent errorMessage = MessageUtils.errorMessage(
						Component.text("Le yaw doit être entre -180 et 180.")
				);

				player.sendMessage(errorMessage);
				return;
			}
		} catch (NumberFormatException e) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Le yaw doit être entre -180 et 180.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		CustomLocation location = hologram.location();
		location.yaw(yaw);

		hologram.teleportTo(location.bukkitLocation());
		hologram.updateForAllPlayers();
		hologramManager.saveHologram(hologram);

		TextComponent successMessage = MessageUtils.successMessage(
				Component.text("Le yaw a été mis à " + yaw + " avec succès !")
		);

		player.sendMessage(successMessage);
	}
}