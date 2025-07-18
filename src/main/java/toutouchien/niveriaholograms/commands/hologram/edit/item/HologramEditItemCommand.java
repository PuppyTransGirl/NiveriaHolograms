package toutouchien.niveriaholograms.commands.hologram.edit.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.ui.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.ItemHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

public class HologramEditItemCommand extends SubCommand {
	public HologramEditItemCommand() {
		super(new CommandData("item", "niveriaholograms")
				.playerRequired(true));
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

		if (!(hologram.configuration() instanceof ItemHologramConfiguration)) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Cette comande ne peut être utilisée que sur des hologrammes d'item.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		ItemStack itemStack = player.getInventory().getItemInMainHand();
		if (itemStack.getType().isAir() || itemStack.getAmount() < 1) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Tu dois tenir un item dans ta main.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		hologram.editConfig((ItemHologramConfiguration config) -> {
			config.itemStack(itemStack);
		});

		TextComponent successMessage = MessageUtils.successMessage(
				Component.text()
						.append(Component.text("L'item a été mit à "))
						.append(itemStack.displayName())
						.append(Component.text("."))
						.build()
		);

		player.sendMessage(successMessage);
	}
}