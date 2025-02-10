package toutouchien.niveriaholograms.hologram;

import org.bukkit.entity.Display;

import java.util.UUID;

public class Hologram<T extends Display> {
	private String name;
	private UUID owner;
	private long timestamp;
	private T display;
	private int updateInterval;

	public Hologram(String name, UUID owner, long timestamp, T display, int updateInterval) {
		this.owner = owner;
		this.timestamp = timestamp;
		this.name = name;
		this.display = display;
		this.updateInterval = updateInterval;
	}

	public String name() {
		return name;
	}

	public Hologram<T> name(String name) {
		this.name = name;
		return this;
	}

	public UUID owner() {
		return owner;
	}

	public Hologram<T> owner(UUID owner) {
		this.owner = owner;
		return this;
	}

	public long timestamp() {
		return timestamp;
	}

	public Hologram<T> timestamp(long timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	public Display display() {
		return display;
	}

	public Hologram<T> display(T display) {
		this.display = display;
		return this;
	}

	public int updateInterval() {
		return updateInterval;
	}

	public Hologram<T> updateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
		return this;
	}

	public enum Type {
		BLOCK,
		ITEM,
		TEXT;
	}
}
