package toutouchien.niveriaholograms.commands.hologram.basic;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.common.MathUtils;
import toutouchien.niveriaapi.utils.ui.ColorUtils;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;
import toutouchien.niveriaholograms.utils.CustomLocation;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class HologramNearbyCommand extends SubCommand {
	HologramNearbyCommand() {
		super(new CommandData("nearby", "niveriaholograms")
				.aliases("near", "n")
				.playerRequired(true)
				.usage("<range>"));
	}

	@Override
	public void execute(@NotNull Player player, String[] args, @NotNull String label) {
		if (args.length == 0) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Tu dois spécifier le rayon dans lequel chercher les hologrammes.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		int radius;
		try {
			radius = Integer.parseInt(args[0]);
		} catch (NumberFormatException exception) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Le rayon spécifié n'est pas un nombre.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
		List<Map.Entry<Hologram, Double>> nearbyHolograms = hologramManager.holograms().stream()
				.filter(hologram -> hologram.location().world().equals(player.getWorld().getName()))
				.map(hologram -> Map.entry(hologram, hologram.location().distance(player.getLocation())))
				.filter(distance -> distance.getValue() <= radius)
				.sorted(Comparator.comparingDouble(Map.Entry::getValue))
				.toList();

		if (nearbyHolograms.isEmpty()) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Il n'y a aucun hologramme dans un rayon de %s blocs.".formatted(radius))
			);

			player.sendMessage(errorMessage);
			return;
		}

		TextComponent infoMessage = MessageUtils.infoMessage(
				Component.text("Voici la liste des hologrammes aux alentours (%s rayon):".formatted(radius))
		);

		player.sendMessage(infoMessage);

		nearbyHolograms.forEach(entry -> {
			Hologram hologram = entry.getKey();
			double distance = entry.getValue();
			String name = hologram.name();

			CustomLocation location = hologram.location();
			String coordinates = "%s/%s/%s in %s, %s blocks away".formatted(
					MathUtils.decimalRound(location.x(), 2),
					MathUtils.decimalRound(location.y(), 2),
					MathUtils.decimalRound(location.z(), 2),
					location.world(),
					MathUtils.decimalRound(distance, 2)
			);

			TextComponent.Builder playerInfo = Component.text()
					.content(" - ").color(NamedTextColor.DARK_GRAY)
					.append(Component.text(hologram.name(), ColorUtils.primaryColor()))
					.append(Component.text(" (", NamedTextColor.DARK_GRAY))
					.append(Component.text(coordinates, ColorUtils.secondaryColor()))
					.append(Component.text(")", NamedTextColor.DARK_GRAY))
					.clickEvent(ClickEvent.runCommand("/" + label + " teleport " + name))
					.hoverEvent(HoverEvent.showText(Component.text("Clique pour s'y téléporter")));

			player.sendMessage(playerInfo);
		});
	}
}
