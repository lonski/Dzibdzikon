package pl.lonski.dzibdzikon.screen;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.ui.window.Window;

/**
 * Tests for WindowManager.toggle() and WindowManager.isVisible().
 *
 * <p>Uses a StubWindow (no LibGDX) and a real Player (no LibGDX in Player constructor).
 * DzibdziInput.listeners is a public static Set — cleared in setUp/tearDown.
 */
class WindowManagerToggleTest {

    private Player player;
    private StubWindow spellBookWnd;
    private WindowManager wm;

    @BeforeEach
    void setUp() {
        DzibdziInput.listeners.clear();
        player = new Player();
        spellBookWnd = new StubWindow();
        wm = new WindowManager(player,
                Map.of(WindowManager.WindowType.SPELL_BOOK, spellBookWnd,
                        WindowManager.WindowType.INVENTORY, new StubWindow()));
    }

    @AfterEach
    void tearDown() {
        DzibdziInput.listeners.clear();
    }

    // ── isVisible ────────────────────────────────────────────────────────────

    @Test
    void isVisible_windowHidden_returnsFalse() {
        assertFalse(wm.isVisible(WindowManager.WindowType.SPELL_BOOK));
    }

    @Test
    void isVisible_windowShown_returnsTrue() {
        spellBookWnd.show();
        assertTrue(wm.isVisible(WindowManager.WindowType.SPELL_BOOK));
    }

    // ── toggle: open path ────────────────────────────────────────────────────

    @Test
    void toggle_hiddenWindow_showsWindow() {
        wm.toggle(WindowManager.WindowType.SPELL_BOOK, w -> {});
        assertTrue(spellBookWnd.visible());
    }

    @Test
    void toggle_hiddenWindow_removesPlayerInputListener() {
        DzibdziInput.listeners.add(player.getInputListener());
        wm.toggle(WindowManager.WindowType.SPELL_BOOK, w -> {});
        assertFalse(DzibdziInput.listeners.contains(player.getInputListener()));
    }

    // ── toggle: close path ───────────────────────────────────────────────────

    @Test
    void toggle_visibleWindow_hidesWindow() {
        spellBookWnd.show();
        wm.toggle(WindowManager.WindowType.SPELL_BOOK, w -> {});
        assertFalse(spellBookWnd.visible());
    }

    @Test
    void toggle_visibleWindow_restoresPlayerInputListener() {
        // Simulate executeInWindow having removed the listener
        DzibdziInput.listeners.remove(player.getInputListener());
        spellBookWnd.show();

        wm.toggle(WindowManager.WindowType.SPELL_BOOK, w -> {});

        assertTrue(DzibdziInput.listeners.contains(player.getInputListener()));
    }

    @Test
    void toggle_visibleWindow_doesNotInvokeOnWindowCallback() {
        spellBookWnd.show();
        boolean[] called = {false};
        wm.toggle(WindowManager.WindowType.SPELL_BOOK, w -> called[0] = true);
        assertFalse(called[0], "onWindow callback must not fire when toggle-closing");
    }

    @Test
    void toggle_visibleWindow_clearsStaleOnCloseCallback() {
        spellBookWnd.show();
        // Simulate a stale onClose set by a previous executeInWindow
        boolean[] staleCallbackFired = {false};
        spellBookWnd.onClose(w -> staleCallbackFired[0] = true);

        wm.toggle(WindowManager.WindowType.SPELL_BOOK, w -> {});

        // onClose was cleared before hide(); fire it again to confirm it's a no-op
        spellBookWnd.onClose.accept(spellBookWnd);
        assertFalse(staleCallbackFired[0], "Stale onClose must be cleared on toggle-close");
    }

    // ── stub ─────────────────────────────────────────────────────────────────

    static class StubWindow implements Window {

        private boolean visible = false;
        Consumer<Window> onClose = w -> {};

        @Override public void show()  { visible = true; }
        @Override public void hide()  { visible = false; }
        @Override public boolean visible() { return visible; }
        @Override public void onClose(Consumer<Window> c) { onClose = c; }
        @Override public void render(float delta) {}
        @Override public void update(float delta) {}
        @Override public Point getPosition() { return new Point(0, 0); }
    }
}
