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
import toutouchien.niveriaholograms.utils.HologramUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

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
                            int radius = ctx.getArgument("radius", int.class);

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            List<Map.Entry<Hologram, Double>> nearbyHolograms = hologramManager.holograms().stream()
                                    .filter(hologram -> hologram.location().world().equals(player.getWorld().getName()))
                                    .map(hologram -> Map.entry(hologram, hologram.location().distance(player.getLocation())))
                                    .filter(distance -> distance.getValue() <= radius)
                                    .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                                    .toList();


                            if (nearbyHolograms.isEmpty()) {
                                LANG.sendMessage(player, "niveriaholograms.hologram.nearby.no_holograms",
                                        Lang.numberPlaceholder("niveriaholograms_nearby_radius", radius)
                                );
                                return Command.SINGLE_SUCCESS;
                            }

                            LANG.sendMessage(player, "niveriaholograms.hologram.nearby.header",
                                    Lang.numberPlaceholder("niveriaholograms_hologram_amount", nearbyHolograms.size())
                            );

                            for (Map.Entry<Hologram, Double> entry : nearbyHolograms) {
                                Hologram hologram = entry.getKey();
                                double distance = entry.getValue();

                                CustomLocation loc = hologram.location();

                                LANG.sendMessage(player, "niveriaholograms.hologram.nearby.hologram_entry",
                                        Lang.unparsedPlaceholder("niveriaholograms_hologram_name", hologram.name()),
                                        Lang.unparsedPlaceholder("niveriaholograms_hologram_type", StringUtils.capitalize(hologram.type().name())),
                                        Lang.unparsedPlaceholder("niveriaholograms_hologram_world", loc.world()),
                                        Lang.numberPlaceholder("niveriaholograms_hologram_x", HologramUtils.formatNumber(loc.x())),
                                        Lang.numberPlaceholder("niveriaholograms_hologram_y", HologramUtils.formatNumber(loc.y())),
                                        Lang.numberPlaceholder("niveriaholograms_hologram_z", HologramUtils.formatNumber(loc.z())),
                                        Lang.numberPlaceholder("niveriaholograms_hologram_distance", HologramUtils.formatNumber(distance))
                                );
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }
}