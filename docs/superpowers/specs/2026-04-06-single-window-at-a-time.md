# Single Window At A Time

**Date:** 2026-04-06

## Summary

Opening any window must hide any other currently visible window. At most one window is visible at any time.

---

## Change

### `WindowManager` — add `closeAllExcept(WindowType)` private helper

```java
private void closeAllExcept(WindowType keep) {
    windows.forEach((type, wnd) -> {
        if (type != keep && wnd.visible()) {
            wnd.onClose(w -> {});  // clear stale callback — don't fire result processing
            wnd.hide();
        }
    });
}
```

Call it at the top of both `executeInWindow()` and `show()`, before any other logic:

```java
public void executeInWindow(WindowType type, Consumer<Window> onWindow) {
    closeAllExcept(type);                          // ← new
    DzibdziInput.listeners.remove(player.getInputListener());
    ...
}

public void show(WindowType type) {
    closeAllExcept(type);                          // ← new
    windows.get(type).show();
    DzibdziInput.listeners.remove(player.getInputListener());
}
```

### Listener management

When window A is open and window B is opened:
- `closeAllExcept(B)` hides A and clears A's stale `onClose` (no result processing, no listener re-add).
- `executeInWindow` then removes the player listener (it was already removed when A opened — safe to call again on a `HashSet`).
- B's `onClose` is registered to re-add the player listener when B closes.

No listener leak: the player listener is re-added exactly once, when the active window closes.

### `toggle()` close path

No change needed. `toggle()` close path only hides the target window; mutual exclusion is already maintained by the open path.

---

## Files Touched

| File | Change |
|------|--------|
| `screen/WindowManager.java` | Add `closeAllExcept()`; call it from `executeInWindow()` and `show()` |

---

## Out of Scope

- No changes to `Hud.java`, window implementations, or keyboard commands.
