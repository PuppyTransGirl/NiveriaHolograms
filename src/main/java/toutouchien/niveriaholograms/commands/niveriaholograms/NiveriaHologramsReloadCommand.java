package toutouchien.niveriaholograms.commands.niveriaholograms;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;

public class NiveriaHologramsReloadCommand extends SubCommand {
	NiveriaHologramsReloadCommand() {
		super(new CommandData("reload", "niveriaholograms"));
	}

	@Override
	public void execute(CommandSender sender, String @NotNull [] args, @NotNull String label) {
		long startMillis = System.currentTimeMillis();
		NiveriaHolograms.instance().reload();
		long timeTaken = System.currentTimeMillis() - startMillis;

		TextComponent successMessage = MessageUtils.successMessage(
				Component.text("NiveriaHolograms a été rechargé avec succès ! (%s ms)".formatted(timeTaken))
		);

		sender.sendMessage(successMessage);
	}
}
