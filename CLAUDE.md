# Dzibdzikon — Claude Code Guide

## Project Overview

Turn-based roguelike dungeon-crawler built on **LibGDX 1.12.1 / Java 17**.
The player (Dzibdzik) explores procedurally generated dungeon floors, fights monsters, casts spells, and collects items.

Base package: `pl.lonski.dzibdzikon`
Source root: `core/src/main/java/pl/lonski/dzibdzikon/`

---

## Build & Run

```bash
# Desktop
./gradlew lwjgl3:run

# Build desktop JAR
./gradlew lwjgl3:jar

# Android
./gradlew android:assembleDebug
```

---

## Architecture in One Page

### Component-Based Entity System

Every game object is an `Entity` composed from **features** (components):

```
entity.getFeature(FeatureType.ATTACKABLE)  // returns null if absent
entity.hasFeature(FeatureType.AI)
entity.addFeature(new MonsterAi())
```

All feature types live in `entity/features/FeatureType.java`.
All feature classes live in `entity/features/`.

### Energy-Based Turn Loop (`World.java`)

```
each tick:
  for each entity:
    entity.energy += entity.speed          // recharge (1.0 = normal speed)
    if energy >= 1.0 && no pending action:
      action = entity.update()             // AI or player input → Action
    if action != null:
      action.update(delta)                 // advance (may span many frames)
      if action.isDone():
        if succeeded: energy -= 1.0
        // if failed: energy is kept (free retry)
```

### Key Interfaces

| Interface | Location | Implementations |
|-----------|----------|-----------------|
| `Action` | `action/Action.java` | Move, Attack, Cast, Die, Open, … |
| `Command` | `command/Command.java` | PositionChange, Wait, CastSpell, … |
| `Spell` | `spell/Spell.java` | SpikeSpell, Burn, Fireball, AcidPuddle |
| `Animation` | `animation/Animation.java` | Move, Attack, Throw, Burn, Explode, … |
| `Effect` | `effect/Effect.java` | DamageEffect, BurnEffect, KnockDownEffect |
| `EntityFeature` | `entity/features/EntityFeature.java` | All components |

---

## Where Things Live

| Want to change… | File(s) |
|-----------------|---------|
| Monster stats | `entity/EntityFactory.java` → `create*()` method |
| Monster AI behaviour | `entity/features/*Ai.java` |
| Melee combat formula | `entity/features/Attackable.java` |
| Spell logic | `spell/<SpellName>.java` |
| Status effect | `effect/<EffectName>Effect.java` |
| Item "use" behaviour | `entity/features/*Useable.java` |
| Level / map generation | `LevelFactory.java`, `map/RoomMapBuilder.java` |
| HUD display | `screen/Hud.java` |
| Player starting stats | `entity/Player.java` constructor |
| Key bindings | `DzibdziInput.java` |
| Tile textures | `map/TextureId.java` + `assets/terrian/` |
| All entity creation | `entity/EntityFactory.java` |

---

## Common Modification Patterns

### Add a monster

1. Create `entity/features/<Name>Ai.java extends MonsterAi`
2. Add `EntityFactory.create<Name>(Point, GameResources)` static method
3. Add a `TextureId` enum entry + 32×32 PNG in `assets/mob/`
4. Add to spawn table in `LevelFactory.java`

### Add a spell

1. Create `spell/<Name>.java extends SpellBase` — implement `cast()`, `getTargetingMode()`, `getName()`, `getDescription()`
2. Register via `EntityFactory.createSpellbookPage(pos, new <Name>())` in `LevelFactory`

Targeting modes: `SINGLE_ATTACKABLE` (entity target) · `COORDS` (tile target) · `DIRECTION` (direction key)

### Add a status effect

Create `effect/<Name>Effect.java implements Effect`:
- `update(entity, world)` — called each world turn
- `isDone()` — when to remove
- `isStackable()` — `false` prevents duplicate instances
- `getId()` — unique string for de-duplication

Apply: `entity.addEffect(new MyEffect())`

### Add an item

1. Create `entity/features/<Name>Useable.java implements Useable` — implement `use()` and `isConsumedOnUse()`
2. Add `EntityFactory.create<Name>(Point)` combining `Pickable` + your `Useable`
3. Spawn in `LevelFactory`

### Add a key binding

1. Create `command/<Name>Command.java implements Command` — `execute(world) → Action`
2. In `DzibdziInput.keyDown()` switch: `case Input.Keys.X: player.addCommand(new NameCommand()); return true;`

---

## Coding Conventions

| Rule | Detail |
|------|--------|
| All entity creation | Through `EntityFactory` only — never construct entities inline |
| Coordinates | Always `Point` (tile coords); pixels only in rendering code |
| Randomness | Always `DzibdziRandom`, never `Math.random()` or `new Random()` |
| Energy cost | Successful actions cost `1.0`; failed actions refund energy automatically |
| Non-stackable effects | Return `false` from `isStackable()` and a unique string from `getId()` |
| Animations | Attach via `entity.addAnimation(anim)` — auto-removed when `isDone()` |
| Player messages | `world.log(String)` → appears in HUD message log |
| Feature lookup | `entity.getFeature(FeatureType.X)` returns `null` if absent — always null-check |
| Tile size | 32 px — use `Point.toPixels()` / `Point.fromPixels()` for conversions |

---

## Incomplete / TODO Features

| Feature | Status | Files |
|---------|--------|-------|
| Acid Potion | Partially done — missing throwable + useable logic | `EntityFactory.java`, `spell/AcidPuddle.java` |
| Acid Puddle Spell | `cast()` needs to call `level.addTileEffect(new AcidTileEffect(pos))` | `spell/AcidPuddle.java`, `effect/tile/AcidTileEffect.java` |

---

## Full Documentation

Detailed reference docs are in `docs/`:

| File | Contents |
|------|---------|
| `docs/ARCHITECTURE.md` | Layer diagram, design patterns, render pipeline, FOV, pathfinding |
| `docs/SYSTEMS.md` | Every game system in depth (combat, spells, AI, effects, input, map gen) |
| `docs/ENTITIES.md` | Entity fields/methods, all features with APIs, EntityFactory table |
| `docs/CODEBASE_MAP.md` | Purpose of every Java source file |
| `docs/LLM_GUIDE.md` | Step-by-step code templates for 10 common modifications |
