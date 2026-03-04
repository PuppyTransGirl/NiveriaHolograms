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
import toutouchien.niveriaholograms.utils.HologramUtils;

import java.util.List;

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

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
                        LANG.sendMessage(sender, "command.hologram.list.no_holograms");
                        return Command.SINGLE_SUCCESS;
                    }

                    LANG.sendMessage(sender, "command.hologram.list.header",
                            Lang.numberPlaceholder("hologram_amount", holograms.size())
                    );

                    for (Hologram hologram : holograms) {
                        CustomLocation loc = hologram.location();

                        LANG.sendMessage(sender, "command.hologram.list.hologram_entry",
                                Lang.unparsedPlaceholder("hologram_name", hologram.name()),
                                Lang.unparsedPlaceholder("hologram_type", StringUtils.capitalize(hologram.type().name())),
                                Lang.unparsedPlaceholder("hologram_world", loc.world()),
                                Lang.numberPlaceholder("hologram_x", HologramUtils.formatNumber(loc.x())),
                                Lang.numberPlaceholder("hologram_y", HologramUtils.formatNumber(loc.y())),
                                Lang.numberPlaceholder("hologram_z", HologramUtils.formatNumber(loc.z()))
                        );
                    }

                    return Command.SINGLE_SUCCESS;
                }).build();
    }
}