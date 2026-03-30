# Mobile Close via Long Press — Design Spec

**Date:** 2026-03-30

## Problem

The `CloseAction` exists and is accessible on keyboard via the `C` key + direction prompt.
On mobile there is no way to trigger it: the `GestureHandler.longPress` callback fires but
broadcasts a `DzibdziKey` with `touchCoords=null`, so no command can identify the target tile,
and no command currently accepts `longPress=true`.

## Goal

Long-pressing on an adjacent open closeable entity (e.g. an open door) closes it.
Long-pressing on anything else (non-closeable, already closed, out of range) does nothing silently.

## Changes

### 1. `DzibdziInput.GestureHandler.longPress`

Unproject screen → world tile coords (identical logic to `tap`), and include them as `touchCoords`
in the broadcast. The `longPress` flag remains `true`.

Before:
```java
broadcast(new DzibdziKey(Input.Keys.UNKNOWN, false, null, screenCoords, true));
```

After:
```java
var pos = getGameResources().camera.unproject(new Vector3(x, y, 0f));
var coords = new Point(Math.round(pos.x / 32.f), Math.round(pos.y / 32.f));
broadcast(new DzibdziKey(Input.Keys.UNKNOWN, false, coords, screenCoords, true));
```

### 2. `CloseCommand`

Add nullable `Point touchTarget` field.

**`accept`:**
- If `key.longPress() && key.touchCoords() != null`: store `touchTarget = key.touchCoords()`, return `true`.
- Otherwise: clear `touchTarget = null`, return `key.keyCode() == Input.Keys.C` (existing keyboard path).

**`execute`:**
- If `touchTarget != null`:
  - Look up the entity at `touchTarget` in the current level.
  - If it has an `OPENABLE` feature and `openable.opened()` is true → create `CloseAction(player, touchTarget)`.
  - Otherwise → do nothing (silent failure).
  - Clear `touchTarget`, call `player.getInputListener().reset()`.
- If `touchTarget == null`: existing `DirectionTargeter` keyboard path unchanged.

## Constraints

- No new files.
- No changes to `CloseAction` — it remains unaware of how it was triggered.
- Silent failure on touch (no HUD messages for non-closeable / already-closed targets).
- Keyboard close path unchanged.
