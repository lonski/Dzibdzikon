package pl.lonski.dzibdzikon.ui.window;

import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import pl.lonski.dzibdzikon.Debouncer;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.Inventory;
import pl.lonski.dzibdzikon.map.TextureId;
import pl.lonski.dzibdzikon.screen.Hud;

public class InventoryWindow extends WindowAdapter {

    private static final int MAX_ITEMS_DISPLAYED = 12;
    private final int windowWidth = 400;
    private final int windowHeight = 572;
    private List<StackedItem> items = new ArrayList<>();
    private int selectedItemIdx = 0;
    private InventoryWindowResult result;
    private DzibdziInput.DzibdziKey key;
    private final Debouncer debouncer = new Debouncer(0.1f);

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

            this.items = player.<Inventory>getFeature(FeatureType.INVENTORY).getItems().stream()
                    .collect(Collectors.groupingBy(Entity::getName))
                    .values()
                    .stream()
                    .map(StackedItem::new)
                    .collect(Collectors.toList());

            if (key != null && debouncer.debounce(delta)) {
                if (key.isUpKey()) {
                    selectedItemIdx = Math.max(0, selectedItemIdx - 1);
                } else if (key.isDownKey()) {
                    selectedItemIdx = Math.min(items.size() - 1, selectedItemIdx + 1);
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        if (visible()) {
            var camera = getGameResources().uiCamera;
            var batch = getGameResources().batch;
            var shapeRenderer = getGameResources().shapeRenderer;

            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Window.FRAME_COLOR);
            shapeRenderer.rect(position.x(), position.y(), windowWidth, windowHeight);
            shapeRenderer.setColor(Window.BG_COLOR);
            shapeRenderer.rect(position.x() + 4, position.y() + 4, windowWidth - 8, windowHeight - 8);
            shapeRenderer.end();

            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            var itemFont = getGameResources().fontItalic20;
            var itemPos = new Point(position.x() + 16, position.y() + windowHeight - 10 - 42);

            var startIdx = 0;
            var endIdx = items.size();
            var displayScrollArrowUp = false;
            var displayScrollArrowDown = false;
            if (items.size() > MAX_ITEMS_DISPLAYED) {
                startIdx = Math.max(0, selectedItemIdx - MAX_ITEMS_DISPLAYED);
                endIdx = startIdx + MAX_ITEMS_DISPLAYED + 1;
                displayScrollArrowUp = startIdx > 0;
                displayScrollArrowDown = endIdx < items.size();
            }
            for (int i = startIdx; i < endIdx; i++) {
                var item = items.get(i).entities().get(0);
                var count = items.get(i).entities().size();
                var offsetMultiplier = i - startIdx;
                batch.draw(
                        getGameResources().textures.get(TextureId.ICON_BACKGROUND),
                        itemPos.x(),
                        itemPos.y() - 42 * offsetMultiplier);
                batch.draw(
                        getGameResources().textures.get(item.getGlyph()),
                        itemPos.x(),
                        itemPos.y() - 42 * offsetMultiplier);

                itemFont.setColor(selectedItemIdx == i ? Color.FOREST : Color.NAVY);
                itemFont.draw(
                        batch,
                        item.getName() + (count > 1 ? " (" + count + ")" : ""),
                        itemPos.x() + 38,
                        itemPos.y() + 24 - offsetMultiplier * 42);
            }

            if (displayScrollArrowUp) {
                var upArrowPos = new Point(position.x() + windowWidth - 42, position.y() + windowHeight - 42);
                batch.draw(getGameResources().textures.get(TextureId.ARROW_UP), upArrowPos.x(), upArrowPos.y());
            }

            if (displayScrollArrowDown) {
                var upArrowPos = new Point(position.x() + windowWidth - 42, position.y() + 10);
                batch.draw(getGameResources().textures.get(TextureId.ARROW_DOWN), upArrowPos.x(), upArrowPos.y());
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
    public boolean onInput(DzibdziInput.DzibdziKey key) {
        if (!visible() || key.released()) {
            this.key = null;
            return false;
        }

        this.key = key;

        if (key.keyCode() == Input.Keys.ESCAPE) {
            result = null;
            hide();
        } else if (key.isEnterKey()) {
            if (selectedItemIdx < items.size() && selectedItemIdx >= 0) {
                result = new InventoryWindowResult(
                        items.get(selectedItemIdx).entities().get(0), ItemAction.USE);
            } else {
                result = null;
            }
            hide();
        }

        return true;
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

    record StackedItem(List<Entity> entities) {}

    public record InventoryWindowResult(Entity item, ItemAction action) {}

    public enum ItemAction {
        USE
    }
}
