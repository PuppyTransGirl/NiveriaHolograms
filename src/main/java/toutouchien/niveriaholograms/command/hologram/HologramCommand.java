package toutouchien.niveriaholograms.command.hologram;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import toutouchien.niveriaapi.command.Command;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.utils.ui.MessageUtils;

public class HologramCommand extends Command {
	public HologramCommand() {
		super(new CommandData("hologram", "niveriaholograms")
				.aliases("holo", "nholo")
				.description("Gère les hologrammes")
				.usage("<list|nearby|teleport|create|remove|clone|edit>")
				.subCommands(
						new HologramCloneCommand(), new HologramEditCommand(), new HologramListCommand(),
						new HologramNearbyCommand(), new HologramRemoveCommand(), new HologramTeleportCommand()
				));
	}

	@Override
	public void execute(CommandSender sender, String[] args, String label) {
		TextComponent errorMessage = MessageUtils.errorMessage(
				Component.text("Tu n'as pas spécifié de sous-commande.")
		);

		TextComponent infoMessage = MessageUtils.infoMessage(
				Component.text("Les sous-commandes possibles sont list, nearby, teleport, create, remove, clone, edit.")
		);

		sender.sendMessage(errorMessage);
		sender.sendMessage(infoMessage);
	}
}
