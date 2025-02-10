package toutouchien.niveriaholograms.hologram;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import toutouchien.niveriaapi.utils.Task;
import toutouchien.niveriaapi.utils.TimeUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class HologramManager {
	private final NiveriaHolograms plugin;
	private final List<Hologram<?>> holograms;

	public HologramManager(NiveriaHolograms plugin) {
		this.plugin = plugin;

		this.holograms = new ArrayList<>();
	}

	public void initialize() {
		Task.taskTimerAsync(() -> {
			saveHolograms();
			plugin.getLogger().info("Periodic save - Holograms");
		}, plugin, TimeUtils.secondsToTicks(30), TimeUtils.secondsToTicks(30));
	}

	public void createHologram(Hologram<?> hologram) {
		this.holograms.add(hologram);
	}

	public Hologram<?> hologramByName(String name) {
		return this.holograms.stream().filter(hologram -> hologram.name().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public List<Hologram<?>> holograms() {
		return this.holograms;
	}

	public void loadHolograms() {
		File file = new File(plugin.getDataFolder(), "holograms.yml");
		if (!file.exists())
			return;

		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if (!config.contains("holograms"))
			return;

		ConfigurationSection hologramsSection = config.getConfigurationSection("holograms");
		for (String key : hologramsSection.getKeys(false)) {
			ConfigurationSection section = hologramsSection.getConfigurationSection(key);
			if (section == null)
				continue;

			String type = section.getString("type");
			UUID owner = UUID.fromString(section.getString("owner"));
			long timestamp = section.getLong("timestamp");
			Location location = section.getLocation("location");

			Vector3f scale = new Vector3f(
					(float) section.getDouble("scale_x"),
					(float) section.getDouble("scale_y"),
					(float) section.getDouble("scale_z")
			);

			Vector3f translation = new Vector3f(
					(float) section.getDouble("translation_x"),
					(float) section.getDouble("translation_y"),
					(float) section.getDouble("translation_z")
			);

			float shadowRadius = (float) section.getDouble("shadow_radius");
			float shadowStrength = (float) section.getDouble("shadow_strength");
			Display.Billboard billboard = Display.Billboard.valueOf(section.getString("billboard"));

			Display display = null;

			switch (type) {
				case "BLOCK" -> {
					BlockDisplay blockDisplay = location.getWorld().spawn(location, BlockDisplay.class);
					blockDisplay.setBlock(Material.valueOf(section.getString("block")).createBlockData());
					display = blockDisplay;
				}
				case "ITEM" -> {
					ItemDisplay itemDisplay = location.getWorld().spawn(location, ItemDisplay.class);
					itemDisplay.setItemStack(section.getItemStack("item"));
					display = itemDisplay;
				}
				case "TEXT" -> {
					TextDisplay textDisplay = location.getWorld().spawn(location, TextDisplay.class);
					String[] textLines = section.getStringList("text").toArray(new String[0]);
					TextComponent.Builder builder = Component.text();

					for (int i = 0; i < textLines.length; i++) {
						builder.append(MiniMessage.miniMessage().deserialize(textLines[i]));
						if (i + 1 == textLines.length)
							continue;

						builder.appendNewline();
					}

					textDisplay.text();
					textDisplay.setShadowed(section.getBoolean("text_shadow"));
					textDisplay.setSeeThrough(section.getBoolean("see_through"));
					textDisplay.setAlignment(TextDisplay.TextAlignment.valueOf(section.getString("text_alignment")));
					display = textDisplay;
				}
			}

			if (display == null)
				continue;

			Transformation transformation = new Transformation(translation, new AxisAngle4f(), scale, new AxisAngle4f());
			display.setTransformation(transformation);
			display.setShadowRadius(shadowRadius);
			display.setShadowStrength(shadowStrength);
			display.setBillboard(billboard);

			this.holograms.add(new Hologram<>(key, owner, timestamp, display, section.getInt("update_text_interval", 20)));
		}
	}

	public void saveHolograms() {
		File file = new File(plugin.getDataFolder(), "holograms.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		ConfigurationSection hologramsSection = config.contains("holograms") ? config.getConfigurationSection("holograms") : config.createSection("holograms");

		Set<String> hologramsKeys = hologramsSection.getKeys(false);
		if (!hologramsKeys.isEmpty()) {
			Set<String> hologramsNames = holograms.stream()
					.map(hologram -> hologram.name().toLowerCase(Locale.ROOT))
					.collect(Collectors.toSet());

			for (String key : hologramsKeys) {
				if (hologramsNames.contains(key))
					continue;

				config.set("holograms." + key, null);
			}
		}

		for (Hologram<?> hologram : holograms) {
			Map<String, Object> hologramData = new LinkedHashMap<>();
			Display display = hologram.display();
			hologramData.put("type", display.getType().name().replace("_DISPLAY", ""));
			hologramData.put("owner", hologram.owner().toString());
			hologramData.put("timestamp", hologram.timestamp());
			hologramData.put("location", display.getLocation());

			Transformation transformation = display.getTransformation();
			Vector3f scale = transformation.getScale();
			Vector3f translation = transformation.getTranslation();

			hologramData.put("scale_x", scale.x);
			hologramData.put("scale_y", scale.y);
			hologramData.put("scale_z", scale.z);
			hologramData.put("translation_x", translation.x);
			hologramData.put("translation_y", translation.y);
			hologramData.put("translation_z", translation.z);

			hologramData.put("shadow_radius", display.getShadowRadius());
			hologramData.put("shadow_strength", display.getShadowStrength());
			hologramData.put("billboard", display.getBillboard().name());

			switch (display.getType()) {
				case BLOCK_DISPLAY -> {
					BlockDisplay blockDisplay = (BlockDisplay) display;
					hologramData.put("block", blockDisplay.getBlock().createBlockState().getType().name());
				}

				case ITEM_DISPLAY -> {
					ItemDisplay itemDisplay = (ItemDisplay) display;
					hologramData.put("item", itemDisplay.getItemStack());
				}

				case TEXT_DISPLAY -> {
					TextDisplay textDisplay = (TextDisplay) display;
					hologramData.put("text", MiniMessage.miniMessage().serialize(textDisplay.text()).split("<br>"));
					hologramData.put("text_shadow", textDisplay.isShadowed());
					hologramData.put("see_through", textDisplay.isSeeThrough());
					hologramData.put("text_alignment", textDisplay.getAlignment().name());
					hologramData.put("update_text_interval", hologram.updateInterval());
				}
			}

			hologramsSection.set(hologram.name(), hologramData);
		}

		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		saveHolograms();
	}
}
