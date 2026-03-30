# Inventory & Spellbook Button Toggle + Highlight — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Pressing the inventory or spellbook HUD button when the window is already open closes it; while the window is open, the button shows a yellow highlight.

**Architecture:** Add `toggle()` and `isVisible(WindowType)` to `WindowManager`. `Hud` button handlers call `toggle()` instead of delegating to the command classes. `Hud.render()` draws the yellow highlight texture over the buttons using the same technique as quickbar highlights.

**Tech Stack:** Java 17, LibGDX 1.12.1, JUnit 5

---

### Task 1: `WindowManager` — add `toggle()` and `isVisible()`

**Files:**
- Modify: `core/src/main/java/pl/lonski/dzibdzikon/screen/WindowManager.java`
- Create: `core/src/test/java/pl/lonski/dzibdzikon/screen/WindowManagerToggleTest.java`

- [ ] **Step 1: Write the failing tests**

Create `core/src/test/java/pl/lonski/dzibdzikon/screen/WindowManagerToggleTest.java`:

```java
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
```

- [ ] **Step 2: Run tests to confirm they fail**

```bash
./gradlew :core:test --tests "pl.lonski.dzibdzikon.screen.WindowManagerToggleTest" 2>&1 | tail -20
```

Expected: compilation error — `toggle()`, `isVisible()`, and the package-private constructor do not exist yet.

- [ ] **Step 3: Add package-private constructor + `toggle()` + `isVisible()` to `WindowManager`**

Open `core/src/main/java/pl/lonski/dzibdzikon/screen/WindowManager.java`.

Add the package-private constructor immediately after the field declarations (before `init()`):

```java
/** Package-private constructor for tests — injects pre-built windows. */
WindowManager(Player player, Map<WindowType, Window> windows) {
    this.player = player;
    this.windows.putAll(windows);
}
```

Add `toggle()` and `isVisible()` after `executeInWindow()`:

```java
public void toggle(WindowType type, Consumer<Window> onWindow) {
    Window wnd = windows.get(type);
    if (wnd.visible()) {
        wnd.onClose(w -> {});                              // clear stale onClose
        wnd.hide();
        DzibdziInput.listeners.add(player.getInputListener());
    } else {
        executeInWindow(type, onWindow);
    }
}

public boolean isVisible(WindowType type) {
    return windows.get(type).visible();
}
```

The full updated class (for reference — only add the three blocks above, don't rewrite the whole file):

```java
package pl.lonski.dzibdzikon.screen;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.ui.window.InventoryWindow;
import pl.lonski.dzibdzikon.ui.window.SpellBookWindow;
import pl.lonski.dzibdzikon.ui.window.Window;

public class WindowManager {

    private final Map<WindowType, Window> windows = new HashMap<>();
    private Player player;

    /** Package-private constructor for tests — injects pre-built windows. */
    WindowManager(Player player, Map<WindowType, Window> windows) {
        this.player = player;
        this.windows.putAll(windows);
    }

    public void init(World world) { ... }            // unchanged
    public void show(WindowType type) { ... }        // unchanged
    public void executeInWindow(...) { ... }         // unchanged

    public void toggle(WindowType type, Consumer<Window> onWindow) {
        Window wnd = windows.get(type);
        if (wnd.visible()) {
            wnd.onClose(w -> {});
            wnd.hide();
            DzibdziInput.listeners.add(player.getInputListener());
        } else {
            executeInWindow(type, onWindow);
        }
    }

    public boolean isVisible(WindowType type) {
        return windows.get(type).visible();
    }

    public void update(float delta) { ... }          // unchanged
    public void render(float delta) { ... }          // unchanged
    public boolean isAnyWindowVisible() { ... }      // unchanged

    public enum WindowType { SPELL_BOOK, INVENTORY } // unchanged
}
```

- [ ] **Step 4: Run tests — confirm they pass**

```bash
./gradlew :core:test --tests "pl.lonski.dzibdzikon.screen.WindowManagerToggleTest" 2>&1 | tail -10
```

Expected output:
```
BUILD SUCCESSFUL in Xs
```

If any test fails, read the failure message and fix `toggle()` / `isVisible()` logic before continuing.

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/pl/lonski/dzibdzikon/screen/WindowManager.java \
        core/src/test/java/pl/lonski/dzibdzikon/screen/WindowManagerToggleTest.java
git commit -m "feat: add toggle() and isVisible() to WindowManager"
```

---

### Task 2: `Hud` — button handlers + highlight rendering

**Files:**
- Modify: `core/src/main/java/pl/lonski/dzibdzikon/screen/Hud.java`

No automated tests — rendering and UI interaction require the running game. Manual test instructions are at the end of this task.

- [ ] **Step 1: Update spellbook button handler in `setupBottomBar()`**

In `Hud.java`, find the spellbook button listener (around line 137–143):

```java
spellBtn.addListener(new InputListener() {
    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        new CastSpellCommand().execute(world.getPlayer(), world);
        return true;
    }
});
```

Replace it with:

```java
spellBtn.addListener(new InputListener() {
    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        var player = world.getPlayer();
        getGameResources().windowManager.toggle(
                WindowManager.WindowType.SPELL_BOOK,
                window -> ((SpellBookWindow) window).takeSelectedSpell().ifPresent(spell -> {
                    pl.lonski.dzibdzikon.action.targeting.TargetConsumer onTargetSelected =
                            target -> new pl.lonski.dzibdzikon.action.CastSpellAction(player, target, spell);
                    player.takeAction(pl.lonski.dzibdzikon.action.targeting.TargeterFactory.create(
                            player, spell.getTargetingMode(), onTargetSelected));
                }));
        player.getInputListener().reset();
        return true;
    }
});
```

Also add the missing imports at the top of `Hud.java` if not already present:

```java
import pl.lonski.dzibdzikon.action.CastSpellAction;
import pl.lonski.dzibdzikon.action.targeting.TargetConsumer;
import pl.lonski.dzibdzikon.action.targeting.TargeterFactory;
import pl.lonski.dzibdzikon.ui.window.SpellBookWindow;
```

Remove the now-unused import `import pl.lonski.dzibdzikon.command.CastSpellCommand;` if it's no longer referenced.

- [ ] **Step 2: Update inventory button handler in `setupBottomBar()`**

Find the inventory button listener (around line 156–162):

```java
invBtn.addListener(new InputListener() {
    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        new OpenInventoryCommand().execute(world.getPlayer(), world);
        return true;
    }
});
```

Replace it with:

```java
invBtn.addListener(new InputListener() {
    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        var player = world.getPlayer();
        getGameResources().windowManager.toggle(
                WindowManager.WindowType.INVENTORY,
                window -> ((InventoryWindow) window).getResult().ifPresent(result -> {
                    if (result.action() == InventoryWindow.ItemAction.USE) {
                        var useable = result.item().<pl.lonski.dzibdzikon.entity.features.Useable>getFeature(
                                pl.lonski.dzibdzikon.entity.FeatureType.USEABLE);
                        if (useable == null) {
                            Hud.addMessage(result.item().getName() + " - nie można użyć");
                            return;
                        }
                        player.takeAction(new pl.lonski.dzibdzikon.action.UseAction(player, player, result.item()));
                    }
                }));
        player.getInputListener().reset();
        return true;
    }
});
```

Also add the missing imports if not already present:

```java
import pl.lonski.dzibdzikon.action.UseAction;
import pl.lonski.dzibdzikon.ui.window.InventoryWindow;
```

Remove the now-unused import `import pl.lonski.dzibdzikon.command.OpenInventoryCommand;` if it's no longer referenced.

- [ ] **Step 3: Add highlight rendering in `render()`**

In `Hud.render()`, find the existing quickbar highlight block (around line 294–302):

```java
if (!highlightedSlots.isEmpty()) {
    var highlightTexture = getGameResources().textures.get(TextureId.HIGHLIGHT_YELLOW);
    batch.setProjectionMatrix(getCamera().combined);
    batch.begin();
    for (int i : highlightedSlots) {
        batch.draw(highlightTexture, 2, 2 + (4 - i) * 44, 44, 40);
    }
    batch.end();
}
```

Replace it with (adds the two new highlights inside the same batch block):

```java
if (!highlightedSlots.isEmpty()
        || getGameResources().windowManager.isVisible(WindowManager.WindowType.SPELL_BOOK)
        || getGameResources().windowManager.isVisible(WindowManager.WindowType.INVENTORY)) {
    var highlightTexture = getGameResources().textures.get(TextureId.HIGHLIGHT_YELLOW);
    batch.setProjectionMatrix(getCamera().combined);
    batch.begin();
    for (int i : highlightedSlots) {
        batch.draw(highlightTexture, 2, 2 + (4 - i) * 44, 44, 40);
    }
    if (getGameResources().windowManager.isVisible(WindowManager.WindowType.SPELL_BOOK)) {
        batch.draw(highlightTexture, 800 - 44 - 2, 2 + 44, 44, 40);
    }
    if (getGameResources().windowManager.isVisible(WindowManager.WindowType.INVENTORY)) {
        batch.draw(highlightTexture, 800 - 44 - 2, 2, 44, 40);
    }
    batch.end();
}
```

Coordinates explanation:
- Spellbook button position from `setupBottomBar()`: `x = 800 - 44 - 2 = 754`, `y = 2 + 44 = 46`, size `44×40`
- Inventory button position: `x = 800 - 44 - 2 = 754`, `y = 2`, size `44×40`

Add the import for `WindowManager` if not already present:

```java
import pl.lonski.dzibdzikon.screen.WindowManager;
```

- [ ] **Step 4: Build to confirm no compile errors**

```bash
./gradlew :core:compileJava 2>&1 | grep -E "error:|warning:|BUILD"
```

Expected: `BUILD SUCCESSFUL` with no errors. Fix any import or type errors before continuing.

- [ ] **Step 5: Run all tests**

```bash
./gradlew :core:test 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`. All existing tests must still pass.

- [ ] **Step 6: Manual test — toggle behavior**

Run the game: `./gradlew lwjgl3:run`

Test the spellbook button:
1. Tap the spellbook button → spellbook window opens.
2. Tap the spellbook button again → window closes immediately (no action taken).
3. Tap the spellbook button → window opens → select a spell and cast → works as before.

Test the inventory button:
1. Tap the inventory button → inventory window opens.
2. Tap the inventory button again → window closes immediately (no item used).
3. Tap the inventory button → window opens → use an item → works as before.

Confirm that Shift+Z (keyboard spellbook) and I (keyboard inventory) still work as before — they open the window and do NOT toggle it closed.

- [ ] **Step 7: Manual test — highlight**

1. Tap the spellbook button → yellow highlight appears over the spellbook button while window is open.
2. Close the window (tap button again or press Escape) → highlight disappears.
3. Same for the inventory button.
4. Confirm quickbar button highlights still work (pick up an item, assign to quickbar, verify it highlights when appropriate).

- [ ] **Step 8: Commit**

```bash
git add core/src/main/java/pl/lonski/dzibdzikon/screen/Hud.java
git commit -m "feat: inventory/spellbook buttons toggle window and show highlight"
```
