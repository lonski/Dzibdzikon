package pl.lonski.dzibdzikon.ui.window;

import java.util.function.Consumer;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.entity.Player;

public abstract class WindowAdapter implements Window, DzibdziInput.DzibdziInputListener {

    protected boolean visible = false;
    protected Point position = new Point(0, 0);
    protected Player player;
    protected Consumer<Window> onClose = window -> {};

    public WindowAdapter(Player player) {
        this.player = player;
    }

    @Override
    public void show() {
        visible = true;
        DzibdziInput.listeners.add(this);
    }

    @Override
    public void hide() {
        visible = false;
        DzibdziInput.listeners.remove(this);
    }

    @Override
    public boolean visible() {
        return visible;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public void onClose(Consumer<Window> consumer) {
        onClose = consumer;
    }
}
