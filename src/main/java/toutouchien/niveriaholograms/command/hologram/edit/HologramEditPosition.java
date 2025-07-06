package toutouchien.niveriaholograms.command.hologram.edit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.hologram.Hologram;
import toutouchien.niveriaholograms.hologram.HologramManager;
import toutouchien.niveriaholograms.utils.CustomLocation;
import toutouchien.niveriaholograms.utils.MathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HologramEditPosition extends SubCommand {
	public HologramEditPosition() {
		super(new CommandData("position", "niveriaholograms")
				.playerRequired(true)
				.usage("<here|joueur|x y z>"));
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
			player.sendMessage(Component.text("/" + label + " edit " + fullArgs[1] + " <here|joueur|x y z>", NamedTextColor.RED));
			return;
		}

		String arg = args[0];

		if (arg.equalsIgnoreCase("here")) {
			hologram.teleportTo(player.getLocation());
			hologram.updateForAllPlayers();
			hologramManager.saveHologram(hologram);

			TextComponent successMessage = MessageUtils.successMessage(
					Component.text("L'hologramme a été déplacé avec succès !")
			);

			player.sendMessage(successMessage);
			return;
		}

		Player p = Bukkit.getPlayerExact(arg);
		if (p != null) {
			hologram.teleportTo(p.getLocation());
			hologram.updateForAllPlayers();
			hologramManager.saveHologram(hologram);

			TextComponent successMessage = MessageUtils.successMessage(
					Component.text("L'hologramme a été déplacé avec succès !")
			);

			player.sendMessage(successMessage);
			return;
		}

		// New coordinate parsing logic
		try {
			if (args.length == 3) {
				CustomLocation currentLocation = hologram.location();
				double x = parseCoordinate(args[0], currentLocation.x());
				double y = parseCoordinate(args[1], currentLocation.y());
				double z = parseCoordinate(args[2], currentLocation.z());

				currentLocation.x(x);
				currentLocation.y(y);
				currentLocation.z(z);

				hologram.teleportTo(currentLocation.bukkitLocation());
				hologram.updateForAllPlayers();
				hologramManager.saveHologram(hologram);

				TextComponent successMessage = MessageUtils.successMessage(
						Component.text("L'hologramme a été déplacé avec succès !")
				);

				player.sendMessage(successMessage);
				return;
			}
		} catch (NumberFormatException e) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Les coordonnées sont invalides.")
			);

			player.sendMessage(errorMessage);
		}

		player.sendMessage(Component.text("/" + label + " edit " + fullArgs[1] + " <here|joueur|x y z>", NamedTextColor.RED));
	}

	private double parseCoordinate(String coord, double currentValue) {
		if (!coord.startsWith("~"))
			return Double.parseDouble(coord);

		if (coord.length() == 1)
			return currentValue;

		return currentValue + Double.parseDouble(coord.substring(1));

	}

	@Override
	public List<String> complete(Player player, String[] args, String[] fullArgs, int argIndex) {
		List<String> completions = new ArrayList<>();

		if (argIndex == 0) {
			completions.addAll(List.of("here", "~0", "~"));

			completions.addAll(Bukkit.getOnlinePlayers().stream()
					.filter(p -> !p.getUniqueId().equals(player.getUniqueId()))
					.map(Player::getName)
					.toList());
		}

		if (argIndex < 5) {
			Hologram hologram = NiveriaHolograms.instance().hologramManager().hologramByName(fullArgs[1]);
			if (hologram == null)
				return Collections.emptyList();

			CustomLocation currentLocation = hologram.location();

			completions.add("~");
			completions.add("~0");

			switch(argIndex) {
				case 0 -> completions.add(String.valueOf(MathUtils.decimalRound(currentLocation.x(), 2)));
				case 1 -> completions.add(String.valueOf(MathUtils.decimalRound(currentLocation.y(), 2)));
				case 2 -> completions.add(String.valueOf(MathUtils.decimalRound(currentLocation.z(), 2)));
			}
		}

		return completions.stream()
				.filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args[argIndex].toLowerCase(Locale.ROOT)))
				.toList();
	}
}