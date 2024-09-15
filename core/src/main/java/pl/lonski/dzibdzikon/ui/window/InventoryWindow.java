package pl.lonski.dzibdzikon.ui.window;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.Inventory;
import pl.lonski.dzibdzikon.map.TextureId;
import pl.lonski.dzibdzikon.screen.Hud;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

public class InventoryWindow extends WindowAdapter {

    private final int windowWidth = 400;
    private final int windowHeight = 600;
    private List<Entity> items = new ArrayList<>();
    private int selectedItemIdx = 0;
    private InventoryWindowResult result;

    public InventoryWindow(Player player) {
        super(player);
    }

    public Optional<InventoryWindowResult> getResult() {
        return Optional.ofNullable(result);
    }

    @Override
    public void update(float delta) {
        if (visible()) {
            Hud.setActionMessage("");
            positionWindowInCenter(windowWidth, windowHeight);
            items = new ArrayList<>(
                    player.<Inventory>getFeature(FeatureType.INVENTORY).getItems());
        }
    }

    @Override
    public void render(float delta) {
        if (visible()) {
            var shapeRenderer = getGameResources().shapeRenderer;

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Window.FRAME_COLOR);
            shapeRenderer.rect(position.x(), position.y(), windowWidth, windowHeight);
            shapeRenderer.setColor(Window.BG_COLOR);
            shapeRenderer.rect(position.x() + 4, position.y() + 4, windowWidth - 8, windowHeight - 8);
            shapeRenderer.end();

            var batch = getGameResources().batch;
            batch.begin();

            var itemFont = getGameResources().fontItalic20;
            var itemPos = new Point(position.x() + 16, position.y() + windowHeight - 10 - 42);
            for (int i = 0; i < items.size(); i++) {
                var item = items.get(i);
                batch.draw(
                        getGameResources().textures.get(TextureId.ICON_BACKGROUND), itemPos.x(), itemPos.y() - 42 * i);
                batch.draw(getGameResources().textures.get(item.getGlyph()), itemPos.x(), itemPos.y() - 42 * i);

                itemFont.setColor(selectedItemIdx == i ? Color.FOREST : Color.NAVY);
                itemFont.draw(batch, item.getName(), itemPos.x() + 38, itemPos.y() + 24 - i * 42);
            }

            var helpPos = new Point(position.x() + windowWidth + 12, position.y() + windowHeight - 4);
            var helpFont = getGameResources().fontItalic15;
            helpFont.setColor(Color.ORANGE);
            var helpLines = List.of("<esc> - zamknij okno", "<enter> - użyj przedmiotu");
            for (int i = 0; i < helpLines.size(); i++) {
                helpFont.draw(batch, helpLines.get(i), helpPos.x(), helpPos.y() - i * 18);
            }

            batch.end();
        }
    }

    @Override
    public void onInput(DzibdziInput.DzibdziKey key) {
        if (!visible() || key.released()) {
            return;
        }

        if (key.keyCode() == Input.Keys.ESCAPE) {
            result = null;
            hide();
        } else if (key.isUpKey()) {
            selectedItemIdx = Math.max(0, selectedItemIdx - 1);
        } else if (key.isDownKey()) {
            selectedItemIdx = Math.min(items.size() - 1, selectedItemIdx + 1);
        } else if (key.isEnterKey()) {
            result = new InventoryWindowResult(items.get(selectedItemIdx), ItemAction.USE);
            hide();
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

    public record InventoryWindowResult(Entity item, ItemAction action) {}

    public enum ItemAction {
        USE
    }
}
