# Dzibdzikon — Game Systems Reference

## 1. Combat System

### Melee Combat

Feature: `Attackable` (`FeatureType.ATTACKABLE`)

Fields:
- `int hp` — current hit points
- `int maxHp` — maximum hit points
- `int attack` — attack power
- `int defense` — defence rating

Attack resolution (`Attackable.attack(target)`):
```
hit = random(0, attacker.attack) > random(0, target.defense)
damage = hit ? random(1, attacker.attack) : 0
```
Returns `FightResult(hit, damage)`.

If `target.hp <= 0` after damage → `DieAction` is queued on target.

### Ranged Combat

Feature: `RangeAttackable` (`FeatureType.RANGE_ATTACKABLE`)

Used by bird monsters. Queues a `RangeAttackAction` that:
1. Spawns a `ThrowAnimation` (projectile flying to target).
2. On animation completion, applies damage via `Attackable.attack()` on the target.

### Death

`DieAction`:
- Removes entity from level.
- If entity is the player → triggers `GameOver` screen.
- Plays a `FallAnimationAction` before removal.

---

## 2. Spell System

### Spell Interface

```java
interface Spell {
    String getName();
    String getDescription();
    boolean hasResources(Entity caster);   // MP check
    void consumeResources(Entity caster);  // deduct MP
    TargetingMode getTargetingMode();
    Optional<Animation> getAnimation(Entity caster, Object target);
    void cast(Entity caster, Object target, World world);
}
```

Base class `SpellBase` handles MP boilerplate.

### Targeting Modes

| `TargetingMode` | Target type passed to `cast()` | Usage |
|-----------------|-------------------------------|-------|
| `SINGLE_ATTACKABLE` | `Entity` | Spike, Burn |
| `COORDS` | `Point` | Fireball |
| `DIRECTION` | `Point` (direction delta) | Directional spells |

### Implemented Spells

| Class | Name | MP | Effect |
|-------|------|----|--------|
| `SpikeSpell` | Spike | 3 | Direct damage to single target |
| `BurnSpell` | Burn | 4 | Apply `BurnEffect` (5 turns, 2 dmg/turn) to single target |
| `FireballSpell` | Fireball | 8 | `ExplosionSimulator` raycasts; 4–12 dmg to all in radius 3 |
| `AcidPuddleSpell` | Acid Puddle | — | Tile hazard (partially implemented) |

### Spell Acquisition

- `LearnSpellUseable`: using a spellbook page item adds a `Spell` instance to `MagicUser.knownSpells`.
- Player starts with no spells; all must be found in the dungeon.

### Casting Flow

```
CastSpellCommand → open SpellBook UI
  Player selects spell
  TargeterFactory creates Targeter for that TargetingMode
  Player selects target (keyboard or touch)
  CastSpellAction queued:
    play spell animation (if any)
    spell.cast(caster, target, world)
    spell.consumeResources(caster)
```

---

## 3. AI System

### Base AI (`MonsterAi`)

Decision tree per turn:
1. Is the player within FOV range and adjacent? → `AttackAction(player)`
2. Is the player within FOV range and not adjacent? → `MoveAction` along A* path toward player
3. Otherwise → `MoveAction` to random walkable neighbour (wander)

### Specialised AI Classes

| Class | Description |
|-------|-------------|
| `MonsterAi` | Base: melee chase |
| `GlazoludAi` | Stronger melee variant (uses melee range check) |
| `PtakodrzewoAi` | Boss: spawns birds each turn, also melee attacks |
| `BirdPlankerAi` | Flying: ranged attack if player in line of sight |
| `RzucoptakAi` | Flying: throws acid projectiles |
| `ThrowerAi` | Ground-based thrower |
| `RollingRockAi` | Boulder: moves in one direction until wall, crushes entities |

All AI classes implement `EntityFeature` and return an `Action` from `update()`.

### Field of View Used by AI

AI checks `world.isVisible(position)` / line-of-sight before deciding whether the player is "seen". Monsters do not act when they cannot see the player (they wander).

---

## 4. Effect System

### Effect Interface

```java
interface Effect {
    void update(Entity entity, World world); // called each world turn
    boolean isDone();
    boolean isStackable();  // false = only one instance per entity
    String getId();         // used for de-duplication
}
```

### Implemented Effects

| Class | Stackable | Duration | Behaviour |
|-------|-----------|----------|-----------|
| `DamageEffect` | No | Instant (1 tick) | Apply fixed damage immediately |
| `BurnEffect` | No | 5 turns | 2 damage/turn, maintains `BurnAnimation` on entity |

Effect application: `entity.addEffect(effect)` — if not stackable and same ID already present, existing effect is replaced.

---

## 5. Animation System

### Animation Interface

```java
interface Animation {
    void update(float delta, World world);
    void render(SpriteBatch batch);
    boolean isDone();
    void finish();      // force-complete (e.g. on level change)
    Object getOwner();  // entity or null; used for removal when owner dies
}
```

Animations are attached to entities (`entity.addAnimation(anim)`) or to the world. They are rendered in `GameScreen` after entities.

### Implemented Animations

| Class | Description |
|-------|-------------|
| `MoveAnimationAction` | Smooth lerp from old tile to new tile position |
| `AttackAnimationAction` | Short directional swing overlay |
| `FallAnimationAction` | Entity drops off screen (on death) |
| `ThrowAnimation` | Projectile sprite travels from source to target |
| `BurnAnimation` | Looping flame sprite on burning entity |
| `CircleExplodeAnimation` | Expanding circle of tiles lit up (fireball) |
| `TextFlowUpAnimation` | Floating number (damage / heal) drifts upward |
| `ChainAnimation` | Runs a list of animations sequentially |
| `AreaBurnAnimation` | Multi-tile burn effect overlay |

---

## 6. Inventory & Item System

### Inventory Feature

`Inventory` (`FeatureType.INVENTORY`) stores a `List<Entity>` of held items.

Methods:
- `addItem(Entity item)` — adds item, removes it from the level
- `getItems()` — returns item list (read-only view)

### Item Features

An item entity typically has one or more of:

| Feature | Class | Purpose |
|---------|-------|---------|
| `PICKABLE` | `Pickable` | Allows player to pick it up via `PickupCommand` |
| `USEABLE` | `Useable` | Allows activation via inventory or quickbar |
| `SPELL_EFFECT` | `SpellEffect` | Carries a spell reference (e.g. acid potion) |

### Useable Implementations

| Class | Effect |
|-------|--------|
| `HealingUseable` | Restores HP to player (amount configurable) |
| `LearnSpellUseable` | Adds a spell to `MagicUser.knownSpells`, consumes item |

### Quickbar

`Player` maintains a quickbar (`List<Object>`) holding items or spells. `UseQuickbarCommand` activates the selected quickbar slot.

---

## 7. Field-of-View System

Feature: `FieldOfView` (`FeatureType.FOV`)

Algorithm:
1. Clear `level.visible` set.
2. Cast 360 rays using `Line.bresenham(playerPos, edgePos)`.
3. For each ray: mark each position along it as visible until a wall tile is hit.
4. Merge `visible` into `visited` (permanent memory).

Called once per turn in `World.update()` after the player's action completes.

View radius: depends on room and level configuration (typically the entire room plus corridors).

---

## 8. Regeneration System

Feature: `Regeneration` (`FeatureType.REGENERATION`)

Behaviour (called in `update()` each world tick):
- If no enemy is currently visible in FOV → heal HP and MP by configured rate.
- Plays `TextFlowUpAnimation` ("+" value) when healing occurs.
- Regen stops while any enemy is in line of sight.

---

## 9. Input & Command System

### Input Handler

`DzibdziInput` implements both `InputAdapter` (keyboard) and `GestureHandler` (touch/pinch):

Key bindings:

| Key(s) | Action |
|--------|--------|
| Arrow keys / numpad 1-9 | Move / attack in 8 directions |
| `.` / numpad 5 | Wait (skip turn) |
| `c` | Close adjacent door |
| `>` | Go downstairs |
| `z` | Open spell book |
| `q` | Use quickbar item |
| `,` | Pick up item |
| `i` | Open inventory |
| Numpad 9/0 | Zoom in/out |
| Pinch gesture | Zoom |

Shift modifier: held via `DzibdziInput.shiftDown` flag; used for directional modifiers.

### Command → Action Mapping

| Command class | Action produced |
|---------------|-----------------|
| `PositionChangeCommand` | `MoveAction`, `AttackAction`, or `OpenAction` depending on target tile |
| `WaitCommand` | `WaitAction` |
| `CloseCommand` | `CloseAction` |
| `GoDownCommand` | `GoDownAction` |
| `CastSpellCommand` | Opens spell selection UI → `CastSpellAction` |
| `UseQuickbarCommand` | `UseAction` |
| `PickupCommand` | `PickupAction` |
| `OpenInventoryCommand` | Opens inventory UI (no action cost) |

---

## 10. Map Generation

### Room Types

| Class | Shape | Notes |
|-------|-------|-------|
| `Room` | Rectangle | Standard room |
| `CircleRoom` | Circle/ellipse | Rounded room |
| `PtakodrzewoRoom` | Large rectangle | Boss arena, fixed layout |

### Builder

`RoomMapBuilder`:
1. Randomly places rooms (no overlap check — rooms are carved into the wall grid).
2. Connects adjacent rooms with straight corridors.
3. Returns populated `TileGrid`.

### Mob Spawn Weights (per standard room)

| Entity | Weight |
|--------|--------|
| Zombie | 80 % |
| Glazolud | 20 % |

Boss rooms always spawn the `Ptakodrzewo` boss entity.

### Tile Types

Defined in `TextureId` enum (or equivalent):

| Tile | Walkable | Blocks sight |
|------|----------|-------------|
| Floor | Yes | No |
| Wall | No | Yes |
| Door (closed) | No (blocks) | Yes |
| Door (open) | Yes | No |

---

## 11. Screen Management

Screens implement LibGDX `Screen`:

| Class | Displayed when |
|-------|---------------|
| `GameMenu` | Application start |
| `GameScreen` | Active gameplay |
| `GameOver` | Player dies |

Switching: `Dzibdzikon.setScreen(new GameScreen(resources))`.

`GameResources` is a shared singleton holding `SpriteBatch`, `OrthographicCamera`, `BitmapFont` instances, and all loaded `TextureRegion` assets. It is passed to every screen.
