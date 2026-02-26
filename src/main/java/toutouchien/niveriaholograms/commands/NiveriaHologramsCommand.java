package toutouchien.niveriaholograms.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaapi.utils.MathUtils;
import toutouchien.niveriaapi.utils.Task;
import toutouchien.niveriaholograms.NiveriaHolograms;

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

public final class NiveriaHologramsCommand {
    private NiveriaHologramsCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("niveriaholograms")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.niveriaholograms"))
                .then(reloadCommand())
                .build();
    }

    private static LiteralArgumentBuilder<CommandSourceStack> reloadCommand() {
        return Commands.literal("reload")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.niveriaholograms.reload"))
                .executes(ctx -> {
                    CommandSender sender = CommandUtils.sender(ctx);

                    LANG.sendMessage(sender, "command.reload.start");
                    long startNanos = System.nanoTime();

                    Task.async(task -> {
                        try {
                            NiveriaHolograms.instance().reload();
                            double timeTaken = (System.nanoTime() - startNanos) / 1_000_000D;

                            LANG.sendMessage(sender, "command.reload.done",
                                    Lang.numberPlaceholder("time_ms", MathUtils.decimalRound(timeTaken, 2))
                            );
                        } catch (Exception e) {
                            NiveriaHolograms.instance().getSLF4JLogger().error("Failed to reload NiveriaHolograms", e);
                            LANG.sendMessage(sender, "command.reload.error");
                        }
                    }, NiveriaHolograms.instance());

                    return Command.SINGLE_SUCCESS;
                });
    }
}
