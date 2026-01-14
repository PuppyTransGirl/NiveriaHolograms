package toutouchien.niveriaholograms.commands.hologram.edit.general;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.minecraft.world.entity.Display;
import org.bukkit.command.CommandSender;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaapi.utils.StringUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

import java.util.Arrays;

public class HologramEditBillboardCommand {
    private HologramEditBillboardCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("billboard")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit.billboard"))
                .then(Commands.argument("billboard", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            String hologramName = ctx.getArgument("hologram", String.class);

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            Hologram hologram = hologramManager.hologramByName(hologramName);
                            Display.BillboardConstraints currentBillboard = hologram == null ? null : hologram.configuration().billboard();

                            Arrays.stream(Display.BillboardConstraints.values())
                                    .filter(billboard -> billboard != currentBillboard)
                                    .map(Enum::name)
                                    .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                    .forEach(builder::suggest);

                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            CommandSender sender = CommandUtils.sender(ctx);
                            String hologramName = ctx.getArgument("hologram", String.class);
                            String billboardName = ctx.getArgument("billboard", String.class);

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            Hologram hologram = hologramManager.hologramByName(hologramName);
                            if (hologram == null) {
                                Lang.sendMessage(sender, "niveriaholograms.hologram.edit.doesnt_exist", hologramName);
                                return Command.SINGLE_SUCCESS;
                            }

                            Display.BillboardConstraints billboard = StringUtils.match(billboardName, Display.BillboardConstraints.class, null);
                            if (billboard == null) {
                                Lang.sendMessage(sender, "niveriaholograms.hologram.edit.billboard.invalid_billboard", billboardName);
                                return Command.SINGLE_SUCCESS;
                            }

                            hologram.editConfig(config ->
                                    config.billboard(billboard)
                            );

                            Lang.sendMessage(sender, "niveriaholograms.hologram.edit.billboard.edited", hologramName);
                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }
}