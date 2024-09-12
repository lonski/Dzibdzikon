package pl.lonski.dzibdzikon.screen;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.ui.window.SpellBookWindow;
import pl.lonski.dzibdzikon.ui.window.Window;

public class WindowManager {

    private final Map<WindowType, Window> windows = new HashMap<>();
    private Player player;

    public void init(World world) {
        this.player = world.getPlayer();
        windows.put(WindowType.SPELL_BOOK, new SpellBookWindow(world.getPlayer()));
    }

    public void show(WindowType type) {
        windows.get(type).show();
        DzibdziInput.listeners.remove(player.getInputListener());
    }

    public void executeInWindow(WindowType type, Consumer<Window> action) {
        DzibdziInput.listeners.remove(player.getInputListener());
        Window wnd = windows.get(type);
        wnd.onClose(window -> {
            action.accept(window);
            DzibdziInput.listeners.add(player.getInputListener());
        });
        wnd.show();
    }

    public void update(float delta) {
        windows.values().stream().filter(Window::visible).forEach(window -> window.update(delta));
    }

    public void render(float delta) {
        windows.values().stream().filter(Window::visible).forEach(window -> window.render(delta));
    }

    public enum WindowType {
        SPELL_BOOK
    }
}
