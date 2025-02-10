package toutouchien.niveriaholograms.command.niveriaholograms;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;

public class NiveriaHologramsReload extends SubCommand {
	NiveriaHologramsReload(Plugin plugin) {
		super(new CommandData("reload", plugin));
	}

	@Override
	public void execute(CommandSender sender, String[] args, String label) {
		long startMillis = System.currentTimeMillis();
		NiveriaHolograms.instance().reload();
		long timeTaken = System.currentTimeMillis() - startMillis;

		TextComponent successMessage = MessageUtils.successMessage(
				Component.text("NiveriaHolograms a été rechargé avec succès ! (%s ms)".formatted(timeTaken))
		);

		sender.sendMessage(successMessage);
	}
}
