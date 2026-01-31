package toutouchien.niveriaholograms.migration.migrators;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.world.entity.Display;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.utils.StringUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configurations.BlockHologramConfiguration;
import toutouchien.niveriaholograms.configurations.HologramConfiguration;
import toutouchien.niveriaholograms.configurations.ItemHologramConfiguration;
import toutouchien.niveriaholograms.configurations.TextHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.core.HologramType;
import toutouchien.niveriaholograms.migration.Migrator;
import toutouchien.niveriaholograms.utils.CustomLocation;
import toutouchien.niveriaholograms.utils.LegacyToMiniMessage;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class FancyHologramsV2Migrator implements Migrator {
    @Override
    public boolean canRun() {
        return new File("plugins/FancyHolograms/holograms.yml").isFile();
    }

    @NotNull
    @Override
    public ObjectList<Hologram> migrate(@NotNull Player player) {
        ObjectList<Hologram> holograms = new ObjectArrayList<>();
        try {
            File file = new File("plugins/FancyHolograms/holograms.yml");

            fileToHolograms(player, file, holograms);
        } catch (Exception e) {
            NiveriaHolograms.instance().getSLF4JLogger().error("The FancyHolograms V2 holograms couldn't be migrated", e);
        }

        return holograms;
    }

    private void fileToHolograms(@NotNull Player player, @NotNull File file, @NotNull ObjectList<Hologram> holograms) {
        FileConfiguration hologramsFile = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection hologramsSection = hologramsFile.getConfigurationSection("holograms");
        if (hologramsSection == null) {
            NiveriaHolograms.instance().getSLF4JLogger().warn("FancyHolograms file has no 'holograms' section");
            return;
        }

        for (String key : hologramsSection.getKeys(false)) {
            try {
                ConfigurationSection section = hologramsSection.getConfigurationSection(key);
                Hologram hologram = migrateHologram(key, player, section);
                if (hologram == null)
                    continue;

                holograms.add(hologram);
            } catch (Exception e) {
                Lang.sendMessage(player, "niveriaholograms.migrator.fancyholograms.cannot_migrate_hologram", key);
                NiveriaHolograms.instance().getSLF4JLogger().error("The hologram '{}' couldn't be migrated", key, e);
            }
        }
    }

    @Nullable
    private Hologram migrateHologram(@NotNull String name, @NotNull Player player, @NotNull ConfigurationSection section) {
        HologramType type = HologramType.valueOf(section.getString("type").toUpperCase(Locale.ROOT));
        CustomLocation location = parseLocation(name, player, section.getConfigurationSection("location"));
        if (location == null)
            return null;

        Vector3f scale = new Vector3f(
                section.getObject("scale_x", Number.class).floatValue(),
                section.getObject("scale_y", Number.class).floatValue(),
                section.getObject("scale_z", Number.class).floatValue()
        );
        Vector3f translation = new Vector3f(
                section.getObject("translation_x", Number.class).floatValue(),
                section.getObject("translation_y", Number.class).floatValue(),
                section.getObject("translation_z", Number.class).floatValue()
        );
        Display.BillboardConstraints billboard = Display.BillboardConstraints.valueOf(section.getString("billboard", "CENTER").toUpperCase(Locale.ROOT));
        float shadowRadius = section.getObject("shadow_radius", Number.class).floatValue();
        float shadowStrength = section.getObject("shadow_strength", Number.class).floatValue();
        int visibilityDistance = section.getObject("visibility_distance", Number.class).intValue();

        HologramConfiguration config = type.createConfiguration()
                .scale(scale)
                .translation(translation)
                .billboard(billboard)
                .shadowRadius(shadowRadius)
                .shadowStrength(shadowStrength)
                .visibilityDistance(visibilityDistance);

        switch (type) {
            case BLOCK -> {
                BlockHologramConfiguration blockConfig = (BlockHologramConfiguration) config;
                Material material = StringUtils.match(section.getString("block", "GRASS_BLOCK").toUpperCase(Locale.ROOT), Material.class, Material.GRASS_BLOCK);
                blockConfig.blockState(material.asBlockType().createBlockData().createBlockState());
            }

            case ITEM -> {
                ItemHologramConfiguration itemConfig = (ItemHologramConfiguration) config;
                itemConfig.itemStack(section.getItemStack("item", ItemStack.of(Material.APPLE)));
            }

            case TEXT -> {
                TextHologramConfiguration textConfig = (TextHologramConfiguration) config;
                textConfig.textAlignment(TextDisplay.TextAlignment.valueOf(section.getString("text_alignment", "CENTER").toUpperCase(Locale.ROOT)));
                textConfig.seeThrough(section.getBoolean("see_through", false));
                textConfig.textShadow(section.getBoolean("text_shadow", true));

                int updateInterval = section.getObject("update_text_interval", Number.class).intValue();
                textConfig.updateInterval(updateInterval);
                List<String> text = parseLines(section);
                if (text == null)
                    return null;

                textConfig.text(text);
            }
        }

        return new Hologram(type, config, name, player.getUniqueId(), location);
    }

    @Nullable
    private List<String> parseLines(@NotNull ConfigurationSection section) {
        List<String> loadedText = section.getStringList("text");
        if (loadedText.isEmpty())
            return null;

        return loadedText.stream()
                .map(LegacyToMiniMessage::convert)
                .toList();
    }

    @Nullable
    private CustomLocation parseLocation(@NotNull String name, @NotNull Player player, @Nullable ConfigurationSection section) {
        if (section == null) {
            Lang.sendMessage(player, "niveriaholograms.migrator.fancyholograms.malformed_location", name);
            return null;
        }

        String world = section.getString("world");

        Optional<String> loadedWorld = Bukkit.getWorlds().stream()
                .map(World::getName)
                .filter(worldName -> worldName.equals(world))
                .findAny();

        if (loadedWorld.isEmpty()) {
            Lang.sendMessage(player, "niveriaholograms.migrator.fancyholograms.invalid_world", name, world);
            return null;
        }

        Number xNum = section.getObject("x", Number.class);
        Number yNum = section.getObject("y", Number.class);
        Number zNum = section.getObject("z", Number.class);
        Number yawNum = section.getObject("yaw", Number.class);
        Number pitchNum = section.getObject("pitch", Number.class);
        if (xNum == null || yNum == null || zNum == null || yawNum == null || pitchNum == null) {
            Lang.sendMessage(player, "niveriaholograms.migrator.fancyholograms.malformed_location", name);
            return null;
        }

        double x = xNum.doubleValue();
        double y = yNum.doubleValue();
        double z = zNum.doubleValue();
        float yaw = yawNum.floatValue();
        float pitch = pitchNum.floatValue();

        return new CustomLocation(world, x, y, z, yaw, pitch);
    }
}
