package toutouchien.niveriaholograms.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("CustomLocation")
public class CustomLocation implements ConfigurationSerializable {
	private final String world;
	private double x, y, z;
	private float yaw, pitch;

	public CustomLocation(String world, double x, double y, double z, float yaw, float pitch) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public CustomLocation(Location location) {
		this(location.getWorld().getName(), location.x(), location.y(), location.z(), location.getYaw(), location.getPitch());
	}

	public CustomLocation(Map<String, Object> map) {
		this.world = (String) map.get("world");
		this.x = (double) map.get("x");
		this.y = (double) map.get("y");
		this.z = (double) map.get("z");
		this.yaw = ((Number) map.get("yaw")).floatValue();
		this.pitch = ((Number) map.get("pitch")).floatValue();
	}

	public String world() {
		return world;
	}

	public double x() {
		return x;
	}

	public CustomLocation x(double x) {
		this.x = x;
		return this;
	}

	public double y() {
		return y;
	}

	public CustomLocation y(double y) {
		this.y = y;
		return this;
	}

	public double z() {
		return z;
	}

	public CustomLocation z(double z) {
		this.z = z;
		return this;
	}

	public float yaw() {
		return yaw;
	}

	public CustomLocation yaw(float yaw) {
		this.yaw = yaw;
		return this;
	}

	public float pitch() {
		return pitch;
	}

	public CustomLocation pitch(float pitch) {
		this.pitch = pitch;
		return this;
	}

	public Location bukkitLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		Map<String, Object> data = new HashMap<>();

		data.put("world", world);

		data.put("x", this.x);
		data.put("y", this.y);
		data.put("z", this.z);

		data.put("yaw", this.yaw);
		data.put("pitch", this.pitch);

		return data;
	}

	public static CustomLocation deserialize(Map<String, Object> map) {
		return new CustomLocation((String) map.get("world"), (double) map.get("x"), (double) map.get("y"), (double) map.get("z"), ((Number) map.get("yaw")).floatValue(), ((Number) map.get("pitch")).floatValue());
	}
}
