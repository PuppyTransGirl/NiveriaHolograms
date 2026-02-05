package toutouchien.niveriaholograms.commands.hologram.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

public class HologramCloneCommand {
    private HologramCloneCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("clone")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.clone", true))
                .then(Commands.argument("hologram", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();

                            hologramManager.holograms().stream()
                                    .map(Hologram::name)
                                    .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                    .forEach(builder::suggest);

                            return builder.buildFuture();
                        })
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(ctx -> {
                                    Player player = (Player) ctx.getSource().getExecutor();
                                    String hologramName = ctx.getArgument("hologram", String.class);
                                    String newHologramName = ctx.getArgument("name", String.class);

                                    HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                                    Hologram hologram = hologramManager.hologramByName(hologramName);

                                    boolean isValidHologram = isValidHologram(hologram, player, hologramName, newHologramName, hologramManager);
                                    if (!isValidHologram)
                                        return Command.SINGLE_SUCCESS;

                                    hologramManager.cloneHologram(hologram, player, newHologramName);
                                    LANG.sendMessage(player, "niveriaholograms.hologram.clone.cloned",
                                            Lang.unparsedPlaceholder("niveriaholograms_cloned_hologram_name", hologram.name()),
                                            Lang.unparsedPlaceholder("niveriaholograms_hologram_name", newHologramName)
                                    );
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .build();
    }

    private static boolean isValidHologram(@Nullable Hologram hologram, @NotNull Player player, @NotNull String hologramName, @NotNull String newHologramName, @NotNull HologramManager hologramManager) {
        if (hologram == null) {
            LANG.sendMessage(player, "niveriaholograms.hologram.clone.doesnt_exist",
                    Lang.unparsedPlaceholder("niveriaholograms_hologram_name", hologramName)
            );
            return false;
        }

        int length = newHologramName.length();
        if (length > 64) {
            LANG.sendMessage(player, "niveriaholograms.hologram.clone.name_too_long",
                    Lang.numberPlaceholder("niveriaholograms_hologram_name_max_length", 64)
            );
            return false;
        }

        if (newHologramName.contains(".") || newHologramName.contains("+")) {
            LANG.sendMessage(player, "niveriaholograms.hologram.clone.invalid_character");
            return false;
        }

        if (hologramManager.hologramExists(newHologramName)) {
            LANG.sendMessage(player, "niveriaholograms.hologram.clone.already_exists");
            return false;
        }

        return true;
    }
}