package pl.lonski.dzibdzikon.map;

import pl.lonski.dzibdzikon.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

public class MapUtils {

    public static Set<Point> getNeighbourPositions(Point point) {
        return getNeighbourPositions(point, true);
    }

    public static Set<Point> getNeighbourPositions(Point point, boolean includeDiagonal) {
        var neighbours = new HashSet<Point>();
        int x = point.x();
        int y = point.y();

        neighbours.add(new Point(x - 1, y));
        neighbours.add(new Point(x + 1, y));
        neighbours.add(new Point(x, y - 1));
        neighbours.add(new Point(x, y + 1));

        if (includeDiagonal) {
            neighbours.add(new Point(x - 1, y - 1));
            neighbours.add(new Point(x - 1, y + 1));
            neighbours.add(new Point(x + 1, y - 1));
            neighbours.add(new Point(x + 1, y + 1));
        }

        return neighbours;
    }

    record QueueElement(Point pos, Double priority) {}

    private static double cost(Point a, Point b) {
        if (a.x() == b.x() || a.y() == b.y()) {
            return 1.0;
        }
        return 1.5;
    }

    public static List<Point> pathfind(Point start, Point end, CanEnterTile canEnterTile) {
        return pathfind(start, end, canEnterTile, true);
    }

    public static List<Point> pathfind(Point start, Point end, CanEnterTile canEnterTile, boolean allowDiagonal) {
        var frontier = new PriorityQueue<>(Comparator.comparing(QueueElement::priority));
        frontier.add(new QueueElement(start, 0.0));

        var cameFrom = new HashMap<Point, Point>();
        cameFrom.put(start, start);
        var costSoFar = new HashMap<Point, Double>();
        costSoFar.put(start, 0.0);

        while (!frontier.isEmpty()) {
            Point current = frontier.remove().pos();
            if (current == end) {
                break;
            }
            for (var next : getNeighbourPositions(current, allowDiagonal)) {
                if (next != end && (cameFrom.containsKey(next) || !canEnterTile.canEnter(next))) {
                    continue;
                }
                var newCost = Optional.ofNullable(costSoFar.get(current)).orElse(0.0) + cost(current, next);
                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    costSoFar.put(next, newCost);
                    frontier.add(new QueueElement(next, newCost));
                    cameFrom.put(next, current);
                }
            }
        }

        List<Point> path = new ArrayList<>();
        if (!cameFrom.containsKey(end)) {
            return path; // no path can be found
        }

        var current = end;
        while (current != start) {
            path.add(current);
            current = cameFrom.get(current);
        }

        Collections.reverse(path);

        return path;
    }

    public static double euclideanDistance(Point a, Point b) {
        return Math.sqrt((a.x() - b.x()) * (a.x() - b.x()) + (a.y() - b.y()) * (a.y() - b.y()));
    }

    public static int manhattanDistance(Point a, Point b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }

    public static boolean inBounds(Point pos, int width, int height) {
        return pos.x() >= 0 && pos.x() < width && pos.y() >= 0 && pos.y() < height;
    }

    public interface CanEnterTile {
        boolean canEnter(Point position);
    }
}
