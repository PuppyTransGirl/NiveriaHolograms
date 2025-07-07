package toutouchien.niveriaholograms.command.niveriaholograms;

import org.bukkit.entity.Player;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.hologram.HologramManager;
import toutouchien.niveriaholograms.hologram.HologramType;

import java.util.UUID;

public class NiveriaHologramCreateCommand extends SubCommand {
	NiveriaHologramCreateCommand() {
		super(new CommandData("create", "niveriaholograms")
				.playerRequired(true));
	}

	@Override
	public void execute(Player player, String[] args, String label) {
		HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();
		UUID uuid = UUID.randomUUID();
		hologramManager.create(player, HologramType.BLOCK, uuid + "_block");
		hologramManager.create(player, HologramType.ITEM, uuid + "_item");
		hologramManager.create(player, HologramType.TEXT, uuid + "_text");

		player.sendRichMessage("<rainbow><bold><underlined>Yippeeeeeeeeeeeeee :3");
	}
}
