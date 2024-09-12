package pl.lonski.dzibdzikon.ui.window;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.List;
import java.util.Optional;
import pl.lonski.dzibdzikon.CameraUtils;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.SpellBook;
import pl.lonski.dzibdzikon.map.Glyph;
import pl.lonski.dzibdzikon.spell.Spell;

public class SpellBookWindow extends WindowAdapter implements DzibdziInput.DzibdziInputListener {

    private Spell selectedSpell;
    private final TextureRegion windowTexture;

    public SpellBookWindow(Dzibdzikon game, Player player) {
        super(game, player);
        windowTexture = game.textures.get(Glyph.WND_SPELLBOOK);
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
            game.batch.draw(windowTexture, position.x(), position.y());
        }
    }

    private void positionWindowInCenter() {
        var bottomLeftCorner = CameraUtils.getBottomLeftCorner(camera);
        var heightRem = camera.viewportHeight - windowTexture.getRegionHeight();
        var widthRem = camera.viewportWidth - windowTexture.getRegionWidth();

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

        if (key.keyCode() == Input.Keys.ESCAPE) {
            selectedSpell = null;
            if (onClose != null) {
                onClose.accept(this);
                onClose = null;
            }
            hide();
        }
    }
}
