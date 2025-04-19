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
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.map.TileGrid;

public class Level {

    private final TileGrid map;
    private final HashSet[][] entityMap;
    private final boolean[][] openableMap;
    private final List<Entity> entities = new ArrayList<>();
    private final Set<Point> visited = new HashSet<>();
    private final Set<Point> visible = new HashSet<>();
    private Map<Point, List<TileEffect>> tileEffects = new HashMap<>();

    public Level(TileGrid map) {
        this.map = map;
        this.entityMap = new HashSet[map.getWidth()][map.getHeight()];
        this.openableMap = new boolean[map.getWidth()][map.getHeight()];
    }

    public void updateEntityPos(Entity entity, Point prevPos, Point newPos) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }

        if (prevPos.equals(newPos)) {
            return;
        }

        if (!map.inBounds(prevPos) || !map.inBounds(newPos)) {
            throw new IllegalArgumentException("Position out of bounds");
        }

        // remove entity from previous position
        var prevEntities = entityMap[prevPos.x()][prevPos.y()];
        if (prevEntities != null) {
            prevEntities.remove(entity);
        }

        // add entity to new position
        var newEntities = entityMap[newPos.x()][newPos.y()];
        if (newEntities == null) {
            newEntities = new HashSet<>();
            entityMap[newPos.x()][newPos.y()] = newEntities;
        }
        newEntities.add(entity);

        if (entity.getFeature(FeatureType.OPENABLE) != null) {
            openableMap[prevPos.x()][prevPos.y()] = false;
            openableMap[newPos.x()][newPos.y()] = true;
        }
    }

    public void addEntity(Entity entity) {
        var pos = entity.<Position>getFeature(FeatureType.POSITION);
        if (pos == null) {
            throw new IllegalStateException("entity has no pos");
        }

        var coords = pos.getCoords();
        var posEntities = entityMap[coords.x()][coords.y()];
        if (posEntities == null) {
            posEntities = new HashSet<>();
            entityMap[coords.x()][coords.y()] = posEntities;
        }
        posEntities.add(entity);
        entities.add(entity);

        if (entity.getFeature(FeatureType.OPENABLE) != null) {
            openableMap[pos.getCoords().x()][pos.getCoords().y()] = true;
        }
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

        if (includeMonsters && getEntityAt(pos, FeatureType.ATTACKABLE) != null) {
            return true;
        }

        // openables
        var entityAtPos = getEntityAt(pos, FeatureType.OPENABLE);
        if (entityAtPos != null
                && entityAtPos.<Openable>getFeature(FeatureType.OPENABLE).obstacle()) {
            return true;
        }

        return false;
    }

    public boolean isOpaque(Point pos) {
        if (!map.inBounds(pos)) {
            return true;
        }

        // openables
        if (openableMap[pos.x()][pos.y()]) {
            return getEntityAt(pos, FeatureType.OPENABLE)
                    .<Openable>getFeature(FeatureType.OPENABLE)
                    .opaque();
        }

        return map.getTile(pos).isWall();
    }

    public HashSet<Entity> getEntitiesAt(Point targetPos) {
        var entitiesAt = entityMap[targetPos.x()][targetPos.y()];
        if (entitiesAt == null) {
            return new HashSet<>();
        }
        return entitiesAt;
    }

    public Entity getEntityAt(Point pos, FeatureType featureType) {
        return getEntitiesAt(pos).stream()
                .filter(e -> featureType == null || e.getFeature(featureType) != null)
                .findFirst()
                .orElse(null);
    }

    public List<Entity> getEntitiesAt(Point targetPos, FeatureType featureType) {
        return getEntitiesAt(targetPos).stream()
                .filter(e -> featureType == null || e.getFeature(featureType) != null)
                .toList();
    }

    public List<Entity> getEntitiesAtCircle(Point center, int radius, FeatureType featureType) {
        var points = new HashSet<Point>();
        PositionUtils.inFilledCircleOf(radius, cp -> points.add(cp.add(center)));

        return points.stream()
                .filter(map::inBounds)
                .flatMap(p -> getEntitiesAt(p).stream())
                .filter(e -> featureType == null || e.getFeature(featureType) != null)
                .collect(Collectors.toList());
    }

    public List<Entity> getEntitiesAt(Set<Point> points, FeatureType featureType) {
        return points.stream()
                .flatMap(p -> getEntitiesAt(p).stream())
                .filter(e -> featureType == null || e.getFeature(featureType) != null)
                .collect(Collectors.toList());
    }

    public void removeEntity(Entity entity) {
        var pos = entity.<Position>getFeature(FeatureType.POSITION);
        getEntitiesAt(pos.getCoords()).remove(entity);
        entities.remove(entity);

        if (entity.getFeature(FeatureType.OPENABLE) != null) {
            openableMap[pos.getCoords().x()][pos.getCoords().y()] = false;
        }
    }
}
