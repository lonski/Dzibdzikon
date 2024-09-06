package pl.lonski.dzibdzikon.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import pl.lonski.dzibdzikon.CameraUtils;
import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.FontUtils;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Attackable;
import pl.lonski.dzibdzikon.ui.ProgressBar;

public class Hud {

    private static final int MAX_MESSAGES = 3;
    private static final List<Message> messages = new ArrayList<>();
    private static String actionMessage = "";
    private final Dzibdzikon game;
    private final ProgressBar hpBar;

    public Hud(Dzibdzikon game) {
        this.game = game;

        this.hpBar = new ProgressBar(
                 100, 10, new Color(0x880000ff), Color.RED);
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
        game.batch.begin();

        // render messages
        var messagePos = CameraUtils.getTopLeftCorner(game.camera);
        for (int i = 0; i < messages.size(); i++) {
            var message = messages.get(i);
            message.ttl -= delta;
            game.font.setColor(message.color);
            game.font.draw(game.batch, message.text, messagePos.x + 10, messagePos.y - 10 - (17 * i));
        }

        if (!actionMessage.isEmpty()) {
            var bottomLeft = CameraUtils.getBottomCenter(game.camera);
            var textWidth = FontUtils.getTextWidth(game.font, actionMessage);
            var actionMessagePos = new Vector2(bottomLeft.x - textWidth / 2, bottomLeft.y + 25);
            game.font.setColor(Color.GOLD);
            game.font.draw(game.batch, actionMessage, actionMessagePos.x, actionMessagePos.y);
        }

        game.batch.end();

        // render hp bar

        var bottomLeft = CameraUtils.getBottomLeftCorner(game.camera);
        var hpBarPos = new Vector2(bottomLeft.x + 10, bottomLeft.y + 10);
        game.shapeRenderer.setProjectionMatrix(game.camera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        hpBar.render(hpBarPos, game.shapeRenderer);
        game.shapeRenderer.end();
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
