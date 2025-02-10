package toutouchien.niveriaholograms.command.hologram;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ColorUtils;
import toutouchien.niveriaapi.utils.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.hologram.Hologram;
import toutouchien.niveriaholograms.hologram.HologramManager;
import toutouchien.niveriaholograms.utils.MathUtils;

import static net.kyori.adventure.text.Component.text;
import static toutouchien.niveriaholograms.utils.MathUtils.decimalRound;

public class HologramList extends SubCommand {
	HologramList(Plugin plugin) {
		super(new CommandData("list", plugin)
				.aliases("l"));
	}

	@Override
	public void execute(CommandSender sender, String[] args, String label) {
		TextComponent infoMessage = MessageUtils.infoMessage(
				text("Voici la liste des hologrammes:")
		);

		sender.sendMessage(infoMessage);

		HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();

		for (Hologram<?> hologram : hologramManager.holograms()) {
			String name = hologram.name();

			Location location = hologram.display().getLocation();
			String coordinates = "(%s/%s/%s in %s)".formatted(
					decimalRound(location.getX(), 2),
					decimalRound(location.getY(), 2),
					decimalRound(location.getZ(), 2),
					location.getWorld().getName()
			);

			TextComponent.Builder playerInfo = text()
					.content(" - ").color(NamedTextColor.DARK_GRAY)
					.append(text(hologram.name(), ColorUtils.primaryColor()))
					.append(text(" (", NamedTextColor.DARK_GRAY))
					.append(text(coordinates, ColorUtils.secondaryColor()))
					.append(text(")", NamedTextColor.DARK_GRAY))
					.clickEvent(ClickEvent.runCommand("holo teleport " + name))
					.hoverEvent(HoverEvent.showText(Component.text("Clique pour s'y téléporter")));

			sender.sendMessage(playerInfo);
		}
	}
}
