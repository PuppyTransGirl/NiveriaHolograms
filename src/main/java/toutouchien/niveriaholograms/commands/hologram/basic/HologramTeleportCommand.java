package toutouchien.niveriaholograms.commands.hologram.basic;

/*
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HologramTeleportCommand extends SubCommand {
	HologramTeleportCommand() {
		super(new CommandData("teleport", "niveriaholograms")
				.aliases("t")
				.playerRequired(true)
				.usage("<hologram>"));
	}

	@Override
	public void execute(@NotNull Player player, String[] args, @NotNull String label) {
		if (args.length == 0) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Tu dois spécifier le nom de l'hologramme auquel tu veux te téléporter.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
		Hologram hologram = hologramManager.hologramByName(args[0]);
		if (hologram == null) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Cet hologramme n'existe pas.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		player.teleportAsync(hologram.location().bukkitLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
		player.sendMessage(MessageUtils.successMessage(Component.text("Tu as été téléporté à " + hologram.name())));
	}

	@Override
	public List<String> complete(@NotNull Player player, String @NotNull [] args, int argIndex) {
		if (argIndex != 0)
			return Collections.emptyList();

		String currentArg = args[argIndex].toLowerCase(Locale.ROOT);
		return NiveriaHolograms.instance().hologramManager().holograms().stream()
				.map(Hologram::name)
				.filter(hologramName -> hologramName.toLowerCase(Locale.ROOT).startsWith(currentArg))
				.toList();
	}
}
*/

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

public class HologramTeleportCommand {
	private HologramTeleportCommand() {
		throw new IllegalStateException("Command class");
	}

	public static LiteralCommandNode<CommandSourceStack> get() {
		return Commands.literal("teleport")
				.requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.teleport", true))
				.then(Commands.argument("hologram", StringArgumentType.word())
						.suggests((ctx, builder) -> {
							HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();

							for (Hologram hologram : hologramManager.holograms())
								builder.suggest(hologram.name());

							return builder.buildFuture();
						})
						.executes(ctx -> {
							Player player = (Player) ctx.getSource().getExecutor();
							String hologramName = StringArgumentType.getString(ctx, "hologram");

							HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
							Hologram hologram = hologramManager.hologramByName(hologramName);
							if (hologram == null) {
								Lang.sendMessage(player, "niveriaholograms.hologram.teleport.doesnt_exist", hologramName);
								return Command.SINGLE_SUCCESS;
							}

							player.teleportAsync(hologram.location().bukkitLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
							Lang.sendMessage(player, "niveriaholograms.hologram.teleport.teleported", hologram.name());
							return Command.SINGLE_SUCCESS;
						})
				).build();
	}
}