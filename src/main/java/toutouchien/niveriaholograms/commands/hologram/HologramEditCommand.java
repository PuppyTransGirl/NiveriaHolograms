package toutouchien.niveriaholograms.commands.hologram;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import toutouchien.niveriaapi.utils.CommandUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.commands.hologram.edit.block.HologramEditBlockCommand;
import toutouchien.niveriaholograms.commands.hologram.edit.general.*;
import toutouchien.niveriaholograms.commands.hologram.edit.item.HologramEditItemCommand;
import toutouchien.niveriaholograms.commands.hologram.edit.other.HologramEditGlowingCommand;
import toutouchien.niveriaholograms.commands.hologram.edit.text.*;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.managers.HologramManager;

public class HologramEditCommand {
    private HologramEditCommand() {
        throw new IllegalStateException("Command class");
    }

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("edit")
                .requires(css -> CommandUtils.defaultRequirements(css, "niveriaholograms.command.hologram.edit"))
                .then(Commands.argument("hologram", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            HologramManager hologramManager = NiveriaHolograms.instance().hologramManager();

                            for (Hologram hologram : hologramManager.holograms())
                                builder.suggest(hologram.name());

                            return builder.buildFuture();
                        })

                        // Block
                        .then(HologramEditBlockCommand.get())

                        // Item
                        .then(HologramEditItemCommand.get())

                        // Text
                        .then(HologramEditAddLineCommand.get())
                        .then(HologramEditBackgroundCommand.get())
                        .then(HologramEditInsertAfterCommand.get())
                        .then(HologramEditInsertBeforeCommand.get())
                        .then(HologramEditRemoveLineCommand.get())
                        .then(HologramEditSeeThroughCommand.get())
                        .then(HologramEditSetLineCommand.get())
                        .then(HologramEditTextAlignmentCommand.get())
                        .then(HologramEditTextShadowCommand.get())
                        .then(HologramEditUpdateIntervalCommand.get())

                        // General
                        .then(HologramEditBillboardCommand.get())
                        .then(HologramEditBrightnessCommand.get())
                        .then(HologramEditPitchCommand.get())
                        .then(HologramEditPositionCommand.get())
                        .then(HologramEditRotationCommand.get())
                        .then(HologramEditScaleCommand.get())
                        .then(HologramEditShadowRadiusCommand.get())
                        .then(HologramEditShadowStrengthCommand.get())
                        .then(HologramEditVisibilityDistanceCommand.get())
                        .then(HologramEditYawCommand.get())

                        // Block & Item
                        .then(HologramEditGlowingCommand.get())
                ).build();
    }
}