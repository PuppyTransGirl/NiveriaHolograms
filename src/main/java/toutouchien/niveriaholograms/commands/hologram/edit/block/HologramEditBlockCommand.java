package toutouchien.niveriaholograms.commands.hologram.edit.block;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.block.BlockType;
import org.bukkit.command.CommandSender;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.BlockHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

public class HologramEditBlockCommand {
    private HologramEditBlockCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("block")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.block"))
                .then(Commands.argument("block", ArgumentTypes.resource(RegistryKey.BLOCK))
                        .executes(ctx -> {
                            CommandSender sender = CommandUtils.sender(ctx);
                            String hologramName = ctx.getArgument("hologram", String.class);
                            BlockType block = ctx.getArgument("block", BlockType.class);

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            Hologram hologram = hologramManager.hologramByName(hologramName);
                            if (hologram == null) {
                                Lang.sendMessage(sender, "niveriaholograms.hologram.edit.doesnt_exist", hologramName);
                                return Command.SINGLE_SUCCESS;
                            }

                            if (!(hologram.configuration() instanceof BlockHologramConfiguration)) {
                                Lang.sendMessage(sender, "niveriaholograms.hologram.edit.only_block");
                                return Command.SINGLE_SUCCESS;
                            }

                            if (block.isAir()) {
                                Lang.sendMessage(sender, "niveriaholograms.hologram.edit.block.no_air");
                                return Command.SINGLE_SUCCESS;
                            }

                            hologram.editConfig((BlockHologramConfiguration config) -> {
                                config.material(block.asMaterial()); // TODO: Change this deprecated method
                            });

                            Lang.sendMessage(sender, "niveriaholograms.hologram.edit.block.edited", hologramName, block.translationKey());
                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }
}