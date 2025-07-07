package toutouchien.niveriaholograms.command.hologram.edit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.hologram.Hologram;
import toutouchien.niveriaholograms.hologram.HologramManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HologramEditBillboardCommand extends SubCommand {
	public HologramEditBillboardCommand() {
		super(new CommandData("billboard", "niveriaholograms")
				.playerRequired(true)
				.usage("<billboard>"));
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
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Tu dois spécifier le type de billboard que tu veux mettre.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		Display.Billboard billboard;

		try {
			billboard = Display.Billboard.valueOf(args[0]);
		} catch (IllegalArgumentException e) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Ce type de billboard n'existe pas.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		hologram.configuration().billboard(billboard);

		hologram.update();
		hologram.updateForAllPlayers();
		hologramManager.saveHologram(hologram);

		TextComponent successMessage = MessageUtils.successMessage(
				Component.text()
						.append(Component.text("Le type de billboard a été mit à "))
						.append(Component.text(billboard.name()))
						.append(Component.text("."))
						.build()
		);

		player.sendMessage(successMessage);
	}

	@Override
	public List<String> complete(Player player, String[] args, String[] fullArgs, int argIndex) {
		if (argIndex != 0)
			return Collections.emptyList();

		Hologram hologram = NiveriaHolograms.instance().hologramManager().hologramByName(fullArgs[1]);
		if (hologram == null)
			return Collections.emptyList();

		String currentArg = args[argIndex].toLowerCase(Locale.ROOT);
		return Arrays.stream(Display.Billboard.values())
				.filter(billboard -> billboard != hologram.configuration().billboard())
				.map(Enum::name)
				.filter(billboard -> billboard.toLowerCase(Locale.ROOT).startsWith(currentArg))
				.toList();
	}
}