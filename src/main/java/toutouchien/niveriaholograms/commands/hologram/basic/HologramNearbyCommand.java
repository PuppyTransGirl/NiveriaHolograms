package toutouchien.niveriaholograms.commands.hologram.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaapi.utils.StringUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;
import toutouchien.niveriaholograms.utils.CustomLocation;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class HologramNearbyCommand {
    private HologramNearbyCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("nearby")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.nearby", true))
                .then(Commands.argument("radius", IntegerArgumentType.integer(0))
                        .executes(ctx -> {
                            Player player = (Player) ctx.getSource().getExecutor();
                            int radius = ctx.getArgument("radius", Integer.class);

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            List<Map.Entry<Hologram, Double>> nearbyHolograms = hologramManager.holograms().stream()
                                    .filter(hologram -> hologram.location().world().equals(player.getWorld().getName()))
                                    .map(hologram -> Map.entry(hologram, hologram.location().distance(player.getLocation())))
                                    .filter(distance -> distance.getValue() <= radius)
                                    .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                                    .toList();


                            if (nearbyHolograms.isEmpty()) {
                                Lang.sendMessage(player, "niveriaholograms.hologram.nearby.no_holograms");
                                return Command.SINGLE_SUCCESS;
                            }

                            Lang.sendMessage(player, "niveriaholograms.hologram.nearby.header", nearbyHolograms.size());

                            for (Map.Entry<Hologram, Double> entry : nearbyHolograms) {
                                Hologram hologram = entry.getKey();
                                double distance = entry.getValue();

                                CustomLocation loc = hologram.location();

                                Lang.sendMessage(player, "niveriaholograms.hologram.nearby.hologram_entry",
                                        hologram.name(),
                                        StringUtils.capitalize(hologram.type().name()),
                                        loc.x(), loc.y(), loc.z(),
                                        loc.world(),
                                        distance
                                );
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }
}