package pl.lonski.dzibdzikon.ui.window;

import com.badlogic.gdx.graphics.Color;
import pl.lonski.dzibdzikon.Point;

import java.util.function.Consumer;

public interface Window {

    Color BG_COLOR = new Color(186 / 255f, 176 / 255f, 155 / 255f, 1f);
    Color FRAME_COLOR = new Color(64 / 255f, 0, 64 / 255f, 1f);

    void render(float delta);

    void show();

    void hide();

    boolean visible();

    Point getPosition();

    void update(float delta);

    void onClose(Consumer<Window> consumer);
}
