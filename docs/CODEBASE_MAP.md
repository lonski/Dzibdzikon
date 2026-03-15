# Dzibdzikon — Codebase Map

Complete file-by-file reference for every Java source file.
Base package: `pl.lonski.dzibdzikon` · Base path: `core/src/main/java/pl/lonski/dzibdzikon/`

---

## Root Package

| File | Class | Purpose |
|------|-------|---------|
| `Dzibdzikon.java` | `Dzibdzikon extends Game` | LibGDX application entry point. Creates `GameResources`, switches between screens. |
| `GameResources.java` | `GameResources` | Shared singleton: `SpriteBatch`, `OrthographicCamera`, `BitmapFont` instances, all loaded `TextureRegion` assets, viewport. |
| `World.java` | `World` | Game state. Holds current `Level` and player. Drives turn loop: energy recharge → action request → action execution → FOV update. |
| `Level.java` | `Level` | Single dungeon floor: `TileGrid`, `EntityMap`, `visible` set, `visited` set, list of `TileEffect`s. |
| `LevelFactory.java` | `LevelFactory` | Procedural level generation: room layout, mob/item/door/downstairs placement. |
| `Point.java` | `Point` (record) | Immutable 2D integer coordinate. Arithmetic helpers. Pixel ↔ tile conversion (`TILE_SIZE = 32`). |
| `DzibdziInput.java` | `DzibdziInput` | Keyboard (`InputAdapter`) + touch/pinch (`GestureHandler`) input. Converts events to `Command` instances queued on `Player`. |
| `DzibdziRandom.java` | `DzibdziRandom` | `SecureRandom` wrapper. `nextInt(min,max)` helpers used everywhere. |
| `ExplosionSimulator.java` | `ExplosionSimulator` | Raycast-based explosion spread. Returns set of `Point`s affected by a blast (stops at walls). |
| `PositionUtils.java` | `PositionUtils` | Geometric helpers: filled/hollow circles, ellipses, direction → delta mapping. |
| `CameraUtils.java` | `CameraUtils` | Camera pan/zoom helpers used by `GameScreen`. |
| `Debouncer.java` | `Debouncer` | Time-based debounce utility (prevents key-repeat flooding). |
| `FontUtils.java` | `FontUtils` | Helpers for measuring and drawing text with `BitmapFont`. |
| `ImageCode.java` | `ImageCode` | Enum or mapping of image asset paths to load into `GameResources`. |

---

## `action/` Package

| File | Purpose |
|------|---------|
| `Action.java` | Interface: `update(delta,world)`, `isDone()`, `succeeded()`. |
| `MoveAction.java` | Move entity to adjacent tile. Checks walkability; spawns `MoveAnimationAction`. |
| `MoveAnimationAction.java` | Smooth lerp of `renderPosition` from old to new tile over ~0.1 s. |
| `AttackAction.java` | Melee attack. Runs `Attackable.attack()`, applies `DamageEffect`, spawns damage text animation. Triggers `DieAction` if target HP ≤ 0. |
| `AttackAnimationAction.java` | Short swing overlay on attacker for visual feedback. |
| `RangeAttackAction.java` | Spawns `ThrowAnimation`; on completion applies melee roll to target. |
| `ThrowAction.java` | Throws a physical item entity as projectile toward target position. |
| `CastSpellAction.java` | Plays spell animation (if any) then calls `spell.cast()` and `spell.consumeResources()`. |
| `OpenAction.java` | Opens a door entity (`DoorOpenable.open()`). Costs one turn. |
| `CloseAction.java` | Closes a door entity. |
| `PickupAction.java` | Moves item from level into player `Inventory`. |
| `UseAction.java` | Calls `Useable.use()` on an item; removes item if `isConsumedOnUse()`. |
| `DieAction.java` | Removes entity from level. If entity is player → `GameOver` screen. Plays `FallAnimationAction`. |
| `FallAnimationAction.java` | Entity drops off-screen; on complete removes entity. |
| `ChainAction.java` | Runs a sequence of `Action`s one after another. |
| `CustomAction.java` | Lambda-based one-shot action (for inline logic). |
| `NoOpAction.java` | Immediate no-op; always done and succeeded (used for wait). |
| `RemoveEntityAction.java` | Removes an arbitrary entity from the level. |

### `action/targeting/` Sub-package

| File | Purpose |
|------|---------|
| `TargetingMode.java` | Enum: `SINGLE_ATTACKABLE`, `COORDS`, `DIRECTION`. |
| `TargeterFactory.java` | Creates the correct `Targeter` for a given `TargetingMode`. |
| `SingleAttackableTargeter.java` | Player selects an enemy entity; highlights valid targets. |
| `CoordsTargeter.java` | Player selects any map coordinate (cursor-based). |
| `DirectionTargeter.java` | Player presses a direction key to choose target direction. |
| `TargetConsumer.java` | Callback interface: `accept(target, world)`. Passed to spell after targeting completes. |

---

## `animation/` Package

| File | Purpose |
|------|---------|
| `Animation.java` | Interface: `update`, `render`, `isDone`, `finish`, `getOwner`. |
| `BaseAnimation.java` | Abstract base: elapsed time tracking, owner field. |
| `BurnAnimation.java` | Looping flame sprite on a burning entity. Driven by `BurnEffect`. |
| `AreaBurnAnimation.java` | Multi-tile burn overlay (for AoE burn effects). |
| `CircleExplodeAnimation.java` | Expanding ring of highlights for fireball explosion. |
| `TextFlowUpAnimation.java` | Floating text (damage numbers, "+HP") drifts upward and fades. |
| `ThrowAnimation.java` | Sprite travels from source point to target point over time. |
| `FallAnimation.java` | Entity sprite slides downward off screen. |
| `RollingAnimation.java` | Rock entity rotation animation while rolling. |
| `ChainAnimation.java` | Plays animations sequentially. |
| `ZeroifyRotationAnimation.java` | Smoothly resets entity render rotation to 0. |

---

## `command/` Package

| File | Purpose |
|------|---------|
| `Command.java` | Interface: `execute(world) → Action`. |
| `PositionChangeCommand.java` | Move or attack in given direction. Checks target: empty→Move, enemy→Attack, door→Open. |
| `WaitCommand.java` | Returns `NoOpAction` (skip turn). |
| `CloseCommand.java` | Finds adjacent door and returns `CloseAction`. |
| `GoDownCommand.java` | Checks player is on downstairs tile → `GoDownAction` (loads next level). |
| `CastSpellCommand.java` | Opens `SpellBookWindow`; on selection spawns targeter, then `CastSpellAction`. |
| `UseQuickbarCommand.java` | Activates currently selected quickbar slot (item or spell). |
| `PickupCommand.java` | Picks up item at player position. |
| `OpenInventoryCommand.java` | Opens `InventoryWindow` (no action cost). |

---

## `effect/` Package

| File | Purpose |
|------|---------|
| `Effect.java` | Interface: `update(entity,world)`, `isDone()`, `isStackable()`, `getId()`. |
| `DamageEffect.java` | Instant damage (1 tick). Not stackable. |
| `BurnEffect.java` | 5-turn burn: 2 dmg/turn. Maintains `BurnAnimation`. Not stackable (`id = "burn"`). |
| `KnockDownEffect.java` | Knocks entity prone for N turns (stuns). |

### `effect/tile/` Sub-package

| File | Purpose |
|------|---------|
| `TileEffect.java` | Interface for effects attached to map tiles rather than entities. |
| `AcidTileEffect.java` | Acid puddle on tile: damages entities that step on it each turn. |

---

## `entity/` Package

| File | Purpose |
|------|---------|
| `Entity.java` | Base class for all game objects. See [ENTITIES.md](ENTITIES.md). |
| `Player.java` | Player subclass: command queue, quickbar. |
| `EntityFactory.java` | Static factory for all entity types. |
| `FeatureType.java` | Enum of all component types. |
| `Quickbar.java` | Quickbar data + selection logic (wraps `List<Object>`). |

### `entity/features/` Sub-package

| File | Feature | Key Behaviour |
|------|---------|--------------|
| `EntityFeature.java` | — | Base interface |
| `Attackable.java` | `ATTACKABLE` | HP/ATK/DEF stats; `attack(target)` → `FightResult` |
| `RangeAttackable.java` | `RANGE_ATTACKABLE` | Ranged attack roll; used by bird AI |
| `MonsterAi.java` | `AI` | Base AI: chase or wander |
| `GlazoludAi.java` | `AI` | Stronger melee AI variant |
| `PtakodrzewoAi.java` | `AI` | Boss: melee + bird-spawn |
| `BirdPlankerAi.java` | `AI` | Flying ranged attacker |
| `RzucoptakAi.java` | `AI` | Flying acid thrower |
| `ThrowerAi.java` | `AI` | Ground ranged thrower |
| `RangeAttackerAi.java` | `AI` | Shared logic for all ranged AI |
| `RollingRockAi.java` | `AI` | Linear boulder movement |
| `RollingRockAttackable.java` | `ATTACKABLE` | Boulder: indestructible, crushes on contact |
| `FieldOfView.java` | `FOV` | Bresenham ray-cast FOV |
| `MagicUser.java` | `MAGIC_USER` | Mana + known spells list |
| `Inventory.java` | `INVENTORY` | Item container |
| `Regeneration.java` | `REGENERATION` | Per-turn HP/MP regen when safe |
| `DoorOpenable.java` | `OPENABLE` | Door open/close; updates tile texture |
| `Openable.java` | `OPENABLE` | Generic openable interface |
| `Downstairs.java` | `DOWNSTAIRS` | Marker: level exit trigger |
| `Pickable.java` | `PICKABLE` | Marker: can be picked up |
| `Useable.java` | `USEABLE` | Interface: `use(user, item, world)` |
| `HealingUseable.java` | `USEABLE` | Consume to restore HP |
| `LearnSpellUseable.java` | `USEABLE` | Consume to learn a spell |
| `SpellEffect.java` | `SPELL_EFFECT` | Carries a `Spell` reference |

---

## `map/` Package

| File | Purpose |
|------|---------|
| `TileGrid.java` | 2D array of `TextureId` tiles. Width × height. `isWalkable(pos)`, `isOpaque(pos)`. |
| `EntityMap.java` | Spatial index: `Point → List<Entity>`. `getAt(pos)`, `add`, `remove`, `move`. |
| `Room.java` | Rectangular room: position, size, interior/perimeter points, entrance detection. |
| `CircleRoom.java` | Circular/elliptical room variant. Overrides tile carving to use `PositionUtils` ellipse. |
| `PtakodrzewoRoom.java` | Fixed large rectangular boss arena. |
| `RoomType.java` | Enum: `STANDARD`, `CIRCLE`, `PTAKODRZEWO`. |
| `RoomMapBuilder.java` | Places rooms on a wall grid, carves floors, connects with corridors. |
| `Line.java` | Bresenham's line algorithm → `List<Point>`. Used by FOV and explosion rays. |
| `MapUtils.java` | A* pathfinding, neighbour generation (4-way / 8-way), distance utilities. |
| `Position.java` | Mutable rendering position (pixel coords + rotation) attached to an entity. |
| `TextureId.java` | Enum of all tile / entity texture identifiers. Maps to asset filenames. |

---

## `screen/` Package

| File | Purpose |
|------|---------|
| `DzibdzikonScreen.java` | Base screen class; holds `GameResources` reference. |
| `GameMenu.java` | Title screen with "New Game" button. |
| `GameOver.java` | Death screen with restart option. |
| `GameScreen.java` | Main gameplay screen. Renders tiles, entities, animations, delegates to `Hud`. |
| `Hud.java` | Scene2D stage overlay: HP/MP bars, message log, quickbar, action text, target highlight. |
| `WindowManager.java` | Manages modal UI windows (inventory, spellbook) — show/hide/input routing. |

---

## `spell/` Package

| File | Purpose |
|------|---------|
| `Spell.java` | Interface: `getName`, `getDescription`, `hasResources`, `consumeResources`, `getTargetingMode`, `getAnimation`, `cast`. |
| `SpellBase.java` | Abstract base: mana cost field, default `hasResources`/`consumeResources` using `MagicUser`. |
| `SpikeSpell.java` | Direct damage to single `Attackable` target. |
| `Burn.java` | Applies `BurnEffect` to single target. |
| `Fireball.java` | AOE: `ExplosionSimulator` + `CircleExplodeAnimation`; deals 4–12 dmg to all in radius 3. |
| `AcidPuddle.java` | (Partially implemented) Places `AcidTileEffect` at target coordinates. |

---

## `ui/` Package

| File | Purpose |
|------|---------|
| `Button.java` | Simple clickable button widget for Scene2D. |
| `ProgressBar.java` | HP/MP bar widget. |

### `ui/window/` Sub-package

| File | Purpose |
|------|---------|
| `Window.java` | Interface for modal windows: `render`, `handleInput`, `isOpen`. |
| `WindowAdapter.java` | No-op default implementation of `Window`. |
| `InventoryWindow.java` | Lists inventory items; handles use/drop input. |
| `SpellBookWindow.java` | Lists known spells; on selection starts targeter flow. |

---

## Platform Launchers

| File | Purpose |
|------|---------|
| `lwjgl3/Lwjgl3Launcher.java` | Desktop entry point. Creates `Lwjgl3Application(new Dzibdzikon(), config)`. |
| `lwjgl3/StartupHelper.java` | JVM restart helper for macOS ARM compatibility. |
| `android/AndroidLauncher.java` | Android entry point. Creates `AndroidApplication(new Dzibdzikon(), config)`. |

---

## Asset Index

All assets live in `assets/`:

| Directory | Contents |
|-----------|---------|
| `animation/` | Burn flame frames (`burn_0.png` … `burn_N.png`) |
| `font/` | `DejaVuSerif-Italic.ttf`, `DejaVuSerif-BoldItalic.ttf` |
| `mob/` | Enemy sprites: zombie, glazolud, birds, ptakodrzewo, rolling rock |
| `potion/` | Healing potion, acid potion sprites |
| `spell/` | Spike, fireball, burn effect sprites |
| `terrian/` | Floor, wall, door (open/closed) tile sprites (32×32 px) |
| `window/` | UI window border/background sprites |
