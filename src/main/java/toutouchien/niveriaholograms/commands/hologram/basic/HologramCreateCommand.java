package toutouchien.niveriaholograms.commands.hologram.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaapi.utils.StringUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.core.HologramType;
import toutouchien.niveriaholograms.managers.HologramManager;

import java.util.Arrays;

public class HologramCreateCommand {
    private HologramCreateCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("create")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.create", true))
                .then(Commands.argument("type", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            Arrays.stream(HologramType.values())
                                    .map(Enum::name)
                                    .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                    .forEach(builder::suggest);

                            return builder.buildFuture();
                        })
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(ctx -> {
                                    Player player = (Player) ctx.getSource().getExecutor();
                                    String type = StringUtils.capitalize(ctx.getArgument("type", String.class));
                                    String name = ctx.getArgument("name", String.class);

                                    HologramType hologramType = StringUtils.match(type, HologramType.class, null);
                                    HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                                    boolean isValidHologram = isValidHologram(hologramType, type, player, name, hologramManager);
                                    if (!isValidHologram)
                                        return Command.SINGLE_SUCCESS;

                                    hologramManager.create(player, hologramType, name);
                                    Lang.sendMessage(player, "niveriaholograms.hologram.create.created", name, type);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                ).build();
    }

    private static boolean isValidHologram(@Nullable HologramType hologramType, @NotNull String type, @NotNull Player player, @NotNull String name, @NotNull HologramManager hologramManager) {
        if (hologramType == null) {
            Lang.sendMessage(player, "niveriaholograms.hologram.create.invalid_type", type);
            return false;
        }

        int length = name.length();
        if (length > 64) {
            Lang.sendMessage(player, "niveriaholograms.hologram.create.name_too_long", 64);
            return false;
        }

        if (name.contains(".") || name.contains("+")) {
            Lang.sendMessage(player, "niveriaholograms.hologram.create.invalid_character");
            return false;
        }

        if (hologramManager.hologramExists(name)) {
            Lang.sendMessage(player, "niveriaholograms.hologram.create.already_exists");
            return false;
        }

        return true;
    }
}
