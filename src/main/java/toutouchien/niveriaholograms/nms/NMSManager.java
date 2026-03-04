package toutouchien.niveriaholograms.nms;

import org.bukkit.Bukkit;
import toutouchien.niveriaapi.utils.VersionUtils;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.nms.types.NMSBlockHologram;
import toutouchien.niveriaholograms.nms.types.NMSItemHologram;
import toutouchien.niveriaholograms.nms.types.NMSTextHologram;

public class NMSManager {
    private final NMSAdapter nmsAdapter;
    private final NMSBlockHologram nmsBlockHologram;
    private final NMSItemHologram nmsItemHologram;
    private final NMSTextHologram nmsTextHologram;

    public NMSManager(NiveriaHolograms plugin) {
        VersionUtils version = VersionUtils.version();

        if (version == VersionUtils.UNKNOWN) {
            this.nmsAdapter = null;
            this.nmsBlockHologram = null;
            this.nmsItemHologram = null;
            this.nmsTextHologram = null;

            plugin.getSLF4JLogger().error("Unsupported server version ! Cannot load NMSAdapter.", new IllegalStateException());
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        this.nmsAdapter = instantiateNMSAdapter(version);
        this.nmsBlockHologram = (NMSBlockHologram) instantiateNMSHologram(version, "Block");
        this.nmsItemHologram = (NMSItemHologram) instantiateNMSHologram(version, "Item");
        this.nmsTextHologram = (NMSTextHologram) instantiateNMSHologram(version, "Text");
    }

    private NMSAdapter instantiateNMSAdapter(VersionUtils version) {
        try {
            String className = "toutouchien.niveriaholograms.nms." + version.name() + "NMSAdapter";

            Class<?> clazz = Class.forName(className);
            return (NMSAdapter) clazz.getDeclaredConstructor().newInstance();

        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("NMSAdapter not found for version " + version.name() + ". Is the module included?", e);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate NMSAdapter for version " + version.name(), e);
        }
    }

    private Object instantiateNMSHologram(VersionUtils version, String hologramType) {
        try {
            String className = "toutouchien.niveriaholograms.nms.types." + version.name() + "NMS" + hologramType + "Hologram";

            Class<?> clazz = Class.forName(className);
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("NMS" + hologramType + "Hologram not found for version " + version.name() + ". Is the module included?", e);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate NMS" + hologramType + "Hologram for version " + version.name(), e);
        }
    }

    public void createHologram(Hologram hologram) {
        switch (hologram.type()) {
            case BLOCK -> this.nmsBlockHologram.createHologram(hologram);
            case ITEM -> this.nmsItemHologram.createHologram(hologram);
            case TEXT -> this.nmsTextHologram.createHologram(hologram);
        }
    }

    public void updateHologram(Hologram hologram) {
        switch (hologram.type()) {
            case BLOCK -> this.nmsBlockHologram.updateHologram(hologram);
            case ITEM -> this.nmsItemHologram.updateHologram(hologram);
            case TEXT -> this.nmsTextHologram.updateHologram(hologram);
        }
    }

    public NMSAdapter adapter() {
        return this.nmsAdapter;
    }
}
