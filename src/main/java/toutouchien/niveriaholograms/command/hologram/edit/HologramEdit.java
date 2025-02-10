package toutouchien.niveriaholograms.command.hologram.edit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.hologram.Hologram;
import toutouchien.niveriaholograms.hologram.HologramManager;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class HologramEdit extends SubCommand {
	HologramEdit(Plugin plugin) {
		super(new CommandData("edit", plugin)
				.aliases("e")
				.playerRequired(true)
				.usage("<hologram>"));
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
		Hologram<?> hologram = hologramManager.hologramByName(args[0]);
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

		Hologram<?> hologram = NiveriaHolograms.instance().hologramManager().hologramByName(args[0]);
		if (hologram == null)
			return Collections.emptyList();

		Display display = hologram.display();

		if (argIndex == 1) {
			List<String> edits = Arrays.asList("position", "movehere", "center", "moveto", "rotate", "yaw", "pitch", "billboard", "scale", "translate", "shadowRadius", "shadowStrength");

			edits.addAll(switch (display.getType()) {
				case BLOCK_DISPLAY -> List.of("block");
				case ITEM_DISPLAY -> List.of("item");
				case TEXT_DISPLAY -> List.of("background", "textshadow", "textalignment", "seethrough", "addline", "setline", "removeline", "insertbefore", "insertafter", "updatetextinterval");

				default -> Collections.emptyList();
			});

			return edits.stream()
					.filter(edit -> edit.toLowerCase(Locale.ROOT).startsWith(currentArg.toLowerCase(Locale.ROOT)))
					.toList();
		}

		if (argIndex == 2) {
			Stream<String> completions = switch(args[1]) {
				case "billboard" -> {
					List<Display.Billboard> values = new ArrayList<>(List.of(Display.Billboard.values()));
					values.remove(display.getBillboard());

					yield values.stream().map(Enum::name);
				}

				case "setline", "removeline" -> {
					if (!(display instanceof TextDisplay textDisplay))
						yield null;

					String[] lines = MiniMessage.miniMessage().serialize(textDisplay.text()).split("<br>");

					yield IntStream.range(0, lines.length)
							.mapToObj(i -> String.valueOf(i + 1));
				}

				case "textshadow" -> {
					if (!(display instanceof TextDisplay textDisplay))
						yield null;

					yield Stream.of(!textDisplay.isShadowed()).map(Objects::toString);
				}

				case "textalignment" -> {
					if (!(display instanceof TextDisplay textDisplay))
						yield null;

					List<TextDisplay.TextAlignment> values = new ArrayList<>(List.of(TextDisplay.TextAlignment.values()));
					values.remove(textDisplay.getAlignment());

					yield values.stream().map(Enum::name);
				}

                default -> null;
            };

			if (completions == null)
				return Collections.emptyList();

			return completions.filter(completion -> completion.toLowerCase(Locale.ROOT).startsWith(currentArg)).toList();
		}

		return Collections.emptyList();
	}
}
