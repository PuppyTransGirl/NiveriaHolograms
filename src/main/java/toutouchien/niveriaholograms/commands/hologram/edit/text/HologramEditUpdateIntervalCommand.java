package toutouchien.niveriaholograms.commands.hologram.edit.text;

/*
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.TextHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

public class HologramEditUpdateIntervalCommand extends SubCommand {
	public HologramEditUpdateIntervalCommand() {
		super(new CommandData("updateinterval", "niveriaholograms")
				.playerRequired(true)
				.usage("<updateinterval>"));
	}

	@Override
	public void execute(@NotNull Player player, String @NotNull [] args, String[] fullArgs, @NotNull String label) {
		HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
		Hologram hologram = hologramManager.hologramByName(fullArgs[1]);
		if (hologram == null) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Cet hologramme n'existe pas.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		if (!(hologram.configuration() instanceof TextHologramConfiguration)) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Cette comande ne peut être utilisée que sur des hologrammes de texte.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		if (args.length == 0) {
			player.sendMessage(Component.text("/" + label + " " + String.join(" ", fullArgs) + " <updateinterval>", NamedTextColor.RED));
			return;
		}

		int updateInterval;
		try {
			updateInterval = Integer.parseInt(args[0]);
			if (updateInterval < 0 || !Double.isFinite(updateInterval)) {
				TextComponent errorMessage = MessageUtils.errorMessage(
						Component.text("L'intervale d'update doit être égal ou au dessus de 0.")
				);

				player.sendMessage(errorMessage);
				return;
			}
		} catch (NumberFormatException e) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("L'intervale d'update doit être égal ou au dessus de 0.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		hologram.editConfig((TextHologramConfiguration config) -> {
			config.updateInterval(updateInterval);
		});

		TextComponent successMessage = MessageUtils.successMessage(
				Component.text("L'intervale d'update a été mis à " + updateInterval + " avec succès !")
		);

		player.sendMessage(successMessage);
	}
}*/

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.TextHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

public class HologramEditUpdateIntervalCommand {
	private HologramEditUpdateIntervalCommand() {
		throw new IllegalStateException("Command class");
	}

	public static LiteralCommandNode<CommandSourceStack> get() {
		return Commands.literal("updateInterval")
				.requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.updateinterval"))
				.then(Commands.argument("updateInterval", IntegerArgumentType.integer(0))
						.executes(ctx -> {
							CommandSender sender = CommandUtils.sender(ctx);
							String hologramName = ctx.getArgument("hologram", String.class);
							int updateInterval = ctx.getArgument("updateInterval", int.class);

							HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
							Hologram hologram = hologramManager.hologramByName(hologramName);
							if (hologram == null) {
								Lang.sendMessage(sender, "niveriaholograms.hologram.edit.doesnt_exist", hologramName);
								return Command.SINGLE_SUCCESS;
							}

							if (!(hologram.configuration() instanceof TextHologramConfiguration)) {
								Lang.sendMessage(sender, "niveriaholograms.hologram.edit.only_text");
								return Command.SINGLE_SUCCESS;
							}

							hologram.editConfig((TextHologramConfiguration config) -> {
								config.updateInterval(updateInterval);
							});

							Lang.sendMessage(sender, "niveriaholograms.hologram.edit.updateInterval.edited", hologramName, updateInterval);
							return Command.SINGLE_SUCCESS;
						})
				).build();
	}
}