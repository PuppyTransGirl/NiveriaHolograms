package toutouchien.niveriaholograms.command.hologram;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.hologram.Hologram;
import toutouchien.niveriaholograms.hologram.HologramManager;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HologramRemoveCommand extends SubCommand {
	public HologramRemoveCommand() {
		super(new CommandData("remove", "niveriaholograms")
				.playerRequired(true)
				.usage("<hologram>"));
	}

	@Override
	public void execute(@NotNull Player player, String[] args, @NotNull String label) {
		if (args.length == 0) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Tu dois spécifier le nom de l'hologramme que tu veux retirer.")
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

		hologramManager.delete(hologram);
		player.sendMessage(MessageUtils.successMessage(Component.text("Hologramme supprimé avec succès !")));
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
