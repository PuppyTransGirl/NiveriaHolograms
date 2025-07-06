package toutouchien.niveriaholograms.hologram.configuration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.TextDisplay;

public class TextHologramConfiguration extends HologramConfiguration {
	private Component text;
	private TextColor background;
	private TextDisplay.TextAlignment textAlignment = TextDisplay.TextAlignment.CENTER;
	private boolean seeThrough = false;
	private boolean textShadow = false;

	public Component text() {
		return text;
	}

	public TextHologramConfiguration text(Component text) {
		this.text = text;
		return this;
	}

	public TextColor background() {
		return background;
	}

	public TextHologramConfiguration background(TextColor background) {
		this.background = background;
		return this;
	}

	public TextDisplay.TextAlignment textAlignment() {
		return textAlignment;
	}

	public TextHologramConfiguration textAlignment(TextDisplay.TextAlignment textAlignment) {
		this.textAlignment = textAlignment;
		return this;
	}

	public boolean seeThrough() {
		return seeThrough;
	}

	public TextHologramConfiguration seeThrough(boolean seeThrough) {
		this.seeThrough = seeThrough;
		return this;
	}

	public boolean textShadow() {
		return textShadow;
	}

	public TextHologramConfiguration textShadow(boolean textShadow) {
		this.textShadow = textShadow;
		return this;
	}
}
