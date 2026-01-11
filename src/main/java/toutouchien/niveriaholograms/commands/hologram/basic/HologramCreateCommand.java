package toutouchien.niveriaholograms.commands.hologram.basic;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.common.StringUtils;
import toutouchien.niveriaapi.utils.data.FileUtils;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.core.HologramType;
import toutouchien.niveriaholograms.managers.HologramManager;

import java.util.*;

public class HologramCreateCommand extends SubCommand {
	public HologramCreateCommand() {
		super(new CommandData("create", "niveriaholograms")
				.playerRequired(true)
				.usage("<type> <nom>"));
	}

	@Override
	public void execute(@NotNull Player player, String[] args, @NotNull String label) {
		if (args.length == 0) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Tu dois spécifier le type d'hologramme que tu veux créer.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		if (args.length != 2) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Tu dois spécifier le nom de l'hologramme.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		Optional<HologramType> hologramType = StringUtils.match(args[0], HologramType.class);
		if (hologramType.isEmpty()) {
			MessageUtils.sendErrorMessage(player, Component.text("Le type d'hologramme spécifié n'existe pas."));
			return;
		}

		HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
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

		hologramManager.create(player, hologramType.get(), holoName);
		player.sendMessage(MessageUtils.successMessage(Component.text("Hologramme créé avec succès !")));
	}

	@Override
	public List<String> complete(@NotNull Player player, String @NotNull [] args, int argIndex) {
        if (argIndex != 0)
            return Collections.emptyList();

		String currentArg = args[argIndex].toLowerCase(Locale.ROOT);
        return Arrays.stream(HologramType.values())
                .map(hologramType -> StringUtils.capitalize(hologramType.name()))
                .filter(hologramName -> hologramName.startsWith(currentArg))
                .toList();
    }
}
