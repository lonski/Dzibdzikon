package pl.lonski.dzibdzikon.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import pl.lonski.dzibdzikon.CameraUtils;
import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Attackable;
import pl.lonski.dzibdzikon.ui.ProgressBar;

public class Hud {

    private static final int MAX_MESSAGES = 3;
    private static final List<Message> messages = new ArrayList<>();
    private final Dzibdzikon game;
    private final ProgressBar hpBar;

    public Hud(Dzibdzikon game) {
        this.game = game;
        this.hpBar = new ProgressBar(new Vector2(10, 10), 100, 10, new Color(0x880000ff), Color.RED);
    }

    public static void addMessage(String message, Color color) {
        if (messages.size() > MAX_MESSAGES) {
            messages.remove(0);
        }

        messages.add(new Message(message, color, 5));
    }

    public void update(World world) {
        messages.removeIf(message -> message.ttl <= 0);
        Attackable playerAttackable = world.getPlayer().getFeature(FeatureType.ATTACKABLE);
        hpBar.setProgress((float) playerAttackable.getHp() / playerAttackable.getMaxHp());
    }

    public void render(float delta) {
        game.batch.begin();

        // render messages
        var pos = CameraUtils.getTopLeftCorner(game.camera);
        for (int i = 0; i < messages.size(); i++) {
            var message = messages.get(i);
            message.ttl -= delta;
            game.font.setColor(message.color);
            game.font.draw(game.batch, message.text, pos.x + 10, pos.y - 10 - (17 * i));
        }

        game.batch.end();

        // render hp bar
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        hpBar.render(game.shapeRenderer);
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
