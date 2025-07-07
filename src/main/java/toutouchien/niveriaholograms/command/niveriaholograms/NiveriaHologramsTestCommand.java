package toutouchien.niveriaholograms.command.niveriaholograms;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaholograms.hologram.Hologram;

public class NiveriaHologramsTestCommand extends SubCommand {
	NiveriaHologramsTestCommand() {
		super(new CommandData("test", "niveriaholograms"));
	}

	@Override
	public void execute(CommandSender sender, String[] args, String label) {
		Player player = (Player) sender;

/*		net.minecraft.world.entity.Display.BlockDisplay blockDisplay = location.getWorld().spawn(location, BlockDisplay.class, CreatureSpawnEvent.SpawnReason.COMMAND, entity -> {
			entity.setBlock(Material.IRON_BLOCK.createBlockData());
			entity.setBillboard(Display.Billboard.CENTER);
		});

		net.minecraft.world.entity.Display.ItemDisplay itemDisplay = location.getWorld().spawn(location, ItemDisplay.class, CreatureSpawnEvent.SpawnReason.COMMAND, entity -> {
			entity.setItemStack(new ItemStack(Material.MILK_BUCKET));
			entity.setBillboard(Display.Billboard.CENTER);
		});

		net.minecraft.world.entity.Display.TextDisplay textDisplay = location.getWorld().spawn(location, TextDisplay.class, CreatureSpawnEvent.SpawnReason.COMMAND, entity -> {
			entity.text(MiniMessage.miniMessage().deserialize("<rainbow><bold><underlined>Biteeeeeeeeee :3").append(Component.newline()).append(Component.text("Je t'aime Ronnie ❤❤❤", NamedTextColor.LIGHT_PURPLE)));
			entity.setBillboard(Display.Billboard.CENTER);
		});

		Hologram<net.minecraft.world.entity.Display.BlockDisplay> blockHologram = new Hologram<>(args[0] + "_block", player.getUniqueId(), System.currentTimeMillis(), blockDisplay, 20);
		Hologram<net.minecraft.world.entity.Display.ItemDisplay> itemHologram = new Hologram<>(args[0] + "_item", player.getUniqueId(), System.currentTimeMillis(), itemDisplay, 20);
		Hologram<net.minecraft.world.entity.Display.TextDisplay> textHologram = new Hologram<>(args[0] + "_text", player.getUniqueId(), System.currentTimeMillis(), textDisplay, 20);*/

/*		HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
		long ms = System.currentTimeMillis();
		hologramManager.create(player, HologramType.BLOCK, ms + "_block");
		hologramManager.create(player, HologramType.ITEM, ms + "_item");
		hologramManager.create(player, HologramType.TEXT, ms + "_text");

		player.sendMessage(MiniMessage.miniMessage().deserialize("<rainbow><bold><underlined>Yippeeeeeeeeeeeeee :3"));*/
		Hologram.printPacketStats();
	}
}
