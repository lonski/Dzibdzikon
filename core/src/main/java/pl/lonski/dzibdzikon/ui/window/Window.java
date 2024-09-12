package pl.lonski.dzibdzikon.ui.window;

import java.util.function.Consumer;
import pl.lonski.dzibdzikon.Point;

public interface Window {

    void render(float delta);

    void show();

    void hide();

    boolean visible();

    Point getPosition();

    void update(float delta);

    void onClose(Consumer<Window> consumer);
}
