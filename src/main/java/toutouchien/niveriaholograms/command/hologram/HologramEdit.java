package toutouchien.niveriaholograms.command.hologram;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.command.hologram.edit.HologramEditBillboard;
import toutouchien.niveriaholograms.command.hologram.edit.HologramEditBrightness;
import toutouchien.niveriaholograms.command.hologram.edit.block.HologramEditBlock;
import toutouchien.niveriaholograms.command.hologram.edit.item.HologramEditItem;
import toutouchien.niveriaholograms.command.hologram.edit.HologramEditPosition;
import toutouchien.niveriaholograms.command.hologram.edit.text.*;
import toutouchien.niveriaholograms.hologram.Hologram;
import toutouchien.niveriaholograms.hologram.HologramManager;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class HologramEdit extends SubCommand {
	public HologramEdit() {
		super(new CommandData("edit", "niveriaholograms")
				.aliases("e")
				.playerRequired(true)
				.usage("<hologram>")
				.subCommands(
						new HologramEditBlock(),

						new HologramEditItem(),

						new HologramEditAddLine(), new HologramEditBackground(), new HologramEditRemoveLine(),
						new HologramEditSeeThrough(), new HologramEditSetLine(), new HologramEditTextAlignment(),
						new HologramEditTextShadow(),

						new HologramEditBillboard(), new HologramEditBrightness(), new HologramEditPosition()
				)
				.hasParameterBeforeSubcommands(true));
	}

	@Override
	public void execute(Player player, String[] args, String label) {
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
	}

	@Override
	public List<String> complete(Player player, String[] args, int argIndex) {
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

			List<String> edits = Arrays.asList("billboard", "brightness", "pitch", "position", "rotate", "scale", "shadowradius", "shadowstrength", "translate", "visibility", "visibilitydistance", "yaw");

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
