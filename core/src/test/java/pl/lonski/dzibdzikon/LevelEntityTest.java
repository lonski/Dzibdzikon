package pl.lonski.dzibdzikon;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.lonski.dzibdzikon.effect.tile.TileEffect;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.map.TextureId;
import pl.lonski.dzibdzikon.map.TileGrid;

/**
 * Tests for Level entity tracking and tile effect management.
 *
 * <p>Intent: verify that the spatial index (EntityMap) correctly tracks entity positions
 * and that tile effects are stored and retrieved correctly.
 */
public class LevelEntityTest {

    private Level level;

    @BeforeEach
    void setUp() {
        var grid = new TileGrid(10, 10);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                grid.setTile(x, y, TextureId.FLOOR);
            }
        }
        level = new Level(grid);
    }

    private static Entity makeEntity(String name) {
        return new Entity(name, TextureId.MOB_ZOMBIE);
    }

    // ----- addEntity / getEntityAt -----

    @Test
    void addEntity_makesEntityRetrievable() {
        var entity = makeEntity("zombie");
        var pos = new Point(3, 4);
        level.addEntity(entity, pos);

        var found = level.getEntityAt(pos, null);
        assertSame(entity, found, "Entity should be retrievable at the position it was added");
    }

    @Test
    void addEntity_setsEntityPosition() {
        var entity = makeEntity("zombie");
        var pos = new Point(2, 5);
        level.addEntity(entity, pos);

        assertEquals(pos, entity.getPosition().getCoords(), "Entity position should be set to the added coords");
    }

    @Test
    void getEntityAt_emptyTile_returnsNull() {
        assertNull(level.getEntityAt(new Point(0, 0), null), "Empty tile should return null");
    }

    @Test
    void getEntityAt_withFeatureFilter_returnsOnlyMatchingFeature() {
        var entity = makeEntity("item");
        var pos = new Point(1, 1);
        level.addEntity(entity, pos);

        // Entity has no ATTACKABLE feature
        assertNull(level.getEntityAt(pos, FeatureType.ATTACKABLE), "Should not return entity missing the requested feature");
        assertSame(entity, level.getEntityAt(pos, null), "Null feature filter should return any entity");
    }

    // ----- removeEntity -----

    @Test
    void removeEntity_makesEntityNoLongerRetrievable() {
        var entity = makeEntity("zombie");
        var pos = new Point(5, 5);
        level.addEntity(entity, pos);
        level.removeEntity(entity);

        assertNull(level.getEntityAt(pos, null), "Removed entity should not be retrievable");
    }

    @Test
    void removeEntity_appearsInNeitherListNorTile() {
        var entity = makeEntity("zombie");
        var pos = new Point(6, 6);
        level.addEntity(entity, pos);
        level.removeEntity(entity);

        assertFalse(level.getEntities().contains(entity), "Removed entity should not appear in entities list");
    }

    @Test
    void removeNonExistentEntity_doesNotThrow() {
        var entity = makeEntity("ghost");
        assertDoesNotThrow(() -> level.removeEntity(entity));
    }

    // ----- moveEntity -----

    @Test
    void moveEntity_updatesPosition() {
        var entity = makeEntity("zombie");
        var start = new Point(1, 1);
        var dest = new Point(4, 4);
        level.addEntity(entity, start);
        level.moveEntity(entity, dest);

        assertEquals(dest, entity.getPosition().getCoords(), "Entity position should update after move");
    }

    @Test
    void moveEntity_entityRetrievableAtNewPosition() {
        var entity = makeEntity("zombie");
        var start = new Point(1, 1);
        var dest = new Point(4, 4);
        level.addEntity(entity, start);
        level.moveEntity(entity, dest);

        assertSame(entity, level.getEntityAt(dest, null), "Entity should be retrievable at new position");
    }

    @Test
    void moveEntity_removedFromOldPosition() {
        var entity = makeEntity("zombie");
        var start = new Point(1, 1);
        var dest = new Point(4, 4);
        level.addEntity(entity, start);
        level.moveEntity(entity, dest);

        assertNull(level.getEntityAt(start, null), "Old position should be empty after move");
    }

    @Test
    void moveNonAddedEntity_doesNotThrow() {
        var entity = makeEntity("ghost");
        assertDoesNotThrow(() -> level.moveEntity(entity, new Point(2, 2)));
    }

    // ----- Multiple entities -----

    @Test
    void twoEntities_canExistAtDifferentPositions() {
        var e1 = makeEntity("zombie1");
        var e2 = makeEntity("zombie2");
        level.addEntity(e1, new Point(1, 1));
        level.addEntity(e2, new Point(2, 2));

        assertSame(e1, level.getEntityAt(new Point(1, 1), null));
        assertSame(e2, level.getEntityAt(new Point(2, 2), null));
    }

    @Test
    void getEntities_containsAllAddedEntities() {
        var e1 = makeEntity("a");
        var e2 = makeEntity("b");
        level.addEntity(e1, new Point(0, 0));
        level.addEntity(e2, new Point(1, 0));

        var entities = level.getEntities();
        assertTrue(entities.contains(e1));
        assertTrue(entities.contains(e2));
    }

    // ----- Tile effects -----

    @Test
    void addTileEffect_makesEffectRetrievable() {
        var pos = new Point(3, 3);
        var effect = dummyTileEffect();
        level.addTileEffect(pos, effect);

        var effects = level.getTileEffects().get(pos);
        assertNotNull(effects, "Tile effect list should not be null after adding");
        assertTrue(effects.contains(effect), "Added tile effect should be retrievable");
    }

    @Test
    void addTileEffect_multipleSamePosition_allStored() {
        var pos = new Point(2, 2);
        var e1 = dummyTileEffect();
        var e2 = dummyTileEffect();
        level.addTileEffect(pos, e1);
        level.addTileEffect(pos, e2);

        var effects = level.getTileEffects().get(pos);
        assertEquals(2, effects.size(), "Both tile effects should be stored at the same position");
    }

    @Test
    void noTileEffect_positionNotInMap() {
        var pos = new Point(9, 9);
        var effects = level.getTileEffects().getOrDefault(pos, java.util.List.of());
        assertTrue(effects.isEmpty(), "Position without tile effect should return empty list");
    }

    // ----- Obstacle checking -----

    @Test
    void isObstacle_wallTile_returnsTrue() {
        var grid = new TileGrid(5, 5);
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                grid.setTile(x, y, x == 2 ? TextureId.WALL : TextureId.FLOOR);
            }
        }
        var wallLevel = new Level(grid);

        assertTrue(wallLevel.isObstacle(new Point(2, 2)), "Wall tile should be an obstacle");
        assertFalse(wallLevel.isObstacle(new Point(1, 2)), "Floor tile should not be an obstacle");
    }

    @Test
    void isObstacle_outOfBounds_returnsTrue() {
        assertTrue(level.isObstacle(new Point(-1, 0)), "Out-of-bounds should be treated as obstacle");
        assertTrue(level.isObstacle(new Point(100, 100)), "Out-of-bounds should be treated as obstacle");
    }

    // ----- Helpers -----

    private static TileEffect dummyTileEffect() {
        return new TileEffect() {
            @Override
            public void render(Point pos) {}

            @Override
            public boolean takeTurn(Point pos, World world) {
                return false;
            }
        };
    }
}
