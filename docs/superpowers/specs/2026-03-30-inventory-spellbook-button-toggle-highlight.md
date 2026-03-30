# Inventory & Spellbook Button Toggle + Highlight

**Date:** 2026-03-30

## Summary

When the player taps the inventory or spellbook button in the HUD:
- If the window is **closed**, open it (existing behaviour).
- If the window is **open**, close it.

Additionally, the button should show a yellow highlight overlay (same as quickbar buttons) while its window is open.

---

## Changes

### 1. `WindowManager` additions

**`toggle(WindowType, Consumer<Window>)`** — the single entry point for button presses:

```java
public void toggle(WindowType type, Consumer<Window> onWindow) {
    Window wnd = windows.get(type);
    if (wnd.visible()) {
        wnd.onClose(w -> {});   // clear stale onClose set by executeInWindow
        wnd.hide();
        DzibdziInput.listeners.add(player.getInputListener());
    } else {
        executeInWindow(type, onWindow);
    }
}
```

When closing via toggle:
- Clear the stale `onClose` first (it was set by the earlier `executeInWindow` call and would otherwise try to process a result when none was selected).
- Call `hide()` directly.
- Re-add the player input listener, which `executeInWindow` removed when it opened the window.

`WindowAdapter.close()` is not needed and will not be added.

**`isVisible(WindowType)`** — used by `Hud` for highlight logic:

```java
public boolean isVisible(WindowType type) {
    return windows.get(type).visible();
}
```

---

### 3. `Hud.java`

**Button handlers** — replace `executeInWindow` calls with `toggle`:

```java
// Spellbook button touchDown
getGameResources().windowManager.toggle(WindowManager.WindowType.SPELL_BOOK,
    window -> ((SpellBookWindow) window).takeSelectedSpell()
        .ifPresent(spell -> castSpell(player, spell)));
player.getInputListener().reset();

// Inventory button touchDown
getGameResources().windowManager.toggle(WindowManager.WindowType.INVENTORY,
    window -> ((InventoryWindow) window).getResult()
        .ifPresent(r -> handleWindowResult(r, player)));
player.getInputListener().reset();
```

**Highlight rendering** — appended to the existing quickbar highlight block in `render()`, using the same `highlightTexture` and batch projection:

```java
var wm = getGameResources().windowManager;
if (wm.isVisible(WindowManager.WindowType.SPELL_BOOK)) {
    batch.draw(highlightTexture, 800 - 44 - 2, 2 + 44, 44, 40);
}
if (wm.isVisible(WindowManager.WindowType.INVENTORY)) {
    batch.draw(highlightTexture, 800 - 44 - 2, 2, 44, 40);
}
```

Button positions match those set in `setupBottomBar()`: spellbook at `(754, 46)` and inventory at `(754, 2)`, both `44×40`.

---

## Files Touched

| File | Change |
|------|--------|
| `screen/WindowManager.java` | Add `toggle()` and `isVisible()` |
| `screen/Hud.java` | Update button listeners; add highlight render logic |

No new files. No changes to `WindowAdapter` or the `Window` interface.

---

## Out of Scope

- No changes to keyboard shortcuts (`CastSpellCommand`, `OpenInventoryCommand`) — those remain as-is.
- No changes to window internals (`InventoryWindow`, `SpellBookWindow`).
