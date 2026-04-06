# Mobile Close via Long Press — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Long-pressing on an open closeable entity on mobile triggers CloseAction on that tile; long-pressing anything else does nothing silently.

**Architecture:** Two targeted changes — `GestureHandler.longPress` gains world-coord unprojection (matching existing `tap` logic), and `CloseCommand` gains a touch-path that stores the pressed tile coordinates and uses them directly instead of `DirectionTargeter`.

**Tech Stack:** LibGDX 1.12.1, Java 17, JUnit 5

---

## Files

| File | Change |
|------|--------|
| `core/src/main/java/pl/lonski/dzibdzikon/DzibdziInput.java` | Unproject coords in `longPress`, include as `touchCoords` in broadcast |
| `core/src/main/java/pl/lonski/dzibdzikon/command/CloseCommand.java` | Add `touchTarget` field; handle long-press in `accept` / `execute` |
| `core/src/test/java/pl/lonski/dzibdzikon/command/CloseCommandTest.java` | New — unit tests for `accept` logic |

---

### Task 1: Fix `GestureHandler.longPress` to include world tile coords

**Files:**
- Modify: `core/src/main/java/pl/lonski/dzibdzikon/DzibdziInput.java:63-67`

- [ ] **Step 1: Edit the `longPress` method**

Replace:
```java
@Override
public boolean longPress(float x, float y) {
    var screenCoords = new Point(Math.round(x), Math.round(y));
    broadcast(new DzibdziKey(Input.Keys.UNKNOWN, false, null, screenCoords, true));
    return true;
}
```

With:
```java
@Override
public boolean longPress(float x, float y) {
    var screenCoords = new Point(Math.round(x), Math.round(y));
    var pos = getGameResources().camera.unproject(new Vector3(x, y, 0f));
    var coords = new Point(Math.round(pos.x / 32.f), Math.round(pos.y / 32.f));
    broadcast(new DzibdziKey(Input.Keys.UNKNOWN, false, coords, screenCoords, true));
    return true;
}
```

`Vector3` is already imported (`com.badlogic.gdx.math.Vector3`). No new imports needed.

- [ ] **Step 2: Commit**

```bash
git add core/src/main/java/pl/lonski/dzibdzikon/DzibdziInput.java
git commit -m "fix: include world tile coords in longPress broadcast"
```

---

### Task 2: Write failing test for `CloseCommand.accept`

**Files:**
- Create: `core/src/test/java/pl/lonski/dzibdzikon/command/CloseCommandTest.java`

- [ ] **Step 1: Create the test file**

```java
package pl.lonski.dzibdzikon.command;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.Input;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.lonski.dzibdzikon.DzibdziInput.DzibdziKey;
import pl.lonski.dzibdzikon.Point;

class CloseCommandTest {

    private CloseCommand cmd;

    @BeforeEach
    void setUp() {
        cmd = new CloseCommand();
    }

    // --- keyboard path ---

    @Test
    void accept_keyC_returnsTrue() {
        var key = new DzibdziKey(Input.Keys.C, false, null, null, false);
        assertTrue(cmd.accept(key));
    }

    @Test
    void accept_otherKey_returnsFalse() {
        var key = new DzibdziKey(Input.Keys.X, false, null, null, false);
        assertFalse(cmd.accept(key));
    }

    // --- long-press path ---

    @Test
    void accept_longPressWithCoords_returnsTrue() {
        var coords = new Point(3, 4);
        var key = new DzibdziKey(Input.Keys.UNKNOWN, false, coords, null, true);
        assertTrue(cmd.accept(key));
    }

    @Test
    void accept_longPressWithoutCoords_returnsFalse() {
        var key = new DzibdziKey(Input.Keys.UNKNOWN, false, null, null, true);
        assertFalse(cmd.accept(key));
    }

    @Test
    void accept_longPressWithCoords_storesTouchTarget() {
        var coords = new Point(5, 7);
        var key = new DzibdziKey(Input.Keys.UNKNOWN, false, coords, null, true);
        cmd.accept(key);
        assertEquals(coords, cmd.touchTarget);
    }

    @Test
    void accept_keyCAfterLongPress_clearsTouchTarget() {
        var coords = new Point(5, 7);
        cmd.accept(new DzibdziKey(Input.Keys.UNKNOWN, false, coords, null, true));
        cmd.accept(new DzibdziKey(Input.Keys.C, false, null, null, false));
        assertNull(cmd.touchTarget);
    }
}
```

- [ ] **Step 2: Run to verify it fails (field `touchTarget` doesn't exist yet)**

```bash
./gradlew core:test --tests "pl.lonski.dzibdzikon.command.CloseCommandTest" 2>&1 | tail -20
```

Expected: compilation failure — `touchTarget` not found on `CloseCommand`.

---

### Task 3: Implement `CloseCommand` touch support

**Files:**
- Modify: `core/src/main/java/pl/lonski/dzibdzikon/command/CloseCommand.java`

- [ ] **Step 1: Rewrite `CloseCommand`**

Replace the entire file with:

```java
package pl.lonski.dzibdzikon.command;

import com.badlogic.gdx.Input;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.CloseAction;
import pl.lonski.dzibdzikon.action.targeting.DirectionTargeter;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.Openable;

public class CloseCommand implements Command {

    Point touchTarget = null;

    @Override
    public boolean accept(DzibdziInput.DzibdziKey key) {
        if (key.longPress() && key.touchCoords() != null) {
            touchTarget = key.touchCoords();
            return true;
        }
        touchTarget = null;
        return key.keyCode() == Input.Keys.C;
    }

    @Override
    public void execute(Player player, World world) {
        if (touchTarget != null) {
            var entity = world.getCurrentLevel().getEntityAt(touchTarget, FeatureType.OPENABLE);
            if (entity != null) {
                Openable openable = entity.getFeature(FeatureType.OPENABLE);
                if (openable.opened()) {
                    player.takeAction(new CloseAction(player, touchTarget));
                }
            }
            player.getInputListener().reset();
            touchTarget = null;
            return;
        }
        player.takeAction(new DirectionTargeter(dir -> {
            var openablePos = player.getPosition().getCoords().add(dir);
            return new CloseAction(player, openablePos);
        }));
        player.getInputListener().reset();
    }
}
```

Note: `touchTarget` is package-private (no access modifier) so the test in the same package can read it directly.

- [ ] **Step 2: Run the tests**

```bash
./gradlew core:test --tests "pl.lonski.dzibdzikon.command.CloseCommandTest" 2>&1 | tail -20
```

Expected: `BUILD SUCCESSFUL`, all 6 tests pass.

- [ ] **Step 3: Run the full test suite**

```bash
./gradlew core:test 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Commit**

```bash
git add core/src/main/java/pl/lonski/dzibdzikon/command/CloseCommand.java \
        core/src/test/java/pl/lonski/dzibdzikon/command/CloseCommandTest.java
git commit -m "feat: close doors via long press on mobile"
```

---

## Self-Review

**Spec coverage:**
- ✅ `GestureHandler.longPress` now includes `touchCoords` — Task 1
- ✅ `CloseCommand.accept` returns true for long press with coords — Task 3
- ✅ Long press on open closeable → `CloseAction` created — Task 3
- ✅ Long press on non-closeable / closed → silent no-op — Task 3 (`entity == null` or `!openable.opened()` branches do nothing)
- ✅ Keyboard close path unchanged — Task 3 (else branch identical to original)

**Placeholders:** None.

**Type consistency:** `touchTarget` is `Point` throughout. `FeatureType.OPENABLE` resolves to `Openable`. `CloseAction(player, touchTarget)` matches constructor `CloseAction(Entity, Point)`.
