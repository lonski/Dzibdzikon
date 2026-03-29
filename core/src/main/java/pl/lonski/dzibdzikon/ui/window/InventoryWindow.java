package pl.lonski.dzibdzikon.ui.window;

import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
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
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 34;
    private static final int BUTTON_Y_OFFSET = 12;
    private static final int USE_BUTTON_X_OFFSET = 10;
    private static final int CLOSE_BUTTON_X_OFFSET = 150;

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

            if (key != null && key.screenTouchCoords() == null && debouncer.debounce(delta)) {
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
            // Touch buttons background
            int btnY = position.y() + BUTTON_Y_OFFSET;
            shapeRenderer.setColor(Color.DARK_GRAY);
            shapeRenderer.rect(position.x() + USE_BUTTON_X_OFFSET, btnY, BUTTON_WIDTH, BUTTON_HEIGHT);
            shapeRenderer.rect(position.x() + CLOSE_BUTTON_X_OFFSET, btnY, BUTTON_WIDTH, BUTTON_HEIGHT);
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
                var downArrowPos = new Point(position.x() + windowWidth - 42, position.y() + 10);
                batch.draw(getGameResources().textures.get(TextureId.ARROW_DOWN), downArrowPos.x(), downArrowPos.y());
            }

            // Touch button labels
            itemFont.setColor(Color.WHITE);
            itemFont.draw(batch, "USE", position.x() + USE_BUTTON_X_OFFSET + 30, btnY + BUTTON_HEIGHT - 8);
            itemFont.draw(batch, "CLOSE", position.x() + CLOSE_BUTTON_X_OFFSET + 18, btnY + BUTTON_HEIGHT - 8);

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

        if (key.screenTouchCoords() != null) {
            return handleTouch(key.screenTouchCoords());
        }

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

    private boolean handleTouch(Point screenCoords) {
        var vec = new Vector3(screenCoords.x(), screenCoords.y(), 0);
        getGameResources().uiCamera.unproject(vec);
        float tx = vec.x;
        float ty = vec.y;

        int btnY = position.y() + BUTTON_Y_OFFSET;

        // USE button
        if (tx >= position.x() + USE_BUTTON_X_OFFSET
                && tx <= position.x() + USE_BUTTON_X_OFFSET + BUTTON_WIDTH
                && ty >= btnY && ty <= btnY + BUTTON_HEIGHT) {
            if (selectedItemIdx >= 0 && selectedItemIdx < items.size()) {
                result = new InventoryWindowResult(
                        items.get(selectedItemIdx).entities().get(0), ItemAction.USE);
            } else {
                result = null;
            }
            hide();
            return true;
        }

        // CLOSE button
        if (tx >= position.x() + CLOSE_BUTTON_X_OFFSET
                && tx <= position.x() + CLOSE_BUTTON_X_OFFSET + BUTTON_WIDTH
                && ty >= btnY && ty <= btnY + BUTTON_HEIGHT) {
            result = null;
            hide();
            return true;
        }

        // Arrow UP (scroll up)
        if (items.size() > MAX_ITEMS_DISPLAYED) {
            int startIdx = Math.max(0, selectedItemIdx - MAX_ITEMS_DISPLAYED);
            if (startIdx > 0) {
                int upArrowX = position.x() + windowWidth - 42;
                int upArrowY = position.y() + windowHeight - 42;
                if (tx >= upArrowX && tx <= upArrowX + 32 && ty >= upArrowY && ty <= upArrowY + 32) {
                    selectedItemIdx = Math.max(0, selectedItemIdx - 1);
                    return true;
                }
            }
            int endIdx = startIdx + MAX_ITEMS_DISPLAYED + 1;
            if (endIdx < items.size()) {
                int downArrowX = position.x() + windowWidth - 42;
                int downArrowY = position.y() + 10;
                if (tx >= downArrowX && tx <= downArrowX + 32 && ty >= downArrowY && ty <= downArrowY + 32) {
                    selectedItemIdx = Math.min(items.size() - 1, selectedItemIdx + 1);
                    return true;
                }
            }
        }

        // Item rows
        int startIdx = items.size() > MAX_ITEMS_DISPLAYED
                ? Math.max(0, selectedItemIdx - MAX_ITEMS_DISPLAYED) : 0;
        int endIdx = items.size() > MAX_ITEMS_DISPLAYED
                ? startIdx + MAX_ITEMS_DISPLAYED + 1 : items.size();
        int itemBaseY = position.y() + windowHeight - 52;
        for (int i = startIdx; i < endIdx; i++) {
            int offsetMultiplier = i - startIdx;
            int itemY = itemBaseY - 42 * offsetMultiplier;
            if (tx >= position.x() + 10 && tx <= position.x() + windowWidth - 10
                    && ty >= itemY && ty <= itemY + 42) {
                if (selectedItemIdx == i) {
                    result = new InventoryWindowResult(items.get(i).entities().get(0), ItemAction.USE);
                    hide();
                } else {
                    selectedItemIdx = i;
                }
                return true;
            }
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
