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

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

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
                        LANG.sendMessage(player, "command.hologram.edit.doesnt_exist",
                                Lang.unparsedPlaceholder("hologram_name", hologramName)
                        );
                        return Command.SINGLE_SUCCESS;
                    }

                    if (!(hologram.configuration() instanceof ItemHologramConfiguration)) {
                        LANG.sendMessage(player, "command.hologram.edit.only_item");
                        return Command.SINGLE_SUCCESS;
                    }

                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    if (itemStack.getType().isAir() || itemStack.getAmount() < 1) {
                        LANG.sendMessage(player, "command.hologram.edit.item.no_item");
                        return Command.SINGLE_SUCCESS;
                    }

                    hologram.editConfig((ItemHologramConfiguration config) ->
                            config.itemStack(itemStack)
                    );

                    LANG.sendMessage(player, "command.hologram.edit.item.edited",
                            Lang.unparsedPlaceholder("hologram_name", hologramName),
                            Lang.unparsedPlaceholder("hologram_item_item", itemStack.translationKey())
                    );
                    return Command.SINGLE_SUCCESS;
                }).build();
    }
}