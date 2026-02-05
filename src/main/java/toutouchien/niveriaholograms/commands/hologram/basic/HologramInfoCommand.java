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
import toutouchien.niveriaholograms.utils.HologramUtils;

import java.util.Locale;

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

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

                            hologramManager.holograms().stream()
                                    .map(Hologram::name)
                                    .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                    .forEach(builder::suggest);

                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            CommandSender sender = CommandUtils.sender(ctx);
                            String hologramName = ctx.getArgument("hologram", String.class);

                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
                            Hologram hologram = hologramManager.hologramByName(hologramName);
                            if (hologram == null) {
                                LANG.sendMessage(sender, "niveriaholograms.hologram.info.doesnt_exist",
                                        Lang.unparsedPlaceholder("niveriaholograms_hologram_name", hologramName)
                                );
                                return Command.SINGLE_SUCCESS;
                            }

                            HologramConfiguration config = hologram.configuration();
                            HologramType type = hologram.type();
                            CustomLocation loc = hologram.location();

                            String[] brightnessText = brightnessText(config.brightness());
                            LANG.sendMessage(sender, "niveriaholograms.hologram.info.info",
                                    Lang.unparsedPlaceholder("niveriaholograms_hologram_name", hologram.name()),
                                    Lang.unparsedPlaceholder("niveriaholograms_hologram_type", StringUtils.capitalize(type.name())),
                                    Lang.numberPlaceholder("niveriaholograms_hologram_x", loc.x()),
                                    Lang.numberPlaceholder("niveriaholograms_hologram_y", loc.y()),
                                    Lang.numberPlaceholder("niveriaholograms_hologram_z", loc.z()),
                                    Lang.unparsedPlaceholder("niveriaholograms_hologram_world", loc.world()),
                                    Lang.numberPlaceholder("niveriaholograms_hologram_yaw", loc.yaw()),
                                    Lang.numberPlaceholder("niveriaholograms_hologram_pitch", loc.pitch()),
                                    Lang.unparsedPlaceholder("niveriaholograms_hologram_scale", scaleText(config.scale())),
                                    Lang.unparsedPlaceholder("niveriaholograms_hologram_billboard", StringUtils.capitalize(config.billboard().name())),
                                    Lang.unparsedPlaceholder("niveriaholograms_hologram_brightness_block", brightnessText[0]),
                                    Lang.unparsedPlaceholder("niveriaholograms_hologram_brightness_sky", brightnessText[1]),
                                    Lang.numberPlaceholder("niveriaholograms_hologram_shadow_radius", config.shadowRadius()),
                                    Lang.numberPlaceholder("niveriaholograms_hologram_shadow_strength", config.shadowStrength()),
                                    Lang.numberPlaceholder("niveriaholograms_hologram_visibility_distance", config.visibilityDistance())
                            );

                            String noGlowingColorText = LANG.getString("niveriaholograms.hologram.other.no_glowing_color");
                            switch (config) {
                                case BlockHologramConfiguration block ->
                                        LANG.sendMessage(sender, "niveriaholograms.hologram.info.info_block",
                                                Lang.unparsedPlaceholder("niveriaholograms_hologram_block_block", block.blockState().getType().name()),
                                                Lang.unparsedPlaceholder("niveriaholograms_hologram_block_glowing", String.valueOf(block.glowing())),
                                                Lang.unparsedPlaceholder("niveriaholograms_hologram_block_glowing_color", block.glowingColor() == null ? noGlowingColorText : block.glowingColor().asHexString())
                                        );

                                case ItemHologramConfiguration item ->
                                        LANG.sendMessage(sender, "niveriaholograms.hologram.info.info_item",
                                                Lang.unparsedPlaceholder("niveriaholograms_hologram_item_item", item.itemStack().getType().name()),
                                                Lang.unparsedPlaceholder("niveriaholograms_hologram_item_glowing", String.valueOf(item.glowing())),
                                                Lang.unparsedPlaceholder("niveriaholograms_hologram_item_glowing_color", item.glowingColor() == null ? noGlowingColorText : item.glowingColor().asHexString())
                                        );

                                case TextHologramConfiguration text ->
                                        LANG.sendMessage(sender, "niveriaholograms.hologram.info.info_text",
                                                Lang.unparsedPlaceholder("niveriaholograms_hologram_text_background", backgroundText(text.background())),
                                                Lang.unparsedPlaceholder("niveriaholograms_hologram_text_alignment", text.textAlignment().name()),
                                                Lang.unparsedPlaceholder("niveriaholograms_hologram_text_see_through", String.valueOf(text.seeThrough())),
                                                Lang.unparsedPlaceholder("niveriaholograms_hologram_text_shadow", String.valueOf(text.textShadow())),
                                                Lang.numberPlaceholder("niveriaholograms_hologram_text_update_interval", text.updateInterval())
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
        String defaultText = LANG.getString("niveriaholograms.hologram.info.other.default");

        if (brightness == null)
            return new String[]{defaultText, defaultText};

        String blockText = String.valueOf(brightness.block());
        String skyText = String.valueOf(brightness.sky());
        return new String[]{blockText, skyText};
    }

    @NotNull
    private static String backgroundText(@Nullable TextColor background) {
        if (background == null)
            return LANG.getString("niveriaholograms.hologram.info.other.default");

        if (background == HologramUtils.TRANSPARENT)
            return LANG.getString("niveriaholograms.hologram.info.other.transparent");

        return background.asHexString().toUpperCase(Locale.ROOT);
    }
}