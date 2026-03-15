# Dzibdzikon — Architecture Overview

## Project Purpose

**Dzibdzikon** is a turn-based roguelike dungeon-crawler built on **LibGDX 1.12.1** (Java 17). The player character (Dzibdzik) explores procedurally generated dungeon levels, fights monsters, casts spells, and collects items across multiple floors.

---

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Game framework | LibGDX 1.12.1 |
| Build system | Gradle (multi-project) |
| Desktop backend | LWJGL3 |
| Mobile backend | Android |
| Random | `java.security.SecureRandom` |

---

## Project Layout

```
Dzibdzikon/
├── assets/                        # All game assets
│   ├── animation/                 # Burn sprite frames
│   ├── font/                      # DejaVu Serif (italic, bold-italic)
│   ├── mob/                       # Enemy sprites
│   ├── potion/                    # Item sprites
│   ├── spell/                     # Spell effect sprites
│   ├── terrian/                   # Terrain tiles (floor, wall)
│   └── window/                    # UI chrome
├── core/src/main/java/pl/lonski/dzibdzikon/
│   ├── action/                    # Action implementations
│   ├── animation/                 # Animation implementations
│   ├── command/                   # Input command handlers
│   ├── effect/                    # Status effect implementations
│   ├── entity/                    # Entity, Player, EntityFactory
│   │   └── features/              # Component feature implementations
│   ├── map/                       # TileGrid, Level, LevelFactory, rooms
│   ├── screen/                    # Screen implementations + HUD
│   ├── spell/                     # Spell implementations
│   └── ui/                        # Reusable UI widgets
├── android/                       # Android launcher
├── lwjgl3/                        # Desktop launcher
├── build.gradle
├── gradle.properties
└── settings.gradle
```

Base Java package: `pl.lonski.dzibdzikon`

---

## Architectural Layers

```
┌─────────────────────────────────────────────────────────┐
│  Screen Layer  (GameScreen, GameMenu, GameOver, Hud)    │
├─────────────────────────────────────────────────────────┤
│  World / Level Layer  (World, Level, LevelFactory)      │
├────────────────┬───────────────┬────────────────────────┤
│  Entity System │  Action System│  Animation System      │
│  (Entity,      │  (Action,     │  (Animation,           │
│   Features)    │   Commands)   │   Effect)              │
├────────────────┴───────────────┴────────────────────────┤
│  Map Layer  (TileGrid, Room, MapUtils, PositionUtils)   │
├─────────────────────────────────────────────────────────┤
│  LibGDX / Platform  (SpriteBatch, Camera, Input, etc.)  │
└─────────────────────────────────────────────────────────┘
```

---

## Core Design Patterns

### 1. Component-Based Entity System

Every game object is an `Entity`. Behaviour is composed from **features** (components):

```
Entity
└── Map<FeatureType, EntityFeature>  features
    ├── PLAYER          – marks this entity as the human-controlled player
    ├── FOV             – field-of-view calculation
    ├── ATTACKABLE      – HP / attack / defence stats, melee combat
    ├── RANGE_ATTACKABLE– ranged attack capability
    ├── AI              – monster AI decision making
    ├── OPENABLE        – door open/close mechanics
    ├── DOWNSTAIRS      – level-exit trigger
    ├── REGENERATION    – per-turn HP / MP healing
    ├── MAGIC_USER      – known spells list, mana pool
    ├── INVENTORY       – item container
    ├── PICKABLE        – item can be picked up
    ├── USEABLE         – item can be activated
    ├── SPELL_EFFECT    – item carries a spell (acid potion)
    └── PTAKODRZEWO     – flags the special boss entity
```

Feature retrieval: `entity.getFeature(FeatureType.ATTACKABLE)` — returns `null` if absent.

### 2. Energy-Based Turn System

Every entity has **speed** and **energy**:

```
Each world tick:
  for every entity:
    entity.energy += entity.speed   // recharge
    if energy >= 1.0 && no pending action:
      action = entity.update()      // request next action
    if action != null:
      action.update(delta)          // advance action
      if action.isDone():
        if action.succeeded(): energy -= 1.0
        else:                  // refund — entity keeps energy
```

Speed values: `1.0` = normal, `0.8` = slow, `2.0` = fast.

### 3. Action Pattern

All in-game deeds implement `Action`:

```java
interface Action {
    void update(float delta, World world);
    boolean isDone();
    boolean succeeded();
}
```

Actions can span multiple frames (e.g., smooth movement animation).
Common actions: `MoveAction`, `AttackAction`, `CastSpellAction`, `DieAction`, `OpenAction`, `GoDownAction`.

### 4. Command Pattern (Input → Action)

Player input is mediated through `Command` objects:

```
DzibdziInput → Command → Player.addCommand(cmd)
                          └→ Player.update() → Action
```

Each `Command` inspects the world context and produces an `Action` (or `null` if the move is illegal).

### 5. Interface-Driven Extensibility

All cross-cutting concerns use narrow interfaces:
- `Spell` — all spells
- `Animation` — all animations
- `Effect` — all status effects
- `EntityFeature` — all components
- `Action` — all actions
- `Command` — all input commands

---

## Constants & Configuration

Defined in `GameResources.java` and `Point.java`:

| Constant | Value | Meaning |
|----------|-------|---------|
| `TILE_SIZE` | 32 px | Tile width & height in pixels |
| Map width | 50 tiles | Default level width |
| Map height | 30 tiles | Default level height |
| UI viewport | 1200 × 720 | Fixed HUD resolution |
| Font sizes | 12, 15, 20, 32 | Available BitmapFont sizes |

Coordinate convention: `(0,0)` at top-left, `x` right, `y` down.
Pixel conversion: `pixelX = tileX * TILE_SIZE`, `pixelY = tileY * TILE_SIZE`.

---

## Rendering Pipeline

```
GameScreen.render(delta)
  1. Clear screen black
  2. Set camera projection on SpriteBatch
  3. Render TileGrid
       visible tile → full colour
       visited tile → 40 % brightness
       unseen tile  → not drawn
  4. Render tile effects (acid puddles, burn patches)
  5. Render entities sorted by z-level (ascending)
       entity at visible position → draw glyph texture
       per-entity animations    → draw on top
  6. Render HUD (Scene2D stage, fixed resolution)
       HP / MP bars
       message log
       quickbar
       targeting overlay
```

---

## Level Generation Pipeline

`LevelFactory.createLevel(world)`:

1. `RoomMapBuilder` — places rectangular, circular, and special rooms on a wall grid, carves floors, connects rooms with corridors.
2. Mob placement — 0–2 mobs per room (80 % zombie, 20 % glazolud); special rooms spawn bosses.
3. Item placement — 0–1 healing potion per room.
4. Door placement — 20–70 % of rooms receive a door (20 % of doors start open).
5. Downstairs — placed in a random room.
6. `FieldOfView` seeded and visibility computed from player start position.

---

## Field-of-View & Fog of War

`FieldOfView` (feature on the player):
- Casts rays using Bresenham's line algorithm (`Line.java`) from the player's position.
- Marks positions in `Level.visible` (currently lit).
- Merges `visible` into `Level.visited` (permanently revealed).
- Walls block sight; floor tiles pass sight.

Rendering uses `Level.isVisible(pos)` and `Level.isVisited(pos)`.

---

## Pathfinding

`MapUtils.pathfind(from, to, level, diagonal)`:
- Standard A* with Manhattan distance heuristic.
- Obstacles: walls, closed doors, and other blocking entities.
- Optional 8-directional movement.
- Used by monster AI when chasing the player.
