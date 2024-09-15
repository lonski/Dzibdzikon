package pl.lonski.dzibdzikon.ui.window;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pl.lonski.dzibdzikon.CameraUtils;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.action.CastSpellAction;
import pl.lonski.dzibdzikon.action.targeting.TargetConsumer;
import pl.lonski.dzibdzikon.action.targeting.TargeterFactory;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.Quickbar;
import pl.lonski.dzibdzikon.entity.features.SpellBook;
import pl.lonski.dzibdzikon.map.TextureId;
import pl.lonski.dzibdzikon.screen.Hud;
import pl.lonski.dzibdzikon.spell.Spell;

import java.util.List;
import java.util.Optional;

import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;
import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

public class SpellBookWindow extends WindowAdapter implements DzibdziInput.DzibdziInputListener {

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
            Hud.setActionMessage("Wybierz czar to rzucenia lub wciśnij 1..5 aby przypisać do panelu podręcznego.");
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
            var descriptionFontBold = getGameResources().fontItalicBold15;
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
                    var desc = spell.getDescription().descriptionLines();
                    // description text
                    descriptionFont.setColor(Color.NAVY);
                    for (int w = 0; w < desc.size(); w++) {
                        descriptionFont.draw(batch, desc.get(w), spellDescX, spellDescY - w * 20);
                    }

                    // targeting text
                    descriptionFontBold.setColor(Color.NAVY);
                    var posY = spellDescY - desc.size() * 20 - 16;
                    descriptionFontBold.draw(
                            batch, "Cel: " + spell.getDescription().targetingMode(), spellDescX, posY);
                    descriptionFontBold.draw(
                            batch, "Obszar: " + spell.getDescription().range(), spellDescX, posY - 20);
                }
            }
        }
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
            hide();
        } else if (key.isUpKey()) {
            selectedSpellIdx = Math.max(0, selectedSpellIdx - 1);
        } else if (key.isDownKey()) {
            selectedSpellIdx = Math.min(spells.size() - 1, selectedSpellIdx + 1);
        } else if (key.isEnterKey()) {
            selectedSpell = spells.get(selectedSpellIdx);
            hide();
        } else {
            var slot =
                    switch (key.keyCode()) {
                        case Input.Keys.NUM_1 -> Quickbar.SlotType.NUM_1;
                        case Input.Keys.NUM_2 -> Quickbar.SlotType.NUM_2;
                        case Input.Keys.NUM_3 -> Quickbar.SlotType.NUM_3;
                        case Input.Keys.NUM_4 -> Quickbar.SlotType.NUM_4;
                        case Input.Keys.NUM_5 -> Quickbar.SlotType.NUM_5;
                        default -> null;
                    };
            if (slot != null) {
                var spell = spells.get(selectedSpellIdx);
                player.getQuickbar().setSlot(slot, spell.getIcon(), () -> {
                    TargetConsumer onTargetSelected = target -> new CastSpellAction(player, target, spell);
                    return TargeterFactory.create(player, spell.getTargetingMode(), onTargetSelected);
                });
                hide();
            }
        }
    }

    @Override
    public void hide() {
        if (onClose != null) {
            onClose.accept(this);
            onClose = null;
        }
        Hud.setActionMessage("");
        super.hide();
    }
}
