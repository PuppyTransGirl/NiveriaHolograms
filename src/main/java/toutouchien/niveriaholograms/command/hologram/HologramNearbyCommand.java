package toutouchien.niveriaholograms.command.hologram;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;

public class HologramNearbyCommand extends SubCommand {
	HologramNearbyCommand() {
		super(new CommandData("nearby", "niveriaholograms")
				.aliases("near", "n")
				.playerRequired(true)
				.usage("<range>"));
	}

	@Override
	public void execute(Player player, String[] args, String label) {
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

/*		HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
		List<Map.Entry<Hologram<?>, Double>> nearbyHolograms = hologramManager.holograms().stream()
				.filter(hologram -> hologram.display().getLocation().getWorld() == player.getWorld())
				.map(hologram -> Map.<Hologram<?>, Double>entry(hologram, hologram.display().getLocation().distance(player.getLocation())))
				.filter(distance -> distance.getValue() <= radius)
				.sorted(Comparator.comparingInt(i -> i.getValue().intValue()))
				.toList();

		if (nearbyHolograms.isEmpty()) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Il n'y a aucun hologramme dans un rayon de %s blocs.".formatted(radius))
			);

			player.sendMessage(errorMessage);
			return;
		}

		TextComponent infoMessage = MessageUtils.infoMessage(
				text("Voici la liste des hologrammes aux alentours (%s rayon):".formatted(radius))
		);

		player.sendMessage(infoMessage);

		nearbyHolograms.forEach(entry -> {
			Hologram<?> hologram = entry.getKey();
			double distance = entry.getValue();
			String name = hologram.name();

			Location location = hologram.display().getLocation();
			String coordinates = "(%s/%s/%s in %s, %s blocks away)".formatted(
					decimalRound(location.getX(), 2),
					decimalRound(location.getY(), 2),
					decimalRound(location.getZ(), 2),
					location.getWorld().getName(),
					decimalRound(distance, 2)
			);

			TextComponent.Builder playerInfo = text()
					.content(" - ").color(NamedTextColor.DARK_GRAY)
					.append(text(hologram.name(), ColorUtils.primaryColor()))
					.append(text(" (", NamedTextColor.DARK_GRAY))
					.append(text(coordinates, ColorUtils.secondaryColor()))
					.append(text(")", NamedTextColor.DARK_GRAY))
					.clickEvent(ClickEvent.runCommand("holo teleport " + name))
					.hoverEvent(HoverEvent.showText(Component.text("Clique pour s'y téléporter")));

			player.sendMessage(playerInfo);
		});*/
	}
}
