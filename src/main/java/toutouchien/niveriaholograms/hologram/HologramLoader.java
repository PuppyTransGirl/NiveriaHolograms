package toutouchien.niveriaholograms.hologram;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.joml.Vector3f;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.configuration.BlockHologramConfiguration;
import toutouchien.niveriaholograms.configuration.HologramConfiguration;
import toutouchien.niveriaholograms.configuration.ItemHologramConfiguration;
import toutouchien.niveriaholograms.configuration.TextHologramConfiguration;
import toutouchien.niveriaholograms.utils.CustomLocation;

import java.util.List;
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

		HologramConfiguration configuration = type == HologramType.BLOCK ? new BlockHologramConfiguration() : type == HologramType.ITEM ? new ItemHologramConfiguration() : new TextHologramConfiguration();

		HologramManager hologramManager = this.plugin.hologramManager();
		Hologram hologram = hologramManager.createHologram(type, configuration, name, owner, location);

		loadConfiguration(section, configuration);

		switch (type) {
			case BLOCK -> loadBlockConfiguration(section, (BlockHologramConfiguration) configuration);
			case ITEM -> loadItemConfiguration(section, (ItemHologramConfiguration) configuration);
			case TEXT -> loadTextConfiguration(section, (TextHologramConfiguration) configuration);
		}

		return hologram;
	}

	private void loadConfiguration(ConfigurationSection section, HologramConfiguration configuration) {
		configuration.scale(loadVector(section, "scale."))
				.translation(loadVector(section, "translation."))
				.billboard(Display.Billboard.valueOf(section.getString("billboard")))
				.shadowRadius((float) section.getDouble("shadow-radius"))
				.shadowStrength((float) section.getDouble("shadow-strength"))
				.visibilityDistance(section.getInt("visibility-distance"));

		ConfigurationSection brightnessSection = section.getConfigurationSection("brightness");
		if (brightnessSection == null)
			return;

		configuration.brightness(new Display.Brightness(brightnessSection.getInt("block"), brightnessSection.getInt("sky")));
	}

	private void loadBlockConfiguration(ConfigurationSection section, BlockHologramConfiguration configuration) {
		configuration.material(Material.valueOf(section.getString("material")));
	}

	private void loadItemConfiguration(ConfigurationSection section, ItemHologramConfiguration configuration) {
		configuration.itemStack(section.getItemStack("itemstack"));
	}

	private void loadTextConfiguration(ConfigurationSection section, TextHologramConfiguration configuration) {
		configuration.background(loadBackground(section));
		configuration.textAlignment(TextDisplay.TextAlignment.valueOf(section.getString("text-alignment")));
		configuration.seeThrough(section.getBoolean("see-through"));
		configuration.textShadow(section.getBoolean("text-shadow"));

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
		String background = section.getString("text-background", null);
		if (background == null || background.equalsIgnoreCase("default"))
			return null;
		else if (background.equalsIgnoreCase("transparent"))
			return Hologram.TRANSPARENT;
		else if (background.startsWith("#"))
			return TextColor.fromHexString(background);
		else
			return NamedTextColor.NAMES.value(background);
	}
}
