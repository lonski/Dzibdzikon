# Dzibdzikon — Entity & Feature System Reference

## Entity Base Class

`pl.lonski.dzibdzikon.entity.Entity`

Every game object (player, monster, item, door, staircase) is an `Entity`.

### Key Fields

| Field | Type | Description |
|-------|------|-------------|
| `features` | `Map<FeatureType, EntityFeature>` | Attached components |
| `position` | `Point` | Current tile coordinate |
| `renderPosition` | `Point` (pixels) | Interpolated pixel position for rendering |
| `animations` | `List<Animation>` | Active animations |
| `effects` | `List<Effect>` | Active status effects |
| `energy` | `float` | Current energy (≥ 1.0 = may act) |
| `speed` | `float` | Energy recharge rate per tick |
| `flying` | `boolean` | Can move over non-walkable tiles |
| `visible` | `boolean` | Rendered if true |
| `glyph` | `TextureId` | Sprite to render |
| `zLevel` | `int` | Rendering layer (higher = drawn on top) |
| `name` | `String` | Display name |

### Key Methods

| Method | Description |
|--------|-------------|
| `getFeature(FeatureType)` | Returns the feature or `null` |
| `hasFeature(FeatureType)` | True if feature present |
| `addFeature(EntityFeature)` | Attach a feature |
| `update(float delta, World)` | Per-tick update; returns next `Action` or `null` |
| `addAnimation(Animation)` | Attach an animation |
| `addEffect(Effect)` | Apply a status effect (respects stackability) |
| `getAction()` / `setAction(Action)` | Pending action management |

---

## Player Class

`pl.lonski.dzibdzikon.entity.Player extends Entity`

The player entity has input command processing on top of the base entity.

### Additional Fields

| Field | Type | Description |
|-------|------|-------------|
| `commands` | `Queue<Command>` | Pending input commands |
| `quickbar` | `List<Object>` | Slots 0–N for items or spells |
| `selectedQuickbarSlot` | `int` | Currently selected slot index |

### Pre-attached Features at Construction

- `FieldOfView` — vision
- `Attackable` — HP 20, attack 5, defence 5 (starting values)
- `MagicUser` — mana 10
- `Inventory` — empty
- `Regeneration` — slow regen
- `PlayerFeature` — marker

### Update Logic

`Player.update()` dequeues a `Command` and calls `command.execute(world)` → returns an `Action`.

---

## FeatureType Enum

`pl.lonski.dzibdzikon.entity.features.FeatureType`

| Value | Feature Class | Description |
|-------|--------------|-------------|
| `PLAYER` | `PlayerFeature` | Marks as player |
| `FOV` | `FieldOfView` | Field-of-view computation |
| `ATTACKABLE` | `Attackable` | HP/attack/defence; melee combat |
| `RANGE_ATTACKABLE` | `RangeAttackable` | Ranged attack capability |
| `AI` | `MonsterAi` (and subclasses) | Monster decision making |
| `OPENABLE` | `Openable` | Door that can be opened/closed |
| `DOWNSTAIRS` | `Downstairs` | Level exit |
| `REGENERATION` | `Regeneration` | Per-turn heal |
| `MAGIC_USER` | `MagicUser` | Spell list + mana |
| `INVENTORY` | `Inventory` | Item container |
| `PICKABLE` | `Pickable` | Can be picked up |
| `USEABLE` | `Useable` | Can be activated |
| `SPELL_EFFECT` | `SpellEffect` | Carries a spell |
| `PTAKODRZEWO` | `PtakodrzewoFeature` | Boss marker |

---

## EntityFeature Interface

```java
interface EntityFeature {
    FeatureType getType();
    default void update(float delta, World world, Entity entity) {}
}
```

Features with active per-tick behaviour override `update()`.

---

## Feature Details

### Attackable

```java
class Attackable implements EntityFeature {
    int hp, maxHp, attack, defense;
    FightResult attack(Attackable target);
    void damage(int amount);
    boolean isDead();
}
```

Combat roll: `hit = rand(0,attack) > rand(0,defense)`.

---

### MagicUser

```java
class MagicUser implements EntityFeature {
    int mana, maxMana;
    List<Spell> knownSpells;
    void learnSpell(Spell spell);
}
```

---

### FieldOfView

```java
class FieldOfView implements EntityFeature {
    void compute(Level level, Point origin);
    Set<Point> getVisible();
}
```

Called by `World.update()` after each player action.

---

### MonsterAi (and subclasses)

```java
class MonsterAi implements EntityFeature {
    Action decideAction(Entity self, World world);
}
```

Subclass hierarchy:

```
MonsterAi
├── GlazoludAi       – stronger melee, different stat scaling
├── PtakodrzewoAi    – boss: spawns birds + melee
├── BirdPlankerAi    – ranged flyer
├── RzucoptakAi      – acid thrower flyer
├── ThrowerAi        – ground ranged
└── RollingRockAi    – linear rolling boulder
```

---

### Inventory

```java
class Inventory implements EntityFeature {
    List<Entity> items;
    void addItem(Entity item, Level level);  // removes from level
    List<Entity> getItems();
}
```

---

### Regeneration

```java
class Regeneration implements EntityFeature {
    float hpRate, mpRate;
    // heals only when no enemy in FOV
}
```

---

### Openable

```java
class Openable implements EntityFeature {
    boolean open;
    void open(Level level, Entity door);   // updates tile to open door tile
    void close(Level level, Entity door);
}
```

---

### Downstairs

Marker feature. `GoDownCommand` checks for a `DOWNSTAIRS` entity at the player's tile.

---

### Pickable

Marker feature. `PickupCommand` moves the entity from `Level` into `Inventory`.

---

### Useable / Implementations

```java
interface Useable extends EntityFeature {
    void use(Entity user, Entity item, World world);
    boolean isConsumedOnUse();
}
```

| Implementation | Consumed | Effect |
|----------------|---------|--------|
| `HealingUseable` | Yes | Heal HP |
| `LearnSpellUseable` | Yes | Add spell to `MagicUser` |

---

## Entity Factory

`pl.lonski.dzibdzikon.entity.EntityFactory`

Static factory. All entity construction goes through here.

### Player

```java
EntityFactory.createPlayer(Point startPos, GameResources res)
```

Returns `Player` with full feature set.

### Monsters

| Method | Entity | AI | Notable stats |
|--------|--------|----|---------------|
| `createZombie(pos)` | Zombie | `MonsterAi` | HP 8, ATK 4, DEF 2 |
| `createGlazolud(pos)` | Glazolud | `GlazoludAi` | HP 14, ATK 6, DEF 4 |
| `createBirdPlanker(pos)` | Bird Planker | `BirdPlankerAi` | Flying, ranged |
| `createRzucoptak(pos)` | Rzucoptak | `RzucoptakAi` | Flying, acid thrower |
| `createPtakodrzewo(pos)` | Ptakodrzewo | `PtakodrzewoAi` | Boss, high stats |
| `createRollingRock(pos)` | Rolling Rock | `RollingRockAi` | Indestructible mover |

### Items

| Method | Features | Effect |
|--------|----------|--------|
| `createHealingPotion(pos)` | Pickable, Useable | +10 HP |
| `createSpellbookPage(pos, spell)` | Pickable, Useable | Learns spell |
| `createAcidPotion(pos)` | Pickable, SpellEffect | (Incomplete) |

### Environment

| Method | Type |
|--------|------|
| `createDoor(pos, open)` | Door with `Openable` |
| `createDownstairs(pos)` | Exit with `Downstairs` |

---

## Z-Level Conventions

| Z-level | Entity type |
|---------|-------------|
| 0 | Items (potions, spellbook pages) |
| 1 | Environment (doors, downstairs) |
| 2 | Monsters |
| 3 | Player |
| 10+ | Projectiles / animations |

Higher z-level entities are drawn on top of lower ones.
