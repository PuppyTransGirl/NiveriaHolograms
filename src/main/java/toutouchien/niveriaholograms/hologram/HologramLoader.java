package toutouchien.niveriaholograms.hologram;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.joml.Vector3f;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.hologram.configuration.BlockHologramConfiguration;
import toutouchien.niveriaholograms.hologram.configuration.HologramConfiguration;
import toutouchien.niveriaholograms.hologram.configuration.ItemHologramConfiguration;
import toutouchien.niveriaholograms.hologram.configuration.TextHologramConfiguration;
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
		UUID owner = UUID.fromString(section.getString("owner"));

		HologramConfiguration configuration = type == HologramType.BLOCK ? new BlockHologramConfiguration() : type == HologramType.ITEM ? new ItemHologramConfiguration() : new TextHologramConfiguration();

		HologramManager hologramManager = this.plugin.hologramManager();
		Hologram hologram = hologramManager.createHologram(type, configuration, name, location, owner);

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

		String[] textLines = text.toArray(new String[0]);
		TextComponent.Builder builder = Component.text();

		for (int i = 0; i < textLines.length; i++) {
			builder.append(MiniMessage.miniMessage().deserialize(textLines[i]));
			if (i + 1 == textLines.length)
				continue;

			builder.appendNewline();
		}

		configuration.text(builder.build());
	}

	public void save(ConfigurationSection section, Hologram hologram) {
		HologramType type = hologram.type();
		section.set("type", type.name());
		section.set("name", hologram.name());
		section.set("location", hologram.location());
		section.set("owner", hologram.owner().toString());

		HologramConfiguration configuration = hologram.configuration();

		saveVector(section, "scale.", configuration.scale());
		saveVector(section, "translation.", configuration.translation());

		section.set("billboard", configuration.billboard().name());
		section.set("shadow-radius", configuration.shadowRadius());
		section.set("shadow-strength", configuration.shadowStrength());
		section.set("visibility-distance", configuration.visibilityDistance());

		Display.Brightness brightness = configuration.brightness();
		if (brightness != null) {
			section.set("brightness.block", brightness.getBlockLight());
			section.set("brightness.sky", brightness.getSkyLight());
		}

		switch (type) {
			case BLOCK -> saveBlockConfiguration(section, (BlockHologramConfiguration) configuration);
			case ITEM -> saveItemConfiguration(section, (ItemHologramConfiguration) configuration);
			case TEXT -> saveTextConfiguration(section, (TextHologramConfiguration) configuration);
		}
	}

	private void saveBlockConfiguration(ConfigurationSection section, BlockHologramConfiguration configuration) {
		section.set("material", configuration.material().name());
	}

	private void saveItemConfiguration(ConfigurationSection section, ItemHologramConfiguration configuration) {
		section.set("itemstack", configuration.itemStack());
	}

	private void saveTextConfiguration(ConfigurationSection section, TextHologramConfiguration configuration) {
		section.set("text-background", backgroundColor(configuration.background()));
		section.set("text-alignment", configuration.textAlignment().name());
		section.set("see-through", configuration.seeThrough());
		section.set("text-shadow", configuration.textShadow());

		section.set("text", MiniMessage.miniMessage().serialize(configuration.text()).split("<br>"));
	}

	private Vector3f loadVector(ConfigurationSection section, String path) {
		return new Vector3f(
				(float) section.getDouble(path + "x"),
				(float) section.getDouble(path + "y"),
				(float) section.getDouble(path + "z")
		);
	}

	private void saveVector(ConfigurationSection section, String path, Vector3f vector) {
		section.set(path + "x", vector.x());
		section.set(path + "y", vector.y());
		section.set(path + "z", vector.z());
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

	private String backgroundColor(TextColor backgroundColor) {
		if (backgroundColor == null)
			return "default";
		else if (backgroundColor == Hologram.TRANSPARENT)
			return "transparent";
		else if (backgroundColor instanceof NamedTextColor namedTextColor)
			return namedTextColor.toString();
		else
			return backgroundColor.asHexString();
	}
}
