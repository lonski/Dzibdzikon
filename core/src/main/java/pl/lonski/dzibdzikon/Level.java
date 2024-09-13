package pl.lonski.dzibdzikon;

import pl.lonski.dzibdzikon.effect.TileEffect;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Openable;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.map.TileGrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Level {

    private final TileGrid map;
    private final List<Entity> entities = new ArrayList<>();
    private final Set<Point> visited = new HashSet<>();
    private final Set<Point> visible = new HashSet<>();
    private Map<Point, List<TileEffect>> tileEffects = new HashMap<>();

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

    public Map<Point, List<TileEffect>> getTileEffects() {
        return tileEffects;
    }

    public void setTileEffects(Map<Point, List<TileEffect>> tileEffects) {
        this.tileEffects = tileEffects;
    }

    public void addTileEffect(Point pos, TileEffect effect) {
        tileEffects.computeIfAbsent(pos, point -> new ArrayList<>());
        tileEffects.get(pos).add(effect);
    }

    public boolean isObstacle(Point pos, boolean includeMonsters) {
        if (!map.inBounds(pos.x(), pos.y())) {
            return true;
        }

        if (map.getTile(pos).isWall()) {
            return true;
        }

        if (includeMonsters && getEntityAt(pos, FeatureType.ATTACKABLE).isPresent()) {
            return true;
        }

        // openables
        if (getEntityAt(pos, FeatureType.OPENABLE)
                .map(o -> o.<Openable>getFeature(FeatureType.OPENABLE).obstacle())
                .orElse(false)) {
            return true;
        }

        return false;
    }

    public boolean isOpaque(Point pos) {
        if (!map.inBounds(pos)) {
            return true;
        }

        // openables
        if (getEntityAt(pos, FeatureType.OPENABLE)
                .map(o -> o.<Openable>getFeature(FeatureType.OPENABLE).opaque())
                .orElse(false)) {
            return true;
        }

        return map.getTile(pos).isWall();
    }

    public Point getRandomFreePosition() {
        while (true) {
            Point pos = new Point(DzibdziRandom.nextInt(map.getWidth()), DzibdziRandom.nextInt(map.getHeight()));
            if (map.getTile(pos).isFloor()
                    && getEntitiesAt(pos, FeatureType.POSITION).isEmpty()) {
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
                    return pos != null && pos.getCoords().equals(targetPos);
                })
                .filter(e -> featureType == null || e.getFeature(featureType) != null)
                .collect(Collectors.toList());
    }

    public List<Entity> getEntitiesAtCircle(Point center, int radius, FeatureType featureType) {
        var points = new HashSet<Point>();
        PositionUtils.inFilledCircleOf(radius, cp -> points.add(cp.add(center)));

        return getEntities().stream()
                .filter(e -> {
                    var pos = e.<Position>getFeature(FeatureType.POSITION);
                    return pos != null && points.contains(pos.getCoords());
                })
                .filter(e -> featureType == null || e.getFeature(featureType) != null)
                .collect(Collectors.toList());
    }

    public List<Entity> getEntitiesAt(Set<Point> points, FeatureType featureType) {
        return getEntities().stream()
                .filter(e -> {
                    var pos = e.<Position>getFeature(FeatureType.POSITION);
                    return pos != null && points.contains(pos.getCoords());
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
