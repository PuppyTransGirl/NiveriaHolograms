package toutouchien.niveriaholograms.configurations.special;

import net.kyori.adventure.text.format.TextColor;
import toutouchien.niveriaholograms.configurations.HologramConfiguration;

public class GlowingHologramConfiguration extends HologramConfiguration {
    private boolean glowing;
    private TextColor glowingColor;

    public GlowingHologramConfiguration() {
        // Needed for BlockHologramConfiguration & ItemHologramConfiguration (it's not used in HologramType)
    }

    private GlowingHologramConfiguration(HologramConfiguration basicConfig) {
        this.scale(basicConfig.scale());
        this.translation(basicConfig.translation());
        this.billboard(basicConfig.billboard());
        this.brightness(basicConfig.brightness());
        this.shadowRadius(basicConfig.shadowRadius());
        this.shadowStrength(basicConfig.shadowStrength());
        this.visibilityDistance(basicConfig.visibilityDistance());
    }

    public boolean glowing() {
        return glowing;
    }

    public TextColor glowingColor() {
        return glowingColor;
    }

    public GlowingHologramConfiguration glowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    public GlowingHologramConfiguration glowingColor(TextColor glowingColor) {
        this.glowingColor = glowingColor;
        return this;
    }

    @Override
    public GlowingHologramConfiguration copy() {
        GlowingHologramConfiguration copy = new GlowingHologramConfiguration(super.copy());

        copy.glowing = this.glowing;
        copy.glowingColor = this.glowingColor;

        return copy;
    }
}
