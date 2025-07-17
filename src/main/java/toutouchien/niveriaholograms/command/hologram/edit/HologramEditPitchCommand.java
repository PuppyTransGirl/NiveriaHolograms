package toutouchien.niveriaholograms.command.hologram.edit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.hologram.Hologram;
import toutouchien.niveriaholograms.hologram.HologramManager;

public class HologramEditPitchCommand extends SubCommand {
	public HologramEditPitchCommand() {
		super(new CommandData("pitch", "niveriaholograms")
				.playerRequired(true)
				.usage("<pitch>"));
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

		if (args.length == 0) {
			player.sendMessage(Component.text("/" + label + " " + String.join(" ", fullArgs) + " <pitch>", NamedTextColor.RED));
			return;
		}

		float pitch;
		try {
			pitch = Float.parseFloat(args[0]);
			if (pitch < -90 || pitch > 90 || !Float.isFinite(pitch)) {
				TextComponent errorMessage = MessageUtils.errorMessage(
						Component.text("Le pitch doit être entre -90 et 90.")
				);

				player.sendMessage(errorMessage);
				return;
			}
		} catch (NumberFormatException e) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Le pitch doit être entre -90 et 90.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		hologram.editLocation(location -> {
			location.pitch(pitch);
		});

		TextComponent successMessage = MessageUtils.successMessage(
				Component.text("Le pitch a été mis à " + pitch + " avec succès !")
		);

		player.sendMessage(successMessage);
	}
}