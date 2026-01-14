package toutouchien.niveriaholograms.configurations;

import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import org.joml.Vector3f;

public class HologramConfiguration {
	private Vector3f scale = new Vector3f(1, 1, 1);
	private Vector3f translation = new Vector3f(0, 0, 0);
	private Display.BillboardConstraints billboard = Display.BillboardConstraints.CENTER;
	private Brightness brightness;
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

	public Display.BillboardConstraints billboard() {
		return billboard;
	}

	public HologramConfiguration billboard(Display.BillboardConstraints billboard) {
		this.billboard = billboard;
		return this;
	}

	public Brightness brightness() {
		return brightness;
	}

	public HologramConfiguration brightness(Brightness brightness) {
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

	public HologramConfiguration copy() {
		HologramConfiguration copy = new HologramConfiguration();
		copy.scale = new Vector3f(this.scale);
		copy.translation = new Vector3f(this.translation);
		copy.billboard = this.billboard;
		if (this.brightness != null)
			copy.brightness = new Brightness(this.brightness.block(), this.brightness.sky());
		copy.shadowRadius = this.shadowRadius;
		copy.shadowStrength = this.shadowStrength;
		copy.visibilityDistance = this.visibilityDistance;
		return copy;
	}
}
