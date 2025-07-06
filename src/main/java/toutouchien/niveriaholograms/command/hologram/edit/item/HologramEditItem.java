package toutouchien.niveriaholograms.command.hologram.edit.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaapi.utils.MessageUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.hologram.Hologram;
import toutouchien.niveriaholograms.hologram.HologramManager;
import toutouchien.niveriaholograms.hologram.configuration.ItemHologramConfiguration;

public class HologramEditItem extends SubCommand {
	public HologramEditItem() {
		super(new CommandData("item", "niveriaholograms")
				.playerRequired(true));
	}

	@Override
	public void execute(Player player, String[] args, String[] fullArgs, String label) {
		HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
		Hologram hologram = hologramManager.hologramByName(fullArgs[1]);
		if (hologram == null) {
			TextComponent errorMessage = MessageUtils.errorMessage(
					Component.text("Cet hologramme n'existe pas.")
			);

			player.sendMessage(errorMessage);
			return;
		}

		if (!(hologram.configuration() instanceof ItemHologramConfiguration configuration)) {
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

		configuration.itemStack(itemStack);
		hologram.update();
		hologram.updateForAllPlayers();
		hologramManager.saveHologram(hologram);

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