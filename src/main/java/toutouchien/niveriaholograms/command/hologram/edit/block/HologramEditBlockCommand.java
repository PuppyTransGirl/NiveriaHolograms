package toutouchien.niveriaholograms.command.hologram.edit.block;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configuration.BlockHologramConfiguration;
import toutouchien.niveriaholograms.hologram.Hologram;
import toutouchien.niveriaholograms.hologram.HologramManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HologramEditBlockCommand extends SubCommand {
	public HologramEditBlockCommand() {
		super(new CommandData("block", "niveriaholograms")
				.playerRequired(true)
				.usage("<bloc>"));
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

		if (!(hologram.configuration() instanceof BlockHologramConfiguration configuration)) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Cette comande ne peut être utilisée que sur des hologrammes de bloc")
			);

			player.sendMessage(errorMessage);
			return;
		}

		if (args.length == 0) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Tu dois spécifier le bloc en lequel tu veux changer ton hologramme.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		Material material = Material.matchMaterial(args[0]);
		if (material == null) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Ce bloc n'existe pas.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		if (material.isAir()) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Le bloc ne peut pas être de l'air.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		if (!material.isBlock()) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Ceci n'est pas un bloc.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		configuration.material(material);

		hologram.update();
		hologram.updateForAllPlayers();
		hologramManager.saveHologram(hologram);

		TextComponent successMessage = MessageUtils.successMessage(
				Component.text()
						.append(Component.text("Le bloc a été mit à "))
						.append(Component.text(material.name()))
						.append(Component.text("."))
						.build()
		);

		player.sendMessage(successMessage);
	}

	@Override
	public List<String> complete(@NotNull Player player, String @NotNull [] args, String @NotNull [] fullArgs, int argIndex) {
		if (argIndex != 0)
			return Collections.emptyList();

		Hologram hologram = NiveriaHolograms.instance().hologramManager().hologramByName(fullArgs[1]);
		if (hologram == null || !(hologram.configuration() instanceof BlockHologramConfiguration))
			return Collections.emptyList();

		String currentArg = args[argIndex].toLowerCase(Locale.ROOT);
		return Arrays.stream(Material.values())
				.filter(Material::isBlock)
				.filter(material -> !material.isAir())
				.map(Enum::name)
				.filter(material -> material.toLowerCase(Locale.ROOT).startsWith(currentArg))
				.toList();
	}
}