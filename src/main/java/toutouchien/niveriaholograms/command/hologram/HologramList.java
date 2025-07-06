package toutouchien.niveriaholograms.command.hologram;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.ColorUtils;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.hologram.Hologram;
import toutouchien.niveriaholograms.hologram.HologramManager;
import toutouchien.niveriaholograms.utils.CustomLocation;

import static net.kyori.adventure.text.Component.text;
import static toutouchien.niveriaholograms.utils.MathUtils.decimalRound;

public class HologramList extends SubCommand {
	HologramList() {
		super(new CommandData("list", "niveriaholograms")
				.aliases("l"));
	}

	@Override
	public void execute(CommandSender sender, String[] args, String label) {
		TextComponent infoMessage = MessageUtils.infoMessage(
				text("Voici la liste des hologrammes:")
		);

		sender.sendMessage(infoMessage);

		HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();

		for (Hologram hologram : hologramManager.holograms()) {
			String name = hologram.name();
			CustomLocation location = hologram.location();

			TextComponent.Builder hologramInfo = text()
					.content(" - ").color(NamedTextColor.DARK_GRAY)
					.append(text(hologram.name(), ColorUtils.primaryColor()))
					.append(text(" (", NamedTextColor.DARK_GRAY))
					.append(text(decimalRound(location.x(), 2), ColorUtils.secondaryColor()))
					.append(text("/", NamedTextColor.GRAY))
					.append(text(decimalRound(location.y(), 2), ColorUtils.secondaryColor()))
					.append(text("/", NamedTextColor.GRAY))
					.append(text(decimalRound(location.z(), 2), ColorUtils.secondaryColor()))
					.append(text(" in ", NamedTextColor.GRAY))
					.append(text(location.world(), ColorUtils.secondaryColor()))
					.append(text(")", NamedTextColor.DARK_GRAY))
					.clickEvent(ClickEvent.runCommand("/holo teleport " + name))
					.hoverEvent(HoverEvent.showText(Component.text("Clique pour s'y téléporter")));

			sender.sendMessage(hologramInfo);
		}
	}
}
