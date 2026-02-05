package toutouchien.niveriaholograms.commands.hologram.edit.block;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.BlockHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

public class HologramEditBlockCommand {
    private HologramEditBlockCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("block")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.block"))
                .then(Commands.argument("block", ArgumentTypes.blockState())
                        .executes(ctx -> {
                            CommandSender sender = CommandUtils.sender(ctx);
                            String hologramName = ctx.getArgument("hologram", String.class);
                            BlockState blockState = ctx.getArgument("block", BlockState.class);

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            Hologram hologram = hologramManager.hologramByName(hologramName);
                            if (hologram == null) {
                                LANG.sendMessage(sender, "niveriaholograms.hologram.edit.doesnt_exist",
                                        Lang.unparsedPlaceholder("niveriaholograms_hologram_name", hologramName)
                                );
                                return Command.SINGLE_SUCCESS;
                            }

                            if (!(hologram.configuration() instanceof BlockHologramConfiguration)) {
                                LANG.sendMessage(sender, "niveriaholograms.hologram.edit.only_block");
                                return Command.SINGLE_SUCCESS;
                            }

                            if (blockState.getType().isAir()) {
                                LANG.sendMessage(sender, "niveriaholograms.hologram.edit.block.no_air");
                                return Command.SINGLE_SUCCESS;
                            }

                            hologram.editConfig((BlockHologramConfiguration config) ->
                                    config.blockState(blockState)
                            );

                            LANG.sendMessage(sender, "niveriaholograms.hologram.edit.block.edited",
                                    Lang.unparsedPlaceholder("niveriaholograms_hologram_name", hologramName),
                                    Lang.unparsedPlaceholder("niveriaholograms_hologram_block_block", blockState.getType().translationKey())
                            );
                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }
}