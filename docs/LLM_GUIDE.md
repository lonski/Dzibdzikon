# Dzibdzikon — LLM Modification Guide

This document provides step-by-step patterns for the most common modifications to this codebase. Each pattern names the exact files to read/edit and the key interfaces to implement.

---

## Quick Reference: Where Things Live

| Want to change... | Look in... |
|-------------------|-----------|
| A monster's stats | `EntityFactory.java` (create* method for that monster) |
| A monster's AI behaviour | `entity/features/*Ai.java` |
| How melee combat works | `entity/features/Attackable.java`, `action/AttackAction.java` |
| Add a new spell | `spell/` package + `EntityFactory.java` (spellbook page) |
| Add a new status effect | `effect/` package + wherever the effect is applied |
| Add a new item | `entity/features/` (Useable impl) + `EntityFactory.java` |
| Map generation | `LevelFactory.java`, `map/RoomMapBuilder.java`, `map/Room.java` |
| HUD display | `screen/Hud.java` |
| Player starting stats | `entity/Player.java` constructor |
| Key bindings | `DzibdziInput.java` |
| Tile textures | `map/TextureId.java`, `GameResources.java` |

---

## Pattern 1 — Add a New Monster

### Files to create / modify

1. `entity/features/<MonsterName>Ai.java` — new AI class
2. `entity/EntityFactory.java` — new `create<MonsterName>()` method
3. `LevelFactory.java` — add spawn logic

### Step 1 — Implement the AI

```java
// entity/features/SkeletonAi.java
package pl.lonski.dzibdzikon.entity.features;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.Action;
import pl.lonski.dzibdzikon.entity.Entity;

public class SkeletonAi extends MonsterAi {
    // MonsterAi already handles: chase player when visible, wander otherwise.
    // Override decideAction() only if you need different behaviour.

    @Override
    public FeatureType getType() {
        return FeatureType.AI;
    }
}
```

For custom behaviour (e.g. only moves every other turn), override `update()` in `MonsterAi`.

### Step 2 — Register in EntityFactory

```java
// entity/EntityFactory.java — add static method
public static Entity createSkeleton(Point pos, GameResources res) {
    Entity e = new Entity();
    e.setPosition(pos);
    e.setName("Skeleton");
    e.setGlyph(TextureId.MOB_SKELETON);         // add to TextureId if needed
    e.setZLevel(2);
    e.setSpeed(1.0f);
    e.addFeature(new Attackable(12, 3, 1));      // HP, ATK, DEF
    e.addFeature(new SkeletonAi());
    return e;
}
```

### Step 3 — Add to spawn table in LevelFactory

```java
// LevelFactory.java — inside mob placement loop
// Example: 10% skeleton, 70% zombie, 20% glazolud
int roll = DzibdziRandom.nextInt(0, 100);
Entity mob;
if (roll < 70)       mob = EntityFactory.createZombie(pos);
else if (roll < 90)  mob = EntityFactory.createGlazolud(pos);
else                 mob = EntityFactory.createSkeleton(pos);
level.getEntityMap().add(mob);
```

### Step 4 — Add TextureId if needed

```java
// map/TextureId.java — add enum value
MOB_SKELETON("mob/skeleton.png"),
```

Then add `assets/mob/skeleton.png` (32×32 px).

---

## Pattern 2 — Add a New Spell

### Files to create / modify

1. `spell/<SpellName>.java` — spell implementation
2. `entity/EntityFactory.java` — `createSpellbookPage(pos, new SpellName())` call in level gen
3. `LevelFactory.java` — add page to spawn table

### Step 1 — Implement the Spell

```java
// spell/Freeze.java
package pl.lonski.dzibdzikon.spell;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.targeting.TargetingMode;
import pl.lonski.dzibdzikon.entity.Entity;

public class Freeze extends SpellBase {

    public Freeze() {
        super(5);  // mana cost
    }

    @Override public String getName() { return "Freeze"; }
    @Override public String getDescription() { return "Immobilises a target for 3 turns."; }
    @Override public TargetingMode getTargetingMode() { return TargetingMode.SINGLE_ATTACKABLE; }

    @Override
    public void cast(Entity caster, Object target, World world) {
        Entity t = (Entity) target;
        t.addEffect(new FreezeEffect(3));  // implement FreezeEffect separately
    }
}
```

For AOE spells use `TargetingMode.COORDS` and cast `target` as `Point`.

### Step 2 — Create the spellbook page in EntityFactory

```java
// Already exists as EntityFactory.createSpellbookPage(Point pos, Spell spell)
// Just call it in LevelFactory with your new spell:
EntityFactory.createSpellbookPage(pos, new Freeze())
```

### Step 3 — Spawn the page

```java
// LevelFactory.java — item placement section
level.getEntityMap().add(EntityFactory.createSpellbookPage(randomPos, new Freeze()));
```

---

## Pattern 3 — Add a New Status Effect

### Files to create

1. `effect/<EffectName>Effect.java`

### Template

```java
// effect/FreezeEffect.java
package pl.lonski.dzibdzikon.effect;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;

public class FreezeEffect implements Effect {
    private int turnsRemaining;

    public FreezeEffect(int turns) { this.turnsRemaining = turns; }

    @Override
    public void update(Entity entity, World world) {
        // Called each world tick.
        // To prevent the entity from acting, set entity.setSpeed(0) and restore on removal.
        turnsRemaining--;
        if (turnsRemaining <= 0) {
            entity.setSpeed(1.0f);  // restore normal speed
        }
    }

    @Override public boolean isDone() { return turnsRemaining <= 0; }
    @Override public boolean isStackable() { return false; }
    @Override public String getId() { return "freeze"; }
}
```

Apply via: `entity.addEffect(new FreezeEffect(3));`

---

## Pattern 4 — Add a New Item

### Files to create / modify

1. `entity/features/<ItemName>Useable.java` — what happens when used
2. `entity/EntityFactory.java` — factory method
3. `LevelFactory.java` — add to item spawn table

### Step 1 — Useable implementation

```java
// entity/features/AntidoteUseable.java
package pl.lonski.dzibdzikon.entity.features;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;

public class AntidoteUseable implements Useable {
    @Override
    public void use(Entity user, Entity item, World world) {
        // Remove all poison/burn effects from user
        user.getEffects().removeIf(e -> e.getId().equals("burn") || e.getId().equals("poison"));
        world.log(user.getName() + " uses antidote.");
    }

    @Override public boolean isConsumedOnUse() { return true; }
    @Override public FeatureType getType() { return FeatureType.USEABLE; }
}
```

### Step 2 — Factory method

```java
// entity/EntityFactory.java
public static Entity createAntidote(Point pos, GameResources res) {
    Entity e = new Entity();
    e.setPosition(pos);
    e.setName("Antidote");
    e.setGlyph(TextureId.POTION_ANTIDOTE);
    e.setZLevel(0);
    e.addFeature(new Pickable());
    e.addFeature(new AntidoteUseable());
    return e;
}
```

---

## Pattern 5 — Modify Map Generation

### Key file: `LevelFactory.java`

The level generation pipeline runs top-to-bottom in `createLevel()`. Main extension points:

```java
// After rooms are placed, before entities are spawned:
List<Room> rooms = builder.getRooms();

// Add a new special room type:
// 1. Create class extending Room in map/ package
// 2. Add to RoomType enum
// 3. Pass to RoomMapBuilder

// Change mob density:
int mobCount = DzibdziRandom.nextInt(0, 3);  // change upper bound

// Add a new spawn rule (e.g. treasure room):
Room treasureRoom = rooms.get(DzibdziRandom.nextInt(0, rooms.size()));
for (Point p : treasureRoom.getInteriorPoints()) {
    level.getEntityMap().add(EntityFactory.createGoldCoin(p));
}
```

### Adding a new room type

1. Create `map/TreasureRoom.java extends Room` — override `getInteriorPoints()` or tile carving.
2. Add `TREASURE` to `map/RoomType.java`.
3. In `RoomMapBuilder.buildRoom(RoomType type)` switch, handle the new type.
4. In `LevelFactory`, after room list is built, find the treasure room and populate it.

---

## Pattern 6 — Add a New Action

### Template

```java
// action/TeleportAction.java
package pl.lonski.dzibdzikon.action;

import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;

public class TeleportAction implements Action {
    private final Entity actor;
    private final Point destination;
    private boolean done = false;

    public TeleportAction(Entity actor, Point destination) {
        this.actor = actor;
        this.destination = destination;
    }

    @Override
    public void update(float delta, World world) {
        world.getLevel().getEntityMap().move(actor, destination);
        actor.setPosition(destination);
        actor.getRenderPosition().set(destination.toPixels());
        done = true;
    }

    @Override public boolean isDone() { return done; }
    @Override public boolean succeeded() { return done; }
}
```

Return this action from a `Command.execute()` or an AI `update()`.

---

## Pattern 7 — Add a New Command (Key Binding)

### Step 1 — Create Command class

```java
// command/TeleportCommand.java
package pl.lonski.dzibdzikon.command;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.Action;
import pl.lonski.dzibdzikon.action.TeleportAction;

public class TeleportCommand implements Command {
    @Override
    public Action execute(World world) {
        // e.g. teleport to random open tile
        Point dest = world.getLevel().getRandomOpenPoint();
        return new TeleportAction(world.getPlayer(), dest);
    }
}
```

### Step 2 — Bind key in DzibdziInput

```java
// DzibdziInput.java — in keyDown() switch:
case Input.Keys.T:
    player.addCommand(new TeleportCommand());
    return true;
```

---

## Pattern 8 — Modify Player Starting Stats

File: `entity/Player.java` constructor (or `entity/EntityFactory.java` `createPlayer()`).

```java
// Change starting HP and attack
Attackable combat = new Attackable(30, 8, 3);  // HP, ATK, DEF
player.addFeature(combat);

// Change starting mana
MagicUser magic = new MagicUser(20);  // maxMana
player.addFeature(magic);

// Add a starting spell
magic.learnSpell(new SpikeSpell());
```

---

## Pattern 9 — Add a Tile Effect

Tile effects damage or affect entities that stand on a tile each turn.

### Template

```java
// effect/tile/PoisonTileEffect.java
package pl.lonski.dzibdzikon.effect.tile;

import pl.lonski.dzibdzikon.Level;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.effect.DamageEffect;
import pl.lonski.dzibdzikon.entity.Entity;

public class PoisonTileEffect implements TileEffect {
    private final Point position;
    private int turnsLeft = 10;

    public PoisonTileEffect(Point position) { this.position = position; }

    @Override
    public void update(World world) {
        Level level = world.getLevel();
        for (Entity e : level.getEntityMap().getAt(position)) {
            e.addEffect(new DamageEffect(1));
        }
        turnsLeft--;
    }

    @Override public boolean isDone() { return turnsLeft <= 0; }
    @Override public Point getPosition() { return position; }
}
```

Add to level: `level.addTileEffect(new PoisonTileEffect(pos));`

---

## Pattern 10 — Add HUD Element

File: `screen/Hud.java`

The HUD uses a LibGDX `Stage` with `Table` layout. To add a new element:

```java
// In Hud constructor, after existing widgets:
Label myLabel = new Label("Custom info", labelStyle);
table.row();
table.add(myLabel).left().padTop(4);

// In Hud.update(World world):
myLabel.setText("Turn: " + world.getTurn());
```

For a new progress bar, use the existing `ProgressBar` widget from `ui/ProgressBar.java`.

---

## Incomplete Features (TODO in Source)

These are partially implemented and safe to complete:

### Acid Potion (`entity/EntityFactory.java`)

- Has `SpellEffect` feature carrying `AcidPuddle` spell.
- Missing: throwable modifier (should use `ThrowAction` to cast at feet).
- To complete: add `Useable` that calls `ThrowAction` targeting player's chosen direction.

### AcidPuddle Spell (`spell/AcidPuddle.java`)

- Targeting mode is `COORDS`.
- `cast()` should add `AcidTileEffect` at the target point.
- The `AcidTileEffect` class exists in `effect/tile/AcidTileEffect.java`.

---

## Coding Conventions

| Convention | Detail |
|-----------|--------|
| Package structure | Follow existing package per concern (action/animation/effect/etc.) |
| Feature registration | Always return correct `FeatureType` from `getType()` |
| Entity construction | All entity creation goes through `EntityFactory` |
| Coordinates | Always `Point` (tile coords); convert to pixels only for rendering |
| Randomness | Always use `DzibdziRandom`, never `Math.random()` |
| Energy cost | Successful actions cost 1.0 energy; failed actions refund it |
| Non-stackable effects | Return unique string from `getId()` and `false` from `isStackable()` |
| Animations | Attach to entity via `entity.addAnimation()` — auto-removed when `isDone()` |
| Messages to player | Call `world.log(String message)` for HUD message log entries |
