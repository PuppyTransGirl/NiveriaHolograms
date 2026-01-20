package toutouchien.niveriaholograms.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("CustomLocation")
public class CustomLocation implements ConfigurationSerializable {
    private String world;
    private double x, y, z;
    private float yaw, pitch;

    public CustomLocation(@NotNull String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public CustomLocation(@NotNull Location location) {
        this(location.getWorld().getName(), location.x(), location.y(), location.z(), location.getYaw(), location.getPitch());
    }

    public CustomLocation(@NotNull Map<String, Object> map) {
        this.world = (String) map.get("world");
        this.x = (double) map.get("x");
        this.y = (double) map.get("y");
        this.z = (double) map.get("z");
        this.yaw = ((Number) map.get("yaw")).floatValue();
        this.pitch = ((Number) map.get("pitch")).floatValue();
    }

    @NotNull
    public String world() {
        return world;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public float yaw() {
        return yaw;
    }

    public float pitch() {
        return pitch;
    }

    @NotNull
    @Contract("_ -> this")
    public CustomLocation world(@NotNull String world) {
        this.world = world;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public CustomLocation x(double x) {
        this.x = x;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public CustomLocation y(double y) {
        this.y = y;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public CustomLocation z(double z) {
        this.z = z;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public CustomLocation yaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public CustomLocation pitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public double distance(@NotNull CustomLocation other) {
        return Math.sqrt(distanceSquared(other));
    }

    public double distance(@NotNull Location other) {
        return Math.sqrt(distanceSquared(other));
    }

    public double distanceSquared(@NotNull CustomLocation other) {
        if (!this.world.equals(other.world))
            throw new IllegalArgumentException("Cannot measure distance between " + this.world + " and " + other.world);

        return NumberConversions.square(x - other.x) + NumberConversions.square(y - other.y) + NumberConversions.square(z - other.z);
    }

    public double distanceSquared(@NotNull Location other) {
        if (!this.world.equals(other.getWorld().getName()))
            throw new IllegalArgumentException("Cannot measure distance between " + this.world + " and " + other.getWorld().getName());

        return NumberConversions.square(x - other.getX()) + NumberConversions.square(y - other.getY()) + NumberConversions.square(z - other.getZ());
    }

    @NotNull
    public Location bukkitLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("world", world);

        data.put("x", this.x);
        data.put("y", this.y);
        data.put("z", this.z);

        data.put("yaw", this.yaw);
        data.put("pitch", this.pitch);

        return data;
    }

    @NotNull
    public static CustomLocation deserialize(@NotNull Map<String, Object> map) {
        return new CustomLocation(
                (String) map.get("world"),
                (double) map.get("x"),
                (double) map.get("y"),
                (double) map.get("z"),
                ((Number) map.get("yaw")).floatValue(),
                ((Number) map.get("pitch")).floatValue()
        );
    }

    public CustomLocation copy() {
        return new CustomLocation(
                this.world,
                this.x, this.y, this.z,
                this.yaw, this.pitch
        );
    }
}
