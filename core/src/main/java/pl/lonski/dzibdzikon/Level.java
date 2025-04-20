package pl.lonski.dzibdzikon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import pl.lonski.dzibdzikon.effect.tile.TileEffect;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Openable;
import pl.lonski.dzibdzikon.map.EntityMap;
import pl.lonski.dzibdzikon.map.Position;
import pl.lonski.dzibdzikon.map.TileGrid;

public class Level {

    private final TileGrid map;
    private final Set<Point> visited = new HashSet<>();
    private final Set<Point> visible = new HashSet<>();
    private Map<Point, List<TileEffect>> tileEffects = new HashMap<>();
    private final EntityMap entityMap;

    public Level(TileGrid map) {
        this.map = map;
        this.entityMap = new EntityMap(map.getWidth(), map.getHeight());
    }

    public TileGrid getMap() {
        return map;
    }

    public List<Entity> getEntities() {
        return entityMap.getEntities();
    }

    public Set<Map.Entry<Entity, Position>> getEntitiesWithPosition() {
        return entityMap.getEntitiesWithPosition();
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

        if (includeMonsters && getEntityAt(pos, FeatureType.ATTACKABLE) != null) {
            return true;
        }

        // openables
        if (entityMap.isOpenable(pos)
                && getEntityAt(pos, FeatureType.OPENABLE)
                        .<Openable>getFeature(FeatureType.OPENABLE)
                        .obstacle()) {
            return true;
        }

        return false;
    }

    public boolean isOpaque(Point pos) {
        if (!map.inBounds(pos)) {
            return true;
        }

        // openables
        if (entityMap.isOpenable(pos)) {
            return getEntityAt(pos, FeatureType.OPENABLE)
                    .<Openable>getFeature(FeatureType.OPENABLE)
                    .opaque();
        }

        return map.getTile(pos).isWall();
    }

    public void addEntity(Entity entity, Point coords) {
        entityMap.addEntity(entity, coords);
    }

    public Entity getEntityAt(Point pos, FeatureType featureType) {
        return entityMap.getEntitiesAt(pos).stream()
                .filter(e -> featureType == null || e.getFeature(featureType) != null)
                .findFirst()
                .orElse(null);
    }

    public void moveEntity(Entity entity, Point newCoords) {
        entityMap.moveEntity(entity, newCoords);
    }

    public List<Entity> getEntitiesAt(Point targetPos, FeatureType featureType) {
        return entityMap.getEntitiesAt(targetPos).stream()
                .filter(e -> featureType == null || e.getFeature(featureType) != null)
                .collect(Collectors.toList());
    }

    public List<Entity> getEntitiesAtCircle(Point center, int radius, FeatureType featureType) {
        var points = new HashSet<Point>();
        PositionUtils.inFilledCircleOf(radius, cp -> points.add(cp.add(center)));

        return points.stream()
                .filter(map::inBounds)
                .flatMap(p -> entityMap.getEntitiesAt(p).stream())
                .filter(e -> featureType == null || e.getFeature(featureType) != null)
                .collect(Collectors.toList());
    }

    public List<Entity> getEntitiesAt(Set<Point> points, FeatureType featureType) {
        return points.stream()
                .flatMap(p -> entityMap.getEntitiesAt(p).stream())
                .filter(e -> featureType == null || e.getFeature(featureType) != null)
                .collect(Collectors.toList());
    }

    public void removeEntity(Entity entity) {
        entityMap.removeEntity(entity);
    }
}
