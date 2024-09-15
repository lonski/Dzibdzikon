package pl.lonski.dzibdzikon;

import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.EntityFactory;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.map.MapUtils;
import pl.lonski.dzibdzikon.map.Room;
import pl.lonski.dzibdzikon.map.RoomMapGeneratorV2;
import pl.lonski.dzibdzikon.map.TileGrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static pl.lonski.dzibdzikon.Dzibdzikon.RANDOM;

public class LevelFactory {

    public static Level generate() {

        var level = new Level(RoomMapGeneratorV2.generate(50, 30));

        // spawn mobs
        for (Room room : level.getMap().getRooms()) {
            int mobsCountInRoom = RANDOM.nextInt(0, 3);
            while (mobsCountInRoom-- > 0) {
                var pos = room.getRandomPosition();
                if (level.getEntityAt(pos, null).isEmpty()) {
                    int mobType = RANDOM.nextInt(100);

                    Entity mob;
                    //                    if (mobType > 80) {
                    mob = EntityFactory.createGlazolud();
                    //                    } else {
                    //                        mob = EntityFactory.createZombie();
                    //                    }

                    mob.addFeature(FeatureType.POSITION, new Position(pos, 0, 10));
                    level.addEntity(mob);
                }
            }
        }

        // spawn items
        for (Room room : level.getMap().getRooms()) {
            int maxItemsInRoom = RANDOM.nextInt(0, 2);
            while (maxItemsInRoom-- > 0) {
                var pos = room.getRandomPosition();
                if (level.getEntityAt(pos, null).isEmpty()) {
                    var item = EntityFactory.createHealingPotion();

                    item.addFeature(FeatureType.POSITION, new Position(pos, 0, 10));
                    level.addEntity(item);
                }
            }
        }

        // put doors
        var map = level.getMap();
        Collections.shuffle(map.getRooms());
        int minDoors = map.getRooms().size() / 2;
        int maxDoors = map.getRooms().size();
        for (Room room : map.getRooms().subList(0, RANDOM.nextInt(minDoors, maxDoors))) {
            var possibleDoors = findPossibleDoorPositions(level, room);
            Collections.shuffle(possibleDoors);
            if (!possibleDoors.isEmpty()) {
                var doorInRoom = RANDOM.nextInt(1, possibleDoors.size() + 1);
                for (int i = 0; i < doorInRoom; i++) {
                    var possiblePos = possibleDoors.get(i);
                    if (level.getEntityAt(possiblePos.pos, null).isEmpty()) {
                        var door = EntityFactory.createDoor(RANDOM.nextInt(10) > 7);
                        door.addFeature(FeatureType.POSITION, new Position(possiblePos.pos, 0, 1));
                        level.addEntity(door);
                    }
                }
            }
        }

        // put downstairs
        var downstairs = EntityFactory.createDownstairs();
        var downstairsPos =
                map.getRooms().get(RANDOM.nextInt(map.getRooms().size())).getCenter();
        downstairs.addFeature(FeatureType.POSITION, new Position(downstairsPos));
        level.addEntity(downstairs);

        return level;
    }

    private static List<PossiblePosition> findPossibleDoorPositions(Level level, Room room) {
        List<PossiblePosition> possibleDoors = new ArrayList<>();
        var map = level.getMap();

        for (int rx = 0; rx < room.w(); rx++) {
            int x = room.x() + rx;

            // top wall
            int y = room.y() - 1;
            if (map.inBounds(x, y)
                    && map.getTile(x, y).isFloor()
                    && horizontalNeighboursAreWalls(x, y, map)
                    && noDoorsNearby(x, y, level)) {
                possibleDoors.add(new PossiblePosition(new Point(x, y), 0));
            }

            // bottom wall
            y = room.y() + room.h();
            if (map.inBounds(x, y)
                    && map.getTile(x, y).isFloor()
                    && horizontalNeighboursAreWalls(x, y, map)
                    && noDoorsNearby(x, y, level)) {
                possibleDoors.add(new PossiblePosition(new Point(x, y), 0));
            }
        }

        for (int ry = 0; ry < room.h(); ry++) {
            int y = room.y() + ry;

            // left wall
            int x = room.x() - 1;
            if (map.inBounds(x, y)
                    && map.getTile(x, y).isFloor()
                    && verticalNeighboursAreWalls(x, y, map)
                    && noDoorsNearby(x, y, level)) {
                possibleDoors.add(new PossiblePosition(new Point(x, y), 90));
            }

            // right wall
            x = room.x() + room.w();
            if (map.inBounds(x, y)
                    && map.getTile(x, y).isFloor()
                    && verticalNeighboursAreWalls(x, y, map)
                    && noDoorsNearby(x, y, level)) {
                possibleDoors.add(new PossiblePosition(new Point(x, y), 90));
            }
        }

        return possibleDoors;
    }

    private static boolean noDoorsNearby(int x, int y, Level level) {
        for (Point neighbourPosition : MapUtils.getNeighbourPositions(new Point(x, y))) {
            if (level.getEntityAt(neighbourPosition, FeatureType.OPENABLE).isPresent()) {
                return false;
            }
        }
        return true;
    }

    private static boolean horizontalNeighboursAreWalls(int x, int y, TileGrid map) {
        var nbPos1 = new Point(x - 1, y);
        var nbPos2 = new Point(x + 1, y);
        return (map.inBounds(nbPos1)
                && map.getTile(nbPos1).isWall()
                && map.inBounds(nbPos2)
                && map.getTile(nbPos2).isWall());
    }

    private static boolean verticalNeighboursAreWalls(int x, int y, TileGrid map) {
        var nbPos1 = new Point(x, y - 1);
        var nbPos2 = new Point(x, y + 1);
        return (map.inBounds(nbPos1)
                && map.getTile(nbPos1).isWall()
                && map.inBounds(nbPos2)
                && map.getTile(nbPos2).isWall());
    }

    record PossiblePosition(Point pos, float rotation) {}
}
