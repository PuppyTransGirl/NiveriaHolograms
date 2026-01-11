package toutouchien.niveriaholograms.commands.niveriaholograms;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;

public class NiveriaHologramsCommand {
	private NiveriaHologramsCommand() {
		throw new IllegalStateException("Command class");
	}

	public static LiteralCommandNode<CommandSourceStack> get() {
		return Commands.literal("niveriaholograms")
				.requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.niveriaholograms"))
				.then(reloadCommand())
				.build();
	}

	private static LiteralArgumentBuilder<CommandSourceStack> reloadCommand() {
		return Commands.literal("reload")
				.requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.niveriaholograms.reload"))
				.executes(ctx -> {
					CommandSender sender = CommandUtils.sender(ctx);

					long startMillis = System.currentTimeMillis();
					NiveriaHolograms.instance().reload();
					long timeTaken = System.currentTimeMillis() - startMillis;
					Lang.sendMessage(sender, "niveriaholograms.reload.done", timeTaken);

					return Command.SINGLE_SUCCESS;
				});
	}
}