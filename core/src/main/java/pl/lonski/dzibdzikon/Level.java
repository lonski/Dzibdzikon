package pl.lonski.dzibdzikon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.map.Glyph;
import pl.lonski.dzibdzikon.map.TileGrid;

public class Level {

    private final TileGrid map;
    private final List<Entity> entities = new ArrayList<>();
    private final Set<Point> visited = new HashSet<>();
    private final Set<Point> visible = new HashSet<>();

    public Level(TileGrid map) {
        this.map = map;
    }

    public void addEntity(Entity entity) {
        this.entities.add(entity);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public TileGrid getMap() {
        return map;
    }

    public Set<Point> getVisited() {
        return visited;
    }

    public Set<Point> getVisible() {
        return visible;
    }

    public boolean isObstacle(Point pos) {
        return isObstacle(pos, true);
    }

    public boolean isObstacle(Point pos, boolean includeMonsters) {
        if (!map.inBounds(pos.x(), pos.y())) {
            return true;
        }

        if (map.getTile(pos) == Glyph.WALL) {
            return true;
        }

        if (includeMonsters && getEntityAt(pos, FeatureType.ATTACKABLE).isPresent()) {
            return true;
        }

        // openables
//        if (getEntityAt(pos, FeatureType.OPENABLE)
//            .map(o -> o.<Openable>getFeature(FeatureType.OPENABLE).obstacle())
//            .orElse(false)) {
//            return true;
//        }

        return false;
    }

    public boolean isOpaque(Point pos) {
        if (!map.inBounds(pos)) {
            return true;
        }

        // openables
//        if (getEntityAt(pos, FeatureType.OPENABLE)
//            .map(o -> o.<Openable>getFeature(FeatureType.OPENABLE).opaque())
//            .orElse(false)) {
//            return true;
//        }

        return map.getTile(pos) == Glyph.WALL;
    }

    public Point getRandomFreePosition() {
        while (true) {
            Point pos = new Point(Dzibdzikon.RANDOM.nextInt(map.getWidth()), Dzibdzikon.RANDOM.nextInt(map.getHeight()));
            if (map.getTile(pos) == Glyph.FLOOR && getEntitiesAt(pos, FeatureType.POSITION).isEmpty()) {
                return pos;
            }
        }
    }

    public Optional<Entity> getEntityAt(Point pos, FeatureType featureType) {
        return getEntitiesAt(pos, featureType).stream().findFirst();
    }

    public List<Entity> getEntitiesAt(Point targetPos, FeatureType featureType) {
        return getEntities().stream()
            .filter(e -> {
                var pos = e.<Position>getFeature(FeatureType.POSITION);
                return pos != null && pos.getCoords() == targetPos;
            })
            .filter(e -> featureType == null || e.getFeature(featureType) != null)
            .collect(Collectors.toList());
    }

    public Optional<Entity> getFirstEntity(FeatureType featureType) {
        return getEntities().stream()
            .filter(e -> e.getFeature(featureType) != null)
            .findFirst();
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }
}
