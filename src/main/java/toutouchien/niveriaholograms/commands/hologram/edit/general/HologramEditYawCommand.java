package toutouchien.niveriaholograms.commands.hologram.edit.general;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

public class HologramEditYawCommand {
	private HologramEditYawCommand() {
		throw new IllegalStateException("Command class");
	}

	public static LiteralCommandNode<CommandSourceStack> get() {
		return Commands.literal("yaw")
				.requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.yaw"))
				.then(Commands.argument("yaw", FloatArgumentType.floatArg(-180F, 180F))
						.executes(ctx -> {
							CommandSender sender = CommandUtils.sender(ctx);
							String hologramName = ctx.getArgument("hologram", String.class);
							float yaw = ctx.getArgument("yaw", Float.class);

							HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
							Hologram hologram = hologramManager.hologramByName(hologramName);
							if (hologram == null) {
								Lang.sendMessage(sender, "niveriaholograms.hologram.edit.doesnt_exist", hologramName);
								return Command.SINGLE_SUCCESS;
							}

							hologram.editLocation(location -> {
								location.yaw(yaw);
							});

							Lang.sendMessage(sender, "niveriaholograms.hologram.edit.yaw.edited", hologramName);
							return Command.SINGLE_SUCCESS;
						})
				).build();
	}
}