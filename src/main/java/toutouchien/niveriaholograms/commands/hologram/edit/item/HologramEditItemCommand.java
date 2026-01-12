package toutouchien.niveriaholograms.commands.hologram.edit.item;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.ItemHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

public class HologramEditItemCommand {
	private HologramEditItemCommand() {
		throw new IllegalStateException("Command class");
	}

	public static LiteralCommandNode<CommandSourceStack> get() {
		return Commands.literal("item")
				.requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.item", true))
				.executes(ctx -> {
					Player player = (Player) ctx.getSource().getExecutor();
					String hologramName = ctx.getArgument("hologram", String.class);

					HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
					Hologram hologram = hologramManager.hologramByName(hologramName);
					if (hologram == null) {
						Lang.sendMessage(player, "niveriaholograms.hologram.edit.doesnt_exist", hologramName);
						return Command.SINGLE_SUCCESS;
					}

					if (!(hologram.configuration() instanceof ItemHologramConfiguration)) {
						Lang.sendMessage(player, "niveriaholograms.hologram.edit.only_item");
						return Command.SINGLE_SUCCESS;
					}

					ItemStack itemStack = player.getInventory().getItemInMainHand();
					if (itemStack.getType().isAir() || itemStack.getAmount() < 1) {
						Lang.sendMessage(player, "niveriaholograms.hologram.edit.item.no_item");
						return Command.SINGLE_SUCCESS;
					}

					hologram.editConfig((ItemHologramConfiguration config) -> {
						config.itemStack(itemStack);
					});

					Lang.sendMessage(player, "niveriaholograms.hologram.edit.item.edited", hologramName, itemStack.translationKey());
					return Command.SINGLE_SUCCESS;
				}).build();
	}
}