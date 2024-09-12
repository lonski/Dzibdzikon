package pl.lonski.dzibdzikon.ui.window;

import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;
import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import pl.lonski.dzibdzikon.CameraUtils;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.SpellBook;
import pl.lonski.dzibdzikon.map.TextureId;
import pl.lonski.dzibdzikon.spell.Spell;

public class SpellBookWindow extends WindowAdapter implements DzibdziInput.DzibdziInputListener {

    private static final int MAX_LINE_WIDTH = 42;

    private Spell selectedSpell;
    private int selectedSpellIdx = 0;
    private final TextureRegion windowTexture;

    public SpellBookWindow(Player player) {
        super(player);
        windowTexture = getGameResources().textures.get(TextureId.WND_SPELLBOOK);
    }

    @Override
    public void update(float delta) {
        if (visible()) {
            positionWindowInCenter();
        }
    }

    @Override
    public void render(float delta) {
        if (visible()) {
            var batch = getGameResources().batch;
            var textures = getGameResources().textures;
            var font = getGameResources().fontItalic20;
            var descriptionFont = getGameResources().fontItalic15;
            batch.draw(windowTexture, position.x(), position.y());

            var spellY = position.y() + windowTexture.getRegionHeight() - 64;
            var spellX = position.x() + 32;
            var spellDescX = spellX + windowTexture.getRegionWidth() / 2 + 16;
            var spellDescY = position.y() + windowTexture.getRegionHeight() - 32;

            var spells = getSpells();
            for (int i = 0; i < spells.size(); i++) {
                var spell = spells.get(i);
                font.setColor(selectedSpellIdx == i ? Color.FOREST : Color.NAVY);
                batch.draw(textures.get(spell.getIcon()), spellX, spellY - i * 42);
                font.draw(batch, spell.getName(), spellX + TILE_WIDTH + 6, spellY + 24 - i * 42);

                if (i == selectedSpellIdx) {
                    var desc = splitDescription(spell.getDescription());
                    descriptionFont.setColor(Color.NAVY);
                    for (int w = 0; w < desc.size(); w++) {
                        descriptionFont.draw(batch, desc.get(w), spellDescX, spellDescY - w * 20);
                    }
                }
            }
        }
    }

    private List<String> splitDescription(String description) {
        var words = description.split(" ");
        var lines = new ArrayList<String>();
        var line = new StringBuilder();
        for (var word : words) {
            if (line.length() + word.length() > MAX_LINE_WIDTH) {
                lines.add(line.toString());
                line = new StringBuilder();
            }
            line.append(word).append(" ");
        }
        lines.add(line.toString());
        return lines;
    }

    private void positionWindowInCenter() {
        var bottomLeftCorner = CameraUtils.getBottomLeftCorner(getGameResources().camera);
        var heightRem = getGameResources().camera.viewportHeight - windowTexture.getRegionHeight();
        var widthRem = getGameResources().camera.viewportWidth - windowTexture.getRegionWidth();

        position = new Point((int) (bottomLeftCorner.x + widthRem / 2f), (int) (bottomLeftCorner.y + heightRem / 2f));
    }

    private List<Spell> getSpells() {
        return player.<SpellBook>getFeature(FeatureType.SPELLBOOK).getSpells();
    }

    public Optional<Spell> takeSelectedSpell() {
        var spell = selectedSpell;
        selectedSpell = null;
        return Optional.ofNullable(spell);
    }

    @Override
    public void onInput(DzibdziInput.DzibdziKey key) {
        if (!visible() || key.released()) {
            return;
        }

        var spells = getSpells();
        if (key.keyCode() == Input.Keys.ESCAPE) {
            selectedSpell = null;
            if (onClose != null) {
                onClose.accept(this);
                onClose = null;
            }
            hide();
        } else if (key.isUpKey()) {
            selectedSpellIdx = Math.max(0, selectedSpellIdx - 1);
        } else if (key.isDownKey()) {
            selectedSpellIdx = Math.min(spells.size() - 1, selectedSpellIdx + 1);
        } else if (key.isEnterKey()) {
            selectedSpell = spells.get(selectedSpellIdx);
            if (onClose != null) {
                onClose.accept(this);
                onClose = null;
            }
            hide();
        }
    }
}
