package pl.lonski.dzibdzikon.screen;

import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;
import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import pl.lonski.dzibdzikon.CameraUtils;
import pl.lonski.dzibdzikon.FontUtils;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Quickbar;
import pl.lonski.dzibdzikon.entity.features.Attackable;
import pl.lonski.dzibdzikon.entity.features.MagicUser;
import pl.lonski.dzibdzikon.map.TextureId;
import pl.lonski.dzibdzikon.ui.ProgressBar;

public class Hud {

    private static final int MAX_MESSAGES = 5;
    private static final List<Message> messages = new ArrayList<>();
    private static String actionMessage = "";
    private final ProgressBar hpBar;
    private final ProgressBar mpBar;
    private static final List<Point> targets = new ArrayList<>();
    public static final List<Point> debugHighlight = new ArrayList<>();
    private final List<Quickbar.SlotIcon> quickBarIcons = new ArrayList<>();

    public Hud() {
        this.hpBar = new ProgressBar(100, 10, new Color(0x880000ff), Color.RED);
        this.mpBar = new ProgressBar(100, 10, new Color(0x000e88ff), Color.BLUE);
        messages.clear();
    }

    public static void setTargets(List<Point> newTargets) {
        targets.clear();
        targets.addAll(newTargets);
    }

    public static void addMessage(String message) {
        addMessage(message, Color.WHITE);
    }

    public static void addMessage(String message, Color color) {
        if (messages.size() > MAX_MESSAGES) {
            messages.remove(0);
        }

        messages.add(new Message(message, color, 5));
    }

    public static void setActionMessage(String message) {
        actionMessage = message;
    }

    public void update(World world) {
        messages.removeIf(message -> message.ttl <= 0);
        Attackable playerAttackable = world.getPlayer().getFeature(FeatureType.ATTACKABLE);
        hpBar.setProgress((float) playerAttackable.getHp() / playerAttackable.getMaxHp());
        MagicUser playerMagicUser = world.getPlayer().getFeature(FeatureType.MAGIC_USER);
        mpBar.setProgress((float) playerMagicUser.getMana() / playerMagicUser.getManaMax());
        quickBarIcons.clear();
        quickBarIcons.addAll(world.getPlayer().getQuickbar().getSlotIcons());
        quickBarIcons.sort(Comparator.comparingInt(Quickbar.SlotIcon::getNum));
    }

    public void render(float delta) {
        var camera = getGameResources().uiCamera;
        var batch = getGameResources().batch;

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // render messages
        var messagePos = CameraUtils.getTopLeftCorner(camera);
        for (int i = 0; i < messages.size(); i++) {
            var message = messages.get(i);
            message.ttl -= delta;
            getGameResources().fontItalic15.setColor(message.color);
            getGameResources().fontItalic15.draw(batch, message.text, messagePos.x + 10, messagePos.y - 10 - (17 * i));
        }

        if (!actionMessage.isEmpty()) {
            var bottomLeft = CameraUtils.getBottomCenter(camera);
            var textWidth = FontUtils.getTextWidth(getGameResources().fontItalic15, actionMessage);
            var actionMessagePos = new Vector2(bottomLeft.x - textWidth / 2, bottomLeft.y + 25);
            getGameResources().fontItalic15.setColor(Color.GOLD);
            getGameResources().fontItalic15.draw(batch, actionMessage, actionMessagePos.x, actionMessagePos.y);
        }

        // debug render
        if (!debugHighlight.isEmpty()) {
            var texture = getGameResources().textures.get(TextureId.HIGHLIGHT_YELLOW);
            float originX = texture.getRegionWidth() / 2f;
            float originY = texture.getRegionHeight() / 2f;
            for (Point point : debugHighlight) {
                batch.draw(texture, point.x() * TILE_WIDTH - originX, point.y() * TILE_HEIGHT - originY);
            }
        }

        // render targeting
        if (!targets.isEmpty()) {
            var texture = getGameResources().textures.get(TextureId.TARGET);
            float originX = texture.getRegionWidth() / 2f;
            float originY = texture.getRegionHeight() / 2f;
            for (Point point : targets) {
                batch.draw(texture, point.x() * TILE_WIDTH - originX, point.y() * TILE_HEIGHT - originY);
            }
        }

        // renader quickbar
        getGameResources().fontItalic12.setColor(Color.WHITE);
        var highlightTexture = getGameResources().textures.get(TextureId.HIGHLIGHT_YELLOW);
        for (int i = 0; i < quickBarIcons.size(); i++) {
            var slotIcon = quickBarIcons.get(i);
            var icon = slotIcon.getIcon();
            var bottomRight = CameraUtils.getBottomRightCorner(camera);
            var posX = bottomRight.x - TILE_WIDTH - 8;
            var posY = bottomRight.y + 8 + i * 40;
            var texture = getGameResources().textures.get(icon);
            batch.draw(texture, posX, posY);
            getGameResources().fontItalic12.draw(batch, String.valueOf(slotIcon.getNum()), posX + 4, posY + 16);

            if (slotIcon.isHighlight()) {
                batch.draw(highlightTexture, posX, posY);
            }
        }

        batch.end();

        // render hp bar
        var shapeRenderer = getGameResources().shapeRenderer;
        var bottomLeft = CameraUtils.getBottomLeftCorner(camera);
        var mpBarPos = new Vector2(bottomLeft.x + 10, bottomLeft.y + 10);
        var hpBarPos = new Vector2(mpBarPos.x, mpBarPos.y + mpBar.getHeight());
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        hpBar.render(hpBarPos, shapeRenderer);
        mpBar.render(mpBarPos, shapeRenderer);

        shapeRenderer.end();
    }

    private static class Message {

        String text;
        Color color;
        float ttl;

        public Message(String text, Color color, float ttl) {
            this.text = text;
            this.color = color;
            this.ttl = ttl;
        }
    }
}
