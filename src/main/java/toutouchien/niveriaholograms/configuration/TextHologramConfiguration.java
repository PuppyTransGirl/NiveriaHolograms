package toutouchien.niveriaholograms.configuration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.TextDisplay;
import toutouchien.niveriaapi.utils.ui.ComponentUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextHologramConfiguration extends HologramConfiguration {
    private List<String> text = new ArrayList<>();
    private Component serializedText;
    private TextColor background;
    private TextDisplay.TextAlignment textAlignment = TextDisplay.TextAlignment.CENTER;
    private boolean seeThrough = false;
    private boolean textShadow = false;

    public TextHologramConfiguration text(List<String> text) {
        this.text = text;
        this.serializedText = null;
        return this;
    }

    public TextHologramConfiguration text(int index, String text) {
        if (index < 0 || index >= this.text.size())
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for text list of size " + this.text.size());

        this.text.set(index, text);
        this.serializedText = null;
        return this;
    }

    public TextHologramConfiguration addText(String text) {
        this.text.add(text);
        this.serializedText = null;
        return this;
    }

    public TextHologramConfiguration removeText(int index) {
        if (index < 0 || index >= this.text.size())
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for text list of size " + this.text.size());

        this.text.remove(index);
        this.serializedText = null;
        return this;
    }

    public TextHologramConfiguration addTextAfter(int index, String text) {
        if (index < 0 || index >= this.text.size())
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for text list of size " + this.text.size());

        this.text.add(index + 1, text);
        this.serializedText = null;
        return this;
    }

    public TextHologramConfiguration addTextBefore(int index, String text) {
        if (index < 0 || index >= this.text.size())
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for text list of size " + this.text.size());

        this.text.add(index, text);
        this.serializedText = null;
        return this;
    }

    public TextHologramConfiguration background(TextColor background) {
        this.background = background;
        return this;
    }

    public TextHologramConfiguration textAlignment(TextDisplay.TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

    public TextHologramConfiguration seeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        return this;
    }

    public TextHologramConfiguration textShadow(boolean textShadow) {
        this.textShadow = textShadow;
        return this;
    }

    public List<String> text() {
        return Collections.unmodifiableList(text);
    }

    public Component serializedText() {
        if (serializedText != null)
            return serializedText;

        List<String> textLines = this.text;
        TextComponent.Builder builder = Component.text();

        for (int i = 0; i < textLines.size(); i++) {
            if (i > 0)
                builder.appendNewline();

            builder.append(ComponentUtils.deserializeMiniMessage(textLines.get(i)));
        }

        return this.serializedText = builder.build();
    }

    public String text(int index) {
        if (index < 0 || index >= text.size())
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for text list of size " + text.size());

        return text.get(index);
    }

    public TextColor background() {
        return background;
    }

    public TextDisplay.TextAlignment textAlignment() {
        return textAlignment;
    }

    public boolean seeThrough() {
        return seeThrough;
    }

    public boolean textShadow() {
        return textShadow;
    }
}
