package toutouchien.niveriaholograms.commands.hologram;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.commands.hologram.edit.*;
import toutouchien.niveriaholograms.commands.hologram.edit.block.HologramEditBlockCommand;
import toutouchien.niveriaholograms.commands.hologram.edit.item.HologramEditItemCommand;
import toutouchien.niveriaholograms.commands.hologram.edit.text.*;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HologramEditCommand extends SubCommand {
	public HologramEditCommand() {
		super(new CommandData("edit", "niveriaholograms")
				.aliases("e")
				.playerRequired(true)
				.usage("<hologram>")
				.subCommands(
						// Block Holograms
						new HologramEditBlockCommand(),

						// Items Holograms
						new HologramEditItemCommand(),

						// Text Holograms
						new HologramEditAddLineCommand(), new HologramEditBackgroundCommand(), new HologramEditInsertAfterCommand(),
						new HologramEditInsertBeforeCommand(), new HologramEditRemoveLineCommand(), new HologramEditSeeThroughCommand(),
						new HologramEditSetLineCommand(), new HologramEditTextAlignmentCommand(), new HologramEditTextShadowCommand(),

						// General Hologram Edits
						new HologramEditBillboardCommand(), new HologramEditBrightnessCommand(), new HologramEditPitchCommand(),
						new HologramEditPositionCommand(), new HologramEditRotationCommand(), new HologramEditScaleCommand(),
						new HologramEditShadowRadiusCommand(), new HologramEditShadowStrengthCommand(), new HologramEditTranslateCommand(),
						new HologramEditVisibilityDistanceCommand(), new HologramEditYawCommand()
				)
				.hasParameterBeforeSubcommands(true));
	}

	@Override
	public void execute(@NotNull Player player, String[] args, @NotNull String label) {
		if (args.length == 0) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Tu dois spécifier le nom de l'hologramme que tu veux modifier.")
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

		List<String> edits = new ArrayList<>(List.of("billboard", "brightness", "pitch", "position", "rotation", "scale", "shadowradius", "shadowstrength", "translate", "visibility", "visibilitydistance", "yaw"));

		edits.addAll(switch (hologram.type()) {
			case BLOCK -> List.of("block");
			case ITEM -> List.of("item");
			case TEXT -> List.of("addline", "background", "insertafter", "insertbefore", "removeline", "seethrough", "setline", "textalignment", "textshadow", "updatetextinterval");
		});

		TextComponent errorMessage = MessageUtils.errorMessage(
				Component.text("Tu n'as pas spécifié de sous-commande.")
		);

		TextComponent infoMessage = MessageUtils.infoMessage(
				Component.text("Les sous-commandes possibles sont " + String.join(", ", edits) + ".")
		);

		player.sendMessage(errorMessage);
		player.sendMessage(infoMessage);
	}

	@Override
	public List<String> complete(@NotNull Player player, String[] args, int argIndex) {
		String currentArg = args[argIndex].toLowerCase(Locale.ROOT);
		if (argIndex == 0) {
			return NiveriaHolograms.instance().hologramManager().holograms().stream()
					.map(Hologram::name)
					.filter(hologramName -> hologramName.toLowerCase(Locale.ROOT).startsWith(currentArg))
					.toList();
		}

		if (argIndex == 1) {
			Hologram hologram = NiveriaHolograms.instance().hologramManager().hologramByName(args[0]);
			if (hologram == null)
				return Collections.emptyList();

			List<String> edits = new ArrayList<>(List.of("billboard", "brightness", "pitch", "position", "rotation", "scale", "shadowradius", "shadowstrength", "translate", "visibility", "visibilitydistance", "yaw"));

			edits.addAll(switch (hologram.type()) {
				case BLOCK -> List.of("block");
				case ITEM -> List.of("item");
				case TEXT -> List.of("addline", "background", "insertafter", "insertbefore", "removeline", "seethrough", "setline", "textalignment", "textshadow", "updatetextinterval");
			});

			return edits.stream()
					.filter(edit -> edit.toLowerCase(Locale.ROOT).startsWith(currentArg.toLowerCase(Locale.ROOT)))
					.toList();
		}

		return Collections.emptyList();
	}
}
