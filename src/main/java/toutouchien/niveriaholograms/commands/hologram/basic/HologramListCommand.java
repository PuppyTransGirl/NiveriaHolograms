package toutouchien.niveriaholograms.commands.hologram.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaapi.utils.StringUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;
import toutouchien.niveriaholograms.utils.CustomLocation;

import java.util.List;

public class HologramListCommand {
    private HologramListCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("list")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.list"))
                .executes(ctx -> {
                    CommandSender sender = CommandUtils.sender(ctx);
                    HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();

                    List<Hologram> holograms = hologramManager.holograms();
                    if (holograms.isEmpty()) {
                        Lang.sendMessage(sender, "niveriaholograms.hologram.list.no_holograms");
                        return Command.SINGLE_SUCCESS;
                    }

                    Lang.sendMessage(sender, "niveriaholograms.hologram.list.header", holograms.size());

                    for (Hologram hologram : holograms) {
                        CustomLocation loc = hologram.location();

                        Lang.sendMessage(sender, "niveriaholograms.hologram.list.hologram_entry",
                                hologram.name(),
                                StringUtils.capitalize(hologram.type().name()),
                                loc.x(), loc.y(), loc.z(),
                                loc.world()
                        );
                    }

                    return Command.SINGLE_SUCCESS;
                }).build();
    }
}