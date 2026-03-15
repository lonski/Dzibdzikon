package pl.lonski.dzibdzikon.map;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pl.lonski.dzibdzikon.Point;

/**
 * Tests for MapUtils A* pathfinding.
 *
 * <p>Intent: verify that the pathfinder finds valid paths when they exist, returns empty
 * when blocked, and produces paths of reasonable length.
 *
 * <p>NOTE — reference-equality break condition: pathfind() uses {@code current == end} for
 * its early-exit. Since {@code getNeighbourPositions()} creates new Point instances, the end
 * Point instance is never {@code ==} to a neighbour, so the algorithm never exits early and
 * instead continues until the frontier is exhausted. This means:
 * <ul>
 *   <li>All tests must use a bounded {@code CanEnterTile} to prevent exploring infinite space.
 *   <li>The result is still <em>correct</em> (cameFrom HashMap uses equals()), just not optimal
 *       performance-wise.
 * </ul>
 */
public class MapPathfindingTest {

    /** Open within a 20x20 grid. All tests should use this instead of unbounded OPEN. */
    private static final int GRID = 20;
    private static final MapUtils.CanEnterTile BOUNDED_OPEN =
            p -> p.x() >= 0 && p.x() < GRID && p.y() >= 0 && p.y() < GRID;

    /** Open except wall cells, bounded to GRID. */
    private static MapUtils.CanEnterTile boundedExcept(Point... walls) {
        java.util.Set<Point> wallSet = java.util.Set.of(walls);
        return p -> p.x() >= 0 && p.x() < GRID && p.y() >= 0 && p.y() < GRID && !wallSet.contains(p);
    }

    // ----- Basic connectivity -----

    @Test
    void adjacentTiles_pathHasOneStep() {
        var start = new Point(0, 0);
        var end = new Point(1, 0);

        var path = MapUtils.pathfind(start, end, BOUNDED_OPEN);

        assertEquals(1, path.size(), "Path between adjacent tiles should have exactly 1 step");
        assertEquals(end, path.get(0));
    }

    @Test
    void sameTile_pathIsEmpty() {
        var pos = new Point(3, 3);
        var path = MapUtils.pathfind(pos, pos, BOUNDED_OPEN);

        assertTrue(path.isEmpty(), "Path from a point to itself should be empty");
    }

    @Test
    void straightHorizontalPath_returnsCorrectLength() {
        var start = new Point(0, 5);
        var end = new Point(5, 5);

        var path = MapUtils.pathfind(start, end, BOUNDED_OPEN, false); // no diagonal

        assertEquals(5, path.size(), "Non-diagonal path from x=0 to x=5 should have 5 steps");
        assertEquals(end, path.get(path.size() - 1), "Last step should be the destination");
    }

    @Test
    void path_doesNotIncludeStartPosition() {
        var start = new Point(0, 0);
        var end = new Point(3, 0);

        var path = MapUtils.pathfind(start, end, BOUNDED_OPEN, false);

        assertFalse(path.contains(start), "Path should not include the start position");
    }

    @Test
    void path_endsAtDestination() {
        var start = new Point(0, 0);
        var end = new Point(4, 4);

        var path = MapUtils.pathfind(start, end, BOUNDED_OPEN);

        assertFalse(path.isEmpty());
        assertEquals(end, path.get(path.size() - 1));
    }

    // ----- Obstacle avoidance -----

    @Test
    void blockedNeighbours_returnEmptyPath() {
        // Start surrounded by impassable tiles — can't reach a distant end
        var start = new Point(5, 5);
        var end = new Point(5, 10);

        MapUtils.CanEnterTile wallsAroundStart = p ->
                !(p.x() == 4 && p.y() == 5)
                && !(p.x() == 6 && p.y() == 5)
                && !(p.x() == 5 && p.y() == 4)
                && !(p.x() == 5 && p.y() == 6)
                && p.x() >= 0 && p.x() < GRID && p.y() >= 0 && p.y() < GRID;

        var path = MapUtils.pathfind(start, end, wallsAroundStart, false);
        assertTrue(path.isEmpty(), "Should not find path when start is boxed in by walls (cardinal only)");
    }

    @Test
    void pathAroundWall_findsAlternativeRoute() {
        // Walls at x=3, y=0..7 forcing path to go around bottom
        MapUtils.CanEnterTile withWall = p -> {
            if (!BOUNDED_OPEN.canEnter(p)) return false;
            // Wall column at x=3 for y=0..7
            return !(p.x() == 3 && p.y() >= 0 && p.y() <= 7);
        };

        var start = new Point(0, 5);
        var end = new Point(6, 5);

        var path = MapUtils.pathfind(start, end, withWall, false);

        assertFalse(path.isEmpty(), "Path around a wall should exist");
        assertEquals(end, path.get(path.size() - 1), "Path should reach the destination");

        for (var p : path) {
            assertFalse(p.x() == 3 && p.y() >= 0 && p.y() <= 7,
                    "Path should not pass through wall tiles; passed through: " + p);
        }
    }

    @Test
    void totallyBlocked_returnsEmptyPath() {
        // Destination completely surrounded by walls (except diagonal paths that don't exist in
        // cardinal-only mode)
        var start = new Point(0, 0);
        var end = new Point(5, 5);

        // Block all cardinal exits from start
        MapUtils.CanEnterTile noExit = p ->
                !(p.x() == 1 && p.y() == 0)
                && !(p.x() == 0 && p.y() == 1)
                && p.x() >= 0 && p.x() < GRID && p.y() >= 0 && p.y() < GRID;

        var path = MapUtils.pathfind(start, end, noExit, false);
        assertTrue(path.isEmpty(), "Start with no exits should yield empty path in cardinal mode");
    }

    // ----- Path quality -----

    @Test
    void cardinalPath_isMinimalLength() {
        var start = new Point(0, 0);
        var end = new Point(4, 0);

        var path = MapUtils.pathfind(start, end, BOUNDED_OPEN, false);

        assertEquals(4, path.size(), "Minimal cardinal path should not waste steps");
    }

    @Test
    void diagonalPath_usesFewerStepsThanCardinal() {
        var start = new Point(0, 0);
        var end = new Point(4, 4);

        var diag = MapUtils.pathfind(start, end, BOUNDED_OPEN, true);
        var card = MapUtils.pathfind(start, end, BOUNDED_OPEN, false);

        assertTrue(diag.size() < card.size(),
                "Diagonal path should be shorter; diag=" + diag.size() + " card=" + card.size());
    }

    // ----- Neighbour generation -----

    @Test
    void getNeighbourPositions_withDiagonal_returnsEight() {
        assertEquals(8, MapUtils.getNeighbourPositions(new Point(5, 5), true).size());
    }

    @Test
    void getNeighbourPositions_withoutDiagonal_returnsFour() {
        assertEquals(4, MapUtils.getNeighbourPositions(new Point(5, 5), false).size());
    }

    @Test
    void getNeighbourPositions_containsExpectedCardinals() {
        var center = new Point(3, 3);
        var neighbours = MapUtils.getNeighbourPositions(center, false);

        assertTrue(neighbours.contains(new Point(2, 3)));
        assertTrue(neighbours.contains(new Point(4, 3)));
        assertTrue(neighbours.contains(new Point(3, 2)));
        assertTrue(neighbours.contains(new Point(3, 4)));
    }

    // ----- Distance helpers -----

    @Test
    void euclideanDistance_samePoint_isZero() {
        assertEquals(0.0, MapUtils.euclideanDistance(new Point(3, 4), new Point(3, 4)), 1e-9);
    }

    @Test
    void euclideanDistance_threeByFour_isFive() {
        assertEquals(5.0, MapUtils.euclideanDistance(new Point(0, 0), new Point(3, 4)), 1e-9);
    }

    @Test
    void manhattanDistance_computed_correctly() {
        assertEquals(7, MapUtils.manhattanDistance(new Point(1, 2), new Point(4, 6)));
    }

    @Test
    void inBounds_detectsOutOfBounds() {
        assertFalse(MapUtils.inBounds(new Point(-1, 0), 10, 10));
        assertFalse(MapUtils.inBounds(new Point(10, 0), 10, 10));
        assertTrue(MapUtils.inBounds(new Point(0, 0), 10, 10));
        assertTrue(MapUtils.inBounds(new Point(9, 9), 10, 10));
    }
}
