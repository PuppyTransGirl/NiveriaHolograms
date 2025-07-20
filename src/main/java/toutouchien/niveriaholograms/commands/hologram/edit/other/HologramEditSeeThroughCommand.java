package toutouchien.niveriaholograms.commands.hologram.edit.other;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.LeaderboardHologramConfiguration;
import toutouchien.niveriaholograms.configurations.TextHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

import java.util.Collections;
import java.util.List;

public class HologramEditSeeThroughCommand extends SubCommand {
	public HologramEditSeeThroughCommand() {
		super(new CommandData("seethrough", "niveriaholograms")
				.playerRequired(true)
				.usage("<true|false>"));
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

		if (!(hologram.configuration() instanceof LeaderboardHologramConfiguration) && !(hologram.configuration() instanceof TextHologramConfiguration)) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Cette comande ne peut être utilisée que sur des hologrammes de classement et de texte.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		if (args.length == 0) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Tu dois spécifier si tu veux que l'on voit à travers l'hologramme ou non.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		boolean seeThrough = Boolean.parseBoolean(args[0]);
		if (hologram.configuration() instanceof LeaderboardHologramConfiguration) {
			hologram.editConfig((LeaderboardHologramConfiguration config) -> {
				config.seeThrough(seeThrough);
			});
		} else if (hologram.configuration() instanceof TextHologramConfiguration) {
			hologram.editConfig((TextHologramConfiguration config) -> {
				config.seeThrough(seeThrough);
			});
		}

		TextComponent successMessage = MessageUtils.successMessage(
				Component.text("La transparence a été " + (seeThrough ? "activée" : "désactivée") + ".")
		);

		player.sendMessage(successMessage);
	}

	@Override
	public List<String> complete(@NotNull Player player, String @NotNull [] args, String @NotNull [] fullArgs, int argIndex) {
		if (argIndex != 0)
			return Collections.emptyList();

		Hologram hologram = NiveriaHolograms.instance().hologramManager().hologramByName(fullArgs[1]);
		if (hologram == null || (!(hologram.configuration() instanceof LeaderboardHologramConfiguration) && !(hologram.configuration() instanceof TextHologramConfiguration)))
			return Collections.emptyList();

		return Collections.singletonList(String.valueOf(
				hologram.configuration() instanceof LeaderboardHologramConfiguration
						? !((LeaderboardHologramConfiguration) hologram.configuration()).seeThrough()
						: !((TextHologramConfiguration) hologram.configuration()).seeThrough()
		));
	}
}