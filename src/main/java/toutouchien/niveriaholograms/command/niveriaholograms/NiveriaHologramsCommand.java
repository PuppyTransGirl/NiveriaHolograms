package toutouchien.niveriaholograms.command.niveriaholograms;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.Command;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.utils.ui.MessageUtils;

public class NiveriaHologramsCommand extends Command {
	public NiveriaHologramsCommand() {
		super(new CommandData("niveriaholograms", "niveriaholograms")
				.description("Permet de gérer le plugin d'hologrammes de Niveria.")
				.usage("<reload>")
				.subCommands(new NiveriaHologramsReloadCommand(), new NiveriaHologramsTestCommand()));
	}

	@Override
	public void execute(CommandSender sender, String @NotNull [] args, @NotNull String label) {
		TextComponent errorMessage = MessageUtils.errorMessage(
				Component.text("Tu n'as pas spécifié de sous-commande.")
		);

		TextComponent infoMessage = MessageUtils.infoMessage(
				Component.text("Les sous-commandes possibles sont reload, test.")
		);

		sender.sendMessage(errorMessage);
		sender.sendMessage(infoMessage);
	}
}
