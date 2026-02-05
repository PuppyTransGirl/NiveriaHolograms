package toutouchien.niveriaholograms.migration;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import toutouchien.niveriaapi.lang.Lang;
import toutouchien.niveriaapi.menu.Menu;
import toutouchien.niveriaapi.menu.MenuContext;
import toutouchien.niveriaapi.menu.component.MenuComponent;
import toutouchien.niveriaapi.menu.component.interactive.Button;
import toutouchien.niveriaapi.menu.component.layout.Grid;
import toutouchien.niveriaapi.utils.ItemBuilder;
import toutouchien.niveriaholograms.NiveriaHolograms;

import java.io.File;

import static toutouchien.niveriaholograms.NiveriaHolograms.LANG;

public class MigrationMenu extends Menu {
    /**
     * Constructs a new Menu for the specified player.
     *
     * @param player the player who will interact with this menu
     * @throws NullPointerException if player is null
     */
    public MigrationMenu(@NotNull Player player) {
        super(player);
    }

    /**
     * Returns the title component for this menu's inventory.
     * <p>
     * This method must be implemented by subclasses to define the menu's title.
     *
     * @return the title component displayed at the top of the inventory
     */
    @Override
    protected @NotNull Component title() {
        return LANG.get("niveriaholograms.menu.migration.title");
    }

    /**
     * Creates and returns the root component for this menu.
     * <p>
     * This method must be implemented by subclasses to define the menu's layout
     * and components.
     *
     * @param context the menu context for component interaction
     * @return the root component that defines the menu's structure
     */
    @Override
    protected @NotNull MenuComponent root(@NotNull MenuContext context) {
        Button decentHologramsButton = Button.create()
                .item(ctx -> {
                    File pluginHolograms = new File("plugins/DecentHolograms/holograms/");
                    String presence = "niveriaholograms.menu.migration.decentholograms.lore" + (pluginHolograms.isDirectory() ? "_present" : "_not_present");

                    return ItemBuilder.of(Material.PLAYER_HEAD)
                            .name(LANG.get("niveriaholograms.menu.migration.decentholograms.name"))
                            .lore(LANG.getList("niveriaholograms.menu.migration.decentholograms.lore",
                                    Lang.placeholder("niveriaholograms_migration_presence", LANG.getString(presence))
                            ))
                            .headTexture("http://textures.minecraft.net/texture/a7ab5cf2dfdc9e8ee3c79db14226cdf41b1b15f67b1613184a49e3c13e379de")
                            .build();
                })
                .onClick(event -> {
                    Player player = event.player();
                    NiveriaHolograms.instance().migrationManager().convertFromDecentHolograms(player);
                    player.closeInventory();
                })
                .build();

        Button fancyHologramsButton = Button.create()
                .item(ctx -> {
                    File pluginHolograms = new File("plugins/FancyHolograms/holograms.yml");
                    String presence = "niveriaholograms.menu.migration.fancyholograms.lore" + (pluginHolograms.isFile() ? "_present" : "_not_present");

                    return ItemBuilder.of(Material.PLAYER_HEAD)
                            .name(LANG.get("niveriaholograms.menu.migration.fancyholograms.name"))
                            .lore(LANG.getList("niveriaholograms.menu.migration.fancyholograms.lore",
                                    Lang.placeholder("niveriaholograms_migration_presence", LANG.getString(presence))
                            ))
                            .headTexture("http://textures.minecraft.net/texture/70dc9420c14fcab98dcd6f5ad51e8ebe2bb97895976caa70578f73c66dfbd")
                            .build();
                })
                .onClick(event -> {
                    Player player = event.player();
                    NiveriaHolograms.instance().migrationManager().convertFromFancyHologramsV2(player);
                    player.closeInventory();
                })
                .build();

        return Grid.create()
                .size(9, 3)
                .add(context, 11, decentHologramsButton)
                .add(context, 15, fancyHologramsButton)
                .build();
    }
}
