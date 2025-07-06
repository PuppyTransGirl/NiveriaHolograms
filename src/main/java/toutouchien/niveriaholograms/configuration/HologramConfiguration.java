package toutouchien.niveriaholograms.configuration;

import org.bukkit.entity.Display;
import org.joml.Vector3f;

public class HologramConfiguration {
	private Vector3f scale = new Vector3f(1, 1, 1);
	private Vector3f translation = new Vector3f(0, 0, 0);
	private Display.Billboard billboard = Display.Billboard.CENTER;
	private Display.Brightness brightness;
	private float shadowRadius = 0F;
	private float shadowStrength = 1F;
	private int visibilityDistance = -1;

	public Vector3f scale() {
		return scale;
	}

	public HologramConfiguration scale(Vector3f scale) {
		this.scale = scale;
		return this;
	}

	public Vector3f translation() {
		return translation;
	}

	public HologramConfiguration translation(Vector3f translation) {
		this.translation = translation;
		return this;
	}

	public Display.Billboard billboard() {
		return billboard;
	}

	public HologramConfiguration billboard(Display.Billboard billboard) {
		this.billboard = billboard;
		return this;
	}

	public Display.Brightness brightness() {
		return brightness;
	}

	public HologramConfiguration brightness(Display.Brightness brightness) {
		this.brightness = brightness;
		return this;
	}

	public float shadowRadius() {
		return shadowRadius;
	}

	public HologramConfiguration shadowRadius(float shadowRadius) {
		this.shadowRadius = shadowRadius;
		return this;
	}

	public float shadowStrength() {
		return shadowStrength;
	}

	public HologramConfiguration shadowStrength(float shadowStrength) {
		this.shadowStrength = shadowStrength;
		return this;
	}

	public int visibilityDistance() {
		return visibilityDistance;
	}

	public HologramConfiguration visibilityDistance(int visibilityDistance) {
		this.visibilityDistance = visibilityDistance;
		return this;
	}
}
