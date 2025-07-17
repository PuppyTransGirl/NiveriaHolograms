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

public class HologramEditShadowRadiusCommand extends SubCommand {
	public HologramEditShadowRadiusCommand() {
		super(new CommandData("shadowradius", "niveriaholograms")
				.playerRequired(true)
				.usage("<radius>"));
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
			player.sendMessage(Component.text("/" + label + " " + String.join(" ", fullArgs) + " <radius>", NamedTextColor.RED));
			return;
		}

		float shadowRadius;
		try {
			shadowRadius = Float.parseFloat(args[0]);
			if (!Float.isFinite(shadowRadius)) {
				TextComponent errorMessage = MessageUtils.errorMessage(
						Component.text("Le rayon est invalide.")
				);

				player.sendMessage(errorMessage);
				return;
			}
		} catch (NumberFormatException e) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Le rayon est invalide.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		hologram.editConfig(config -> {
			config.shadowRadius(shadowRadius);
		});

		TextComponent successMessage = MessageUtils.successMessage(
				Component.text("Le rayon d'ombre a été mis à " + shadowRadius + " avec succès !")
		);

		player.sendMessage(successMessage);
	}
}