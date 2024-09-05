package pl.lonski.dzibdzikon.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;
import pl.lonski.dzibdzikon.CameraUtils;
import pl.lonski.dzibdzikon.FontUtils;
import pl.lonski.dzibdzikon.World;

public class Hud {

    private static final int FONT_SIZE = 15;
    private static final int MAX_MESSAGES = 3;
    private static final List<Message> messages = new ArrayList<>();
    private final BitmapFont font;
    private final OrthographicCamera camera;

    public Hud(OrthographicCamera camera) {
        this.camera = camera;
        font = FontUtils.createFont("font/DejaVuSerif-Italic.ttf", FONT_SIZE);
    }

    public static void addMessage(String message, Color color) {
        if (messages.size() > MAX_MESSAGES) {
            messages.remove(0);
        }

        messages.add(new Message(message, color, 5));
    }

    public void update(World world) {
    }

    public void render(float delta, SpriteBatch batch) {
        // render messages
        var pos = CameraUtils.getTopLeftCorner(camera);

        for (int i = 0; i < messages.size(); i++) {
            var message = messages.get(i);
            message.ttl -= delta;
            font.setColor(message.color);
            font.draw(batch, message.text, pos.x + 10, pos.y - 10 - (FONT_SIZE + 2) * i);
        }

        messages.removeIf(message -> message.ttl <= 0);
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
