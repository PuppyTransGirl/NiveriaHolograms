package toutouchien.niveriaholograms.persistence;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.TextDisplay;
import org.joml.Vector3f;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.*;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.core.HologramType;
import toutouchien.niveriaholograms.managers.HologramManager;
import toutouchien.niveriaholograms.utils.CustomLocation;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class HologramLoader {
    private final NiveriaHolograms plugin;

    public HologramLoader(NiveriaHolograms plugin) {
        this.plugin = plugin;
    }

    public Hologram load(ConfigurationSection section) {
        HologramType type = HologramType.valueOf(section.getString("type"));
        String name = section.getName();
        CustomLocation location = (CustomLocation) section.get("location");
        UUID owner = UUID.fromString(section.getString("owner", UUID.randomUUID().toString()));

        HologramConfiguration configuration = type == HologramType.BLOCK
                ? new BlockHologramConfiguration()
                : type == HologramType.ITEM
                ? new ItemHologramConfiguration()
                : type == HologramType.LEADERBOARD
                ? new LeaderboardHologramConfiguration()
                : new TextHologramConfiguration();

        HologramManager hologramManager = this.plugin.hologramManager();
        Hologram hologram = hologramManager.createHologram(type, configuration, name, owner, location);

        loadConfiguration(section, configuration);

        switch (type) {
            case BLOCK -> loadBlockConfiguration(section, (BlockHologramConfiguration) configuration);
            case ITEM -> loadItemConfiguration(section, (ItemHologramConfiguration) configuration);
            case LEADERBOARD -> loadLeaderboardConfiguration(section, (LeaderboardHologramConfiguration) configuration);
            case TEXT -> loadTextConfiguration(section, (TextHologramConfiguration) configuration);
        }

        return hologram;
    }

    private void loadConfiguration(ConfigurationSection section, HologramConfiguration configuration) {
        configuration.scale(loadVector(section, "scale."))
                .translation(loadVector(section, "translation."))
                .billboard(Display.BillboardConstraints.valueOf(section.getString("billboard")))
                .shadowRadius((float) section.getDouble("shadow-radius"))
                .shadowStrength((float) section.getDouble("shadow-strength"))
                .visibilityDistance(section.getInt("visibility-distance"));

        ConfigurationSection brightnessSection = section.getConfigurationSection("brightness");
        if (brightnessSection == null)
            return;

        configuration.brightness(new Brightness(brightnessSection.getInt("block"), brightnessSection.getInt("sky")));
    }

    private void loadBlockConfiguration(ConfigurationSection section, BlockHologramConfiguration configuration) {
        configuration.material(Material.valueOf(section.getString("material")));

        TextColor glowingColor = loadGlowing(section);
        if (glowingColor == null) {
            configuration.glowing(false);
            return;
        }

        configuration.glowing(true)
                .glowingColor(glowingColor);
    }

    private void loadItemConfiguration(ConfigurationSection section, ItemHologramConfiguration configuration) {
        configuration.itemStack(section.getItemStack("itemstack"));

        TextColor glowingColor = loadGlowing(section);
        if (glowingColor == null) {
            configuration.glowing(false);
            return;
        }

        configuration.glowing(true)
                .glowingColor(glowingColor);
    }

    private void loadLeaderboardConfiguration(ConfigurationSection section, LeaderboardHologramConfiguration configuration) {
        configuration.background(loadBackground(section))
                .seeThrough(section.getBoolean("see-through"))
                .textShadow(section.getBoolean("text-shadow"))
                .updateInterval(section.getInt("update-interval", 0))
                .placeholder(section.getString("placeholder", "%statistic_player_kills%"))
                .title(section.getString("title", "Kills Leaderboard"))
                .suffix(section.getString("suffix", "kills"))
                .showSuffix(section.getBoolean("show-suffix", true))
                .showEmptyPlaces(section.getBoolean("show-empty-places", true))
                .maxLines(section.getInt("max-lines", 10))
                .reverseOrder(section.getBoolean("reverse-order", false));

        TextColor background = loadBackground(section);
        if (background != null) {
            configuration.background(background);
        }

        TextColor mainColor = loadColor(section, "main-color");
        if (mainColor != null) {
            configuration.mainColor(mainColor);
        }

        TextColor firstColor = loadColor(section, "first-color");
        if (firstColor != null) {
            configuration.firstColor(firstColor);
        }

        TextColor secondColor = loadColor(section, "second-color");
        if (secondColor != null) {
            configuration.secondColor(secondColor);
        }

        TextColor thirdColor = loadColor(section, "third-color");
        if (thirdColor != null) {
            configuration.thirdColor(thirdColor);
        }

        TextColor otherColor = loadColor(section, "other-color");
        if (otherColor != null) {
            configuration.otherColor(otherColor);
        }

        TextColor valueColor = loadColor(section, "value-color");
        if (valueColor != null) {
            configuration.valueColor(valueColor);
        }

        TextColor suffixColor = loadColor(section, "suffix-color");
        if (suffixColor != null) {
            configuration.suffixColor(suffixColor);
        }
    }

    private void loadTextConfiguration(ConfigurationSection section, TextHologramConfiguration configuration) {
        configuration.background(loadBackground(section))
                .textAlignment(TextDisplay.TextAlignment.valueOf(section.getString("text-alignment")))
                .seeThrough(section.getBoolean("see-through"))
                .textShadow(section.getBoolean("text-shadow"))
                .updateInterval(section.getInt("update-interval", 0));

        List<String> text = section.getStringList("text");
        if (text.isEmpty())
            return;

        configuration.text(text);
    }

    private Vector3f loadVector(ConfigurationSection section, String path) {
        return new Vector3f(
                (float) section.getDouble(path + "x"),
                (float) section.getDouble(path + "y"),
                (float) section.getDouble(path + "z")
        );
    }

    private TextColor loadBackground(ConfigurationSection section) {
        String background = section.getString("text-background", "default");

        return switch (background.toLowerCase()) {
            case "default" -> null;
            case "transparent" -> Hologram.TRANSPARENT;
            default -> background.startsWith("#")
                    ? TextColor.fromHexString(background)
                    : NamedTextColor.NAMES.value(background);
        };
    }

    private TextColor loadColor(ConfigurationSection section, String key) {
        String background = section.getString(key, "default").toLowerCase(Locale.ROOT);

        if (background.equals("default")) {
            return null;
        }

        if (background.startsWith("#")) {
            return TextColor.fromHexString(background);
        }

        return NamedTextColor.NAMES.value(background);
    }

    private TextColor loadGlowing(ConfigurationSection section) {
        String background = section.getString("glowing", "none");

        return switch (background.toLowerCase()) {
            case "none" -> null;
            case "default" -> NamedTextColor.WHITE;
            default -> background.startsWith("#")
                    ? TextColor.fromHexString(background)
                    : NamedTextColor.NAMES.value(background);
        };
    }
}
