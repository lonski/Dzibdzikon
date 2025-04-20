package pl.lonski.dzibdzikon.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;

public class EntityMap {

    private final List<Entity> entities = new ArrayList<>();
    private final HashMap<Entity, Position> entityMap;
    private final HashSet[][] tileMap;
    private final boolean[][] openableMap;

    public EntityMap(int width, int height) {
        entityMap = new HashMap<>();
        tileMap = new HashSet[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tileMap[x][y] = new HashSet<>();
            }
        }
        openableMap = new boolean[width][height];
    }

    public boolean isOpenable(Point coords) {
        return openableMap[coords.x()][coords.y()];
    }

    public void moveEntity(Entity entity, Point newCoords) {
        var pos = entityMap.get(entity);

        if (pos == null) {
            // move non existent entity
            // one case is in rock ai: if it hits the wall it got removed,
            // but then the animation action finishes later and tries to move it
            return;
        }

        if (newCoords.equals(pos.getCoords())) {
            pos.setCoords(newCoords);
            return;
        }

        var prevCoords = pos.getCoords();
        // add entity to new position
        tileMap[prevCoords.x()][prevCoords.y()].remove(entity);
        tileMap[newCoords.x()][newCoords.y()].add(entity);

        pos.setCoords(newCoords);

        if (entity.getFeature(FeatureType.OPENABLE) != null) {
            openableMap[prevCoords.x()][prevCoords.y()] = false;
            openableMap[newCoords.x()][newCoords.y()] = true;
        }
    }

    public void addEntity(Entity entity, Point coords) {
        entity.getPosition().setCoords(coords);
        tileMap[coords.x()][coords.y()].add(entity);
        entityMap.put(entity, entity.getPosition());
        entities.add(entity);

        if (entity.getFeature(FeatureType.OPENABLE) != null) {
            openableMap[coords.x()][coords.y()] = true;
        }
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);

        var pos = entityMap.get(entity);
        if (pos == null) {
            return;
        }

        var coords = entityMap.get(entity).getCoords();
        tileMap[coords.x()][coords.y()].remove(entity);
        entityMap.remove(entity);

        if (entity.getFeature(FeatureType.OPENABLE) != null) {
            openableMap[coords.x()][coords.y()] = false;
        }
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public Set<Map.Entry<Entity, Position>> getEntitiesWithPosition() {
        return entityMap.entrySet();
    }

    public HashSet<Entity> getEntitiesAt(Point pos) {
        return tileMap[pos.x()][pos.y()];
    }
}
