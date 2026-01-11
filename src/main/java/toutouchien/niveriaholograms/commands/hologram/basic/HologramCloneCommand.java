package toutouchien.niveriaholograms.commands.hologram.basic;

/*
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.data.FileUtils;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HologramCloneCommand extends SubCommand {
	public HologramCloneCommand() {
		super(new CommandData("clone", "niveriaholograms")
				.playerRequired(true)
				.usage("<hologram> <nom>"));
	}

	@Override
	public void execute(@NotNull Player player, String[] args, @NotNull String label) {
		if (args.length == 0) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Tu dois spécifier le nom de l'hologramme que tu veux cloner.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		if (args.length != 2) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Tu dois spécifier le nom du nouvel hologramme.")
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

		String holoName = args[1];
		int length = holoName.length();
		if (length > 64) {
			MessageUtils.sendErrorMessage(player, Component.text("Le nom de l'hologramme ne peut pas dépasser 64 caractères."));
			return;
		}

		if (hologramManager.hologramExists(holoName)) {
			MessageUtils.sendErrorMessage(player, Component.text("Un hologramme avec ce nom existe déjà."));
			return;
		}

		String[] invalidCharacters = FileUtils.invalidCharacters();
		for (String invalidCharacter : invalidCharacters) {
			if (!holoName.contains(invalidCharacter))
				continue;

			MessageUtils.sendErrorMessage(player, Component.text("Le nom de l'hologramme contient un caractère non autorisé. (%s)".formatted(invalidCharacter)));
			MessageUtils.sendInfoMessage(player, Component.text("Les caractères non autorisés sont " + Arrays.toString(invalidCharacters)
					.replace("[", "")
					.replace("]", "")
					.replace(",", "")));
			return;
		}

		Hologram clone = new Hologram(hologram, player, holoName);
		clone.create();
		clone.createForAllPlayers();

		hologramManager.saveHologram(hologram);
		hologramManager.addHologram(hologram);

		player.sendMessage(MessageUtils.successMessage(Component.text("Hologramme cloné avec succès !")));
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
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

public class HologramCloneCommand {
    private HologramCloneCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("clone")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.clone", true))
                .then(Commands.argument("hologram", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();

                            for (Hologram hologram : hologramManager.holograms())
                                builder.suggest(hologram.name());

                            return builder.buildFuture();
                        })
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(ctx -> {
                                    Player player = (Player) ctx.getSource().getExecutor();
                                    String hologramName = ctx.getArgument("hologram", String.class);
                                    String newHologramName = ctx.getArgument("name", String.class);

                                    HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                                    Hologram hologram = hologramManager.hologramByName(hologramName);

                                    boolean isValidHologram = isValidHologram(hologram, player, hologramName, newHologramName, hologramManager);
                                    if (!isValidHologram)
                                        return Command.SINGLE_SUCCESS;

                                    Hologram clone = new Hologram(hologram, player, newHologramName);
                                    clone.create();
                                    clone.createForAllPlayers();

                                    hologramManager.saveHologram(hologram);
                                    hologramManager.addHologram(hologram);

                                    Lang.sendMessage(player, "niveriaholograms.hologram.clone.cloned");
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .build();
    }

    private static boolean isValidHologram(@Nullable Hologram hologram, @NotNull Player player, @NotNull String hologramName, @NotNull String newHologramName, @NotNull HologramManager hologramManager) {
        if (hologram == null) {
            Lang.sendMessage(player, "niveriaholograms.hologram.clone.doesnt_exist", hologramName);
            return false;
        }

        int length = newHologramName.length();
        if (length > 64) {
            Lang.sendMessage(player, "niveriaholograms.hologram.clone.name_too_long");
            return false;
        }

        if (newHologramName.contains(".") || newHologramName.contains("+")) {
            Lang.sendMessage(player, "niveriaholograms.hologram.clone.invalid_characters");
            return false;
        }

        if (hologramManager.hologramExists(newHologramName)) {
            Lang.sendMessage(player, "niveriaholograms.hologram.clone.already_exists", newHologramName);
            return false;
        }

        return true;
    }
}