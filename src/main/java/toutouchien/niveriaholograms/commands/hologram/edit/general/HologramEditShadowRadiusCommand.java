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

public class HologramEditShadowRadiusCommand {
	private HologramEditShadowRadiusCommand() {
		throw new IllegalStateException("Command class");
	}

	public static LiteralCommandNode<CommandSourceStack> get() {
		return Commands.literal("shadowRadius")
				.requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.shadowRadius"))
				.then(Commands.argument("shadowRadius", FloatArgumentType.floatArg())
						.executes(ctx -> {
							CommandSender sender = CommandUtils.sender(ctx);
							String hologramName = ctx.getArgument("hologram", String.class);
							float shadowRadius = ctx.getArgument("shadowRadius", Float.class);

							HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
							Hologram hologram = hologramManager.hologramByName(hologramName);
							if (hologram == null) {
								Lang.sendMessage(sender, "niveriaholograms.hologram.edit.doesnt_exist", hologramName);
								return Command.SINGLE_SUCCESS;
							}

							hologram.editConfig(config -> {
								config.shadowRadius(shadowRadius);
							});

							Lang.sendMessage(sender, "niveriaholograms.hologram.edit.shadowRadius.edited", hologramName, shadowRadius);
							return Command.SINGLE_SUCCESS;
						})
				).build();
	}
}