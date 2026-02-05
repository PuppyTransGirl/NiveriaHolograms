package toutouchien.niveriaholograms.commands.niveriaholograms;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.migration.MigrationMenu;

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

public class NiveriaHologramsCommand {
    private NiveriaHologramsCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("niveriaholograms")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.niveriaholograms"))
                .then(migrateCommand())
                .then(reloadCommand())
                .build();
    }

    private static LiteralArgumentBuilder<CommandSourceStack> migrateCommand() {
        return Commands.literal("migrate")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.niveriaholograms.migrate", true))
                .executes(ctx -> {
                    Player player = (Player) ctx.getSource().getExecutor();
                    new MigrationMenu(player).open();

                    return Command.SINGLE_SUCCESS;
                });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> reloadCommand() {
        return Commands.literal("reload")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.niveriaholograms.reload"))
                .executes(ctx -> {
                    CommandSender sender = CommandUtils.sender(ctx);

                    long startMillis = System.currentTimeMillis();
                    NiveriaHolograms.instance().reload();
                    long timeTaken = System.currentTimeMillis() - startMillis;
                    LANG.sendMessage(sender, "niveriaholograms.reload.done",
                            Lang.numberPlaceholder("niveriaholograms_time_ms", timeTaken)
                    );

                    return Command.SINGLE_SUCCESS;
                });
    }
}