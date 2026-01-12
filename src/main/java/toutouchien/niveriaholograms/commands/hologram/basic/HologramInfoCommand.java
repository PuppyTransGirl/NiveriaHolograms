package toutouchien.niveriaholograms.commands.hologram.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.util.Brightness;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaapi.utils.StringUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.BlockHologramConfiguration;
import toutouchien.niveriaholograms.configurations.HologramConfiguration;
import toutouchien.niveriaholograms.configurations.ItemHologramConfiguration;
import toutouchien.niveriaholograms.configurations.TextHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.core.HologramType;
import toutouchien.niveriaholograms.managers.HologramManager;
import toutouchien.niveriaholograms.utils.CustomLocation;

import java.util.Locale;

public class HologramInfoCommand {
    private HologramInfoCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("info")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.info"))
                .then(Commands.argument("hologram", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();

                            for (Hologram hologram : hologramManager.holograms())
                                builder.suggest(hologram.name());

                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            CommandSender sender = CommandUtils.sender(ctx);
                            String hologramName = ctx.getArgument("hologram", String.class);

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            Hologram hologram = hologramManager.hologramByName(hologramName);
                            if (hologram == null) {
                                Lang.sendMessage(sender, "niveriaholograms.hologram.info.doesnt_exist", hologramName);
                                return Command.SINGLE_SUCCESS;
                            }

                            HologramConfiguration config = hologram.configuration();
                            HologramType type = hologram.type();
                            CustomLocation loc = hologram.location();

                            Lang.sendMessage(sender, "niveriaholograms.hologram.info.info",
                                    hologram.name(),
                                    StringUtils.capitalize(type.name()),
                                    loc.x(), loc.y(), loc.z(),
                                    hologram.location().world(),
                                    loc.yaw(), loc.pitch(),
                                    scaleText(config.scale()),
                                    StringUtils.capitalize(config.billboard().name()),
                                    brightnessText(config.brightness()),
                                    config.shadowRadius(),
                                    config.shadowStrength(),
                                    config.visibilityDistance()
                            );

                            String noGlowingColorText = Lang.getString("niveriaholograms.hologram.other.no_glowing_color");
                            switch (config) {
                                case BlockHologramConfiguration block ->
                                        Lang.sendMessage(sender, "niveriaholograms.hologram.info.info_block",
                                                block.material().name(),
                                                block.glowing(),
                                                block.glowingColor() == null ? noGlowingColorText : block.glowingColor().asHexString()
                                        );

                                case ItemHologramConfiguration item ->
                                        Lang.sendMessage(sender, "niveriaholograms.hologram.info.info_item",
                                                item.itemStack().getType(),
                                                item.glowing(),
                                                item.glowingColor() == null ? noGlowingColorText : item.glowingColor().asHexString()
                                        );

                                case TextHologramConfiguration text ->
                                        Lang.sendMessage(sender, "niveriaholograms.hologram.info.info_text",
                                                backgroundText(text.background()),
                                                text.textAlignment().name(),
                                                text.seeThrough(),
                                                text.shadowRadius(),
                                                text.updateInterval()
                                        );

                                default -> throw new IllegalStateException("Unexpected value: " + config);
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }

    @NotNull
    private static String scaleText(@NotNull Vector3f scale) {
        boolean allEquals = scale.x() == scale.y() && scale.y() == scale.z();
        return allEquals ? Float.toString(scale.x()) : scale.x() + " " + scale.y() + " " + scale.z();
    }

    @NotNull
    private static String[] brightnessText(@Nullable Brightness brightness) {
        String defaultText = Lang.getString("niveriaholograms.hologram.info.other.default");

        if (brightness == null)
            return new String[]{defaultText, defaultText};

        String blockText = String.valueOf(brightness.block());
        String skyText = String.valueOf(brightness.sky());
        return new String[]{blockText, skyText};
    }

    @NotNull
    private static String backgroundText(@Nullable TextColor background) {
        if (background == null)
            return Lang.getString("niveriaholograms.hologram.info.other.default");

        if (background == Hologram.TRANSPARENT)
            return Lang.getString("niveriaholograms.hologram.info.other.transparent");

        return background.asHexString().toUpperCase(Locale.ROOT);
    }
}