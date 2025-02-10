package toutouchien.niveriaholograms.command.niveriaholograms;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.hologram.Hologram;
import toutouchien.niveriaholograms.hologram.HologramManager;

public class NiveriaHologramsTest extends SubCommand {
	NiveriaHologramsTest(Plugin plugin) {
		super(new CommandData("test", plugin));
	}

	@Override
	public void execute(CommandSender sender, String[] args, String label) {
		Player player = (Player) sender;
		Location location = player.getLocation();

		BlockDisplay blockDisplay = location.getWorld().spawn(location, BlockDisplay.class, CreatureSpawnEvent.SpawnReason.COMMAND, entity -> {
			entity.setBlock(Material.IRON_BLOCK.createBlockData());
			entity.setBillboard(Display.Billboard.CENTER);
		});

		ItemDisplay itemDisplay = location.getWorld().spawn(location, ItemDisplay.class, CreatureSpawnEvent.SpawnReason.COMMAND, entity -> {
			entity.setItemStack(new ItemStack(Material.MILK_BUCKET));
			entity.setBillboard(Display.Billboard.CENTER);
		});

		TextDisplay textDisplay = location.getWorld().spawn(location, TextDisplay.class, CreatureSpawnEvent.SpawnReason.COMMAND, entity -> {
			entity.text(MiniMessage.miniMessage().deserialize("<rainbow><bold><underlined>Biteeeeeeeeee :3").append(Component.newline()).append(Component.text("Je t'aime Ronnie ❤❤❤", NamedTextColor.LIGHT_PURPLE)));
			entity.setBillboard(Display.Billboard.CENTER);
		});

		Hologram<BlockDisplay> blockHologram = new Hologram<>(args[0] + "_block", player.getUniqueId(), System.currentTimeMillis(), blockDisplay, 20);
		Hologram<ItemDisplay> itemHologram = new Hologram<>(args[0] + "_item", player.getUniqueId(), System.currentTimeMillis(), itemDisplay, 20);
		Hologram<TextDisplay> textHologram = new Hologram<>(args[0] + "_text", player.getUniqueId(), System.currentTimeMillis(), textDisplay, 20);

		HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
		hologramManager.createHologram(blockHologram);
		hologramManager.createHologram(itemHologram);
		hologramManager.createHologram(textHologram);

		player.sendMessage(MiniMessage.miniMessage().deserialize("<rainbow><bold><underlined>Yippeeeeeeeeeeeeee :3"));
	}
}
