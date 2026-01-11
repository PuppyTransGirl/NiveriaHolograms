package toutouchien.niveriaholograms.commands.hologram;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.commands.hologram.basic.*;

public class HologramCommand {
	private HologramCommand() {
		throw new IllegalStateException("Command class");
	}

	public static LiteralCommandNode<CommandSourceStack> get() {
		return Commands.literal("hologram")
				.requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram"))
				.then(HologramCloneCommand.get())
				.then(HologramCreateCommand.get())
				.then(HologramEditCommand.get())
				.then(HologramListCommand.get())
				.then(HologramNearbyCommand.get())
				.then(HologramRemoveCommand.get())
				.then(HologramTeleportCommand.get())
				.then(HologramInfoCommand.get())
				.build();
	}
}