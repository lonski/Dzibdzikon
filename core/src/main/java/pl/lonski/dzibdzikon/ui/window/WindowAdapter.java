package pl.lonski.dzibdzikon.ui.window;

import com.badlogic.gdx.graphics.OrthographicCamera;
import java.util.function.Consumer;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.screen.WindowManager;

public abstract class WindowAdapter implements Window, DzibdziInput.DzibdziInputListener {

    protected boolean visible = false;
    protected Point position = new Point(0, 0);
    protected Player player;
    protected OrthographicCamera camera;
    protected WindowManager windowManager;
    protected Dzibdzikon game;
    protected Consumer<Window> onClose = window -> {};

    public WindowAdapter(Dzibdzikon game, Player player) {
        this.player = player;
        this.game = game;
        this.windowManager = game.windowManager;
        this.camera = game.camera;
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
