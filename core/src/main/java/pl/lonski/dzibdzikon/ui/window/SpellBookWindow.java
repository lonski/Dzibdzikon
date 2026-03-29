package pl.lonski.dzibdzikon.ui.window;

import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;
import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import java.util.List;
import java.util.Optional;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.action.CastSpellAction;
import pl.lonski.dzibdzikon.action.targeting.TargetConsumer;
import pl.lonski.dzibdzikon.action.targeting.TargeterFactory;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.Quickbar;
import pl.lonski.dzibdzikon.entity.features.MagicUser;
import pl.lonski.dzibdzikon.map.TextureId;
import pl.lonski.dzibdzikon.screen.Hud;
import pl.lonski.dzibdzikon.spell.Spell;

public class SpellBookWindow extends WindowAdapter {

    private static final int BUTTON_HEIGHT = 34;
    private static final int BUTTON_Y_OFFSET = 12;
    private static final int CAST_BUTTON_X_OFFSET = 10;
    private static final int CAST_BUTTON_WIDTH = 90;
    private static final int ASSIGN_BUTTON_X_OFFSET = 110;
    private static final int ASSIGN_BUTTON_WIDTH = 160;
    private static final int CLOSE_BUTTON_X_OFFSET = 280;
    private static final int CLOSE_BUTTON_WIDTH = 90;
    private static final int SLOT_BUTTON_WIDTH = 58;

    private Spell selectedSpell;
    private int selectedSpellIdx = 0;
    private boolean assignMode = false;
    private final TextureRegion windowTexture;

    public SpellBookWindow(Player player) {
        super(player);
        windowTexture = getGameResources().textures.get(TextureId.WND_SPELLBOOK);
    }

    @Override
    public void update(float delta) {
        if (visible()) {
            Hud.setActionMessage("");
            positionWindowInCenter(windowTexture.getRegionWidth(), windowTexture.getRegionHeight());
        }
    }

    @Override
    public void render(float delta) {
        if (visible()) {
            var camera = getGameResources().uiCamera;
            var batch = getGameResources().batch;
            var shapeRenderer = getGameResources().shapeRenderer;

            batch.setProjectionMatrix(camera.combined);
            batch.begin();
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
                    descriptionFont.setColor(Color.NAVY);
                    for (int w = 0; w < desc.size(); w++) {
                        descriptionFont.draw(batch, desc.get(w), spellDescX, spellDescY - w * 20);
                    }

                    descriptionFontBold.setColor(Color.NAVY);
                    var posY = spellDescY - desc.size() * 20 - 16;
                    descriptionFontBold.draw(
                            batch, "Cel: " + spell.getDescription().targetingMode(), spellDescX, posY);
                    descriptionFontBold.draw(
                            batch, "Obszar: " + spell.getDescription().range(), spellDescX, posY - 20);

                    descriptionFontBold.setColor(Color.BLUE);
                    descriptionFontBold.draw(
                            batch, "Koszt: " + spell.getDescription().cost(), spellDescX, posY - 40);
                }
            }
            batch.end();

            // Touch button backgrounds
            int btnY = position.y() + BUTTON_Y_OFFSET;
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.DARK_GRAY);
            if (assignMode) {
                for (int i = 0; i < 5; i++) {
                    shapeRenderer.rect(position.x() + 10 + i * (SLOT_BUTTON_WIDTH + 4), btnY,
                            SLOT_BUTTON_WIDTH, BUTTON_HEIGHT);
                }
                shapeRenderer.rect(position.x() + 10 + 5 * (SLOT_BUTTON_WIDTH + 4), btnY, 80, BUTTON_HEIGHT);
            } else {
                shapeRenderer.rect(position.x() + CAST_BUTTON_X_OFFSET, btnY, CAST_BUTTON_WIDTH, BUTTON_HEIGHT);
                shapeRenderer.rect(position.x() + ASSIGN_BUTTON_X_OFFSET, btnY, ASSIGN_BUTTON_WIDTH, BUTTON_HEIGHT);
                shapeRenderer.rect(position.x() + CLOSE_BUTTON_X_OFFSET, btnY, CLOSE_BUTTON_WIDTH, BUTTON_HEIGHT);
            }
            shapeRenderer.end();

            // Touch button labels
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            font.setColor(Color.WHITE);
            if (assignMode) {
                for (int i = 0; i < 5; i++) {
                    font.draw(batch, String.valueOf(i + 1),
                            position.x() + 10 + i * (SLOT_BUTTON_WIDTH + 4) + 22, btnY + BUTTON_HEIGHT - 8);
                }
                font.draw(batch, "X",
                        position.x() + 10 + 5 * (SLOT_BUTTON_WIDTH + 4) + 28, btnY + BUTTON_HEIGHT - 8);
            } else {
                font.draw(batch, "CAST", position.x() + CAST_BUTTON_X_OFFSET + 12, btnY + BUTTON_HEIGHT - 8);
                font.draw(batch, "ASSIGN", position.x() + ASSIGN_BUTTON_X_OFFSET + 20, btnY + BUTTON_HEIGHT - 8);
                font.draw(batch, "CLOSE", position.x() + CLOSE_BUTTON_X_OFFSET + 10, btnY + BUTTON_HEIGHT - 8);
            }
            batch.end();
        }
    }

    private List<Spell> getSpells() {
        return player.<MagicUser>getFeature(FeatureType.MAGIC_USER).getSpells();
    }

    public Optional<Spell> takeSelectedSpell() {
        var spell = selectedSpell;
        selectedSpell = null;
        return Optional.ofNullable(spell);
    }

    @Override
    public boolean onInput(DzibdziInput.DzibdziKey key) {
        if (!visible() || key.released()) {
            return false;
        }

        if (key.screenTouchCoords() != null) {
            return handleTouch(key.screenTouchCoords());
        }

        var spells = getSpells();
        if (key.keyCode() == Input.Keys.ESCAPE) {
            if (assignMode) {
                assignMode = false;
            } else {
                selectedSpell = null;
                hide();
            }
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
                assignSpellToSlot(spells.get(selectedSpellIdx), slot);
            }
        }

        return true;
    }

    private boolean handleTouch(pl.lonski.dzibdzikon.Point screenCoords) {
        var vec = new Vector3(screenCoords.x(), screenCoords.y(), 0);
        getGameResources().uiCamera.unproject(vec);
        float tx = vec.x;
        float ty = vec.y;

        int btnY = position.y() + BUTTON_Y_OFFSET;

        if (assignMode) {
            // Slot 1-5 buttons
            for (int i = 0; i < 5; i++) {
                int bx = position.x() + 10 + i * (SLOT_BUTTON_WIDTH + 4);
                if (tx >= bx && tx <= bx + SLOT_BUTTON_WIDTH && ty >= btnY && ty <= btnY + BUTTON_HEIGHT) {
                    var slot = Quickbar.SlotType.values()[i];
                    var spells = getSpells();
                    if (selectedSpellIdx < spells.size()) {
                        assignSpellToSlot(spells.get(selectedSpellIdx), slot);
                    }
                    return true;
                }
            }
            // Cancel assign mode
            int cancelX = position.x() + 10 + 5 * (SLOT_BUTTON_WIDTH + 4);
            if (tx >= cancelX && tx <= cancelX + 80 && ty >= btnY && ty <= btnY + BUTTON_HEIGHT) {
                assignMode = false;
                return true;
            }
            return true;
        }

        // CAST button
        if (tx >= position.x() + CAST_BUTTON_X_OFFSET
                && tx <= position.x() + CAST_BUTTON_X_OFFSET + CAST_BUTTON_WIDTH
                && ty >= btnY && ty <= btnY + BUTTON_HEIGHT) {
            var spells = getSpells();
            if (selectedSpellIdx < spells.size()) {
                selectedSpell = spells.get(selectedSpellIdx);
            }
            hide();
            return true;
        }

        // ASSIGN button
        if (tx >= position.x() + ASSIGN_BUTTON_X_OFFSET
                && tx <= position.x() + ASSIGN_BUTTON_X_OFFSET + ASSIGN_BUTTON_WIDTH
                && ty >= btnY && ty <= btnY + BUTTON_HEIGHT) {
            assignMode = true;
            return true;
        }

        // CLOSE button
        if (tx >= position.x() + CLOSE_BUTTON_X_OFFSET
                && tx <= position.x() + CLOSE_BUTTON_X_OFFSET + CLOSE_BUTTON_WIDTH
                && ty >= btnY && ty <= btnY + BUTTON_HEIGHT) {
            selectedSpell = null;
            hide();
            return true;
        }

        // Spell rows (left half of window)
        var spells = getSpells();
        int spellY = position.y() + windowTexture.getRegionHeight() - 64;
        for (int i = 0; i < spells.size(); i++) {
            int rowY = spellY - i * 42;
            if (tx >= position.x() + 16 && tx <= position.x() + windowTexture.getRegionWidth() / 2
                    && ty >= rowY && ty <= rowY + 42) {
                if (selectedSpellIdx == i) {
                    selectedSpell = spells.get(i);
                    hide();
                } else {
                    selectedSpellIdx = i;
                }
                return true;
            }
        }

        return true;
    }

    private void assignSpellToSlot(Spell spell, Quickbar.SlotType slot) {
        player.getQuickbar().setSlot(slot, spell.getIcon(), () -> {
            TargetConsumer onTargetSelected = target -> new CastSpellAction(player, target, spell);
            return TargeterFactory.create(player, spell.getTargetingMode(), onTargetSelected);
        });
        assignMode = false;
        hide();
    }

    @Override
    public void hide() {
        assignMode = false;
        if (onClose != null) {
            onClose.accept(this);
            onClose = null;
        }
        Hud.setActionMessage("");
        super.hide();
    }
}
