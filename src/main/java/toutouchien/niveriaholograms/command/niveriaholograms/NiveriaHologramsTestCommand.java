package toutouchien.niveriaholograms.command.niveriaholograms;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.command.CommandData;
import toutouchien.niveriaapi.command.SubCommand;
import toutouchien.niveriaholograms.hologram.Hologram;

public class NiveriaHologramsTestCommand extends SubCommand {
	NiveriaHologramsTestCommand() {
		super(new CommandData("test", "niveriaholograms"));
	}

	@Override
	public void execute(@NotNull CommandSender sender, String @NotNull [] args, @NotNull String label) {
		Hologram.printPacketStats();
	}
}
