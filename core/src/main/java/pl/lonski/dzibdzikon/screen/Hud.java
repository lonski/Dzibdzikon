package pl.lonski.dzibdzikon.screen;

import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;
import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import pl.lonski.dzibdzikon.CameraUtils;
import pl.lonski.dzibdzikon.FontUtils;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Attackable;
import pl.lonski.dzibdzikon.ui.ProgressBar;

public class Hud {

    private static final int MAX_MESSAGES = 5;
    private static final List<Message> messages = new ArrayList<>();
    private static String actionMessage = "";
    private final ProgressBar hpBar;
    public static final List<Point> debugHighlight = new ArrayList<>();

    public Hud() {
        this.hpBar = new ProgressBar(100, 10, new Color(0x880000ff), Color.RED);
        messages.clear();
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
    }

    public void render(float delta) {
        var camera = getGameResources().camera;
        getGameResources().batch.begin();

        // render messages
        var messagePos = CameraUtils.getTopLeftCorner(camera);
        for (int i = 0; i < messages.size(); i++) {
            var message = messages.get(i);
            message.ttl -= delta;
            getGameResources().fontItalic15.setColor(message.color);
            getGameResources()
                    .fontItalic15
                    .draw(getGameResources().batch, message.text, messagePos.x + 10, messagePos.y - 10 - (17 * i));
        }

        if (!actionMessage.isEmpty()) {
            var bottomLeft = CameraUtils.getBottomCenter(camera);
            var textWidth = FontUtils.getTextWidth(getGameResources().fontItalic15, actionMessage);
            var actionMessagePos = new Vector2(bottomLeft.x - textWidth / 2, bottomLeft.y + 25);
            getGameResources().fontItalic15.setColor(Color.GOLD);
            getGameResources()
                    .fontItalic15
                    .draw(getGameResources().batch, actionMessage, actionMessagePos.x, actionMessagePos.y);
        }

        getGameResources().batch.end();

        // render hp bar
        var shapeRenderer = getGameResources().shapeRenderer;
        var bottomLeft = CameraUtils.getBottomLeftCorner(camera);
        var hpBarPos = new Vector2(bottomLeft.x + 10, bottomLeft.y + 10);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        hpBar.render(hpBarPos, shapeRenderer);

        // debug render
        var red = 0.05f;
        for (Point point : debugHighlight) {
            shapeRenderer.setColor(new Color(red, 0, 0, 255));
            shapeRenderer.rect(
                    (point.x() * TILE_WIDTH) - TILE_WIDTH / 4f, (point.y() * TILE_HEIGHT) - TILE_HEIGHT / 4f, 16, 16);
            red = Math.min(1.0f, red + 0.05f);
        }

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
