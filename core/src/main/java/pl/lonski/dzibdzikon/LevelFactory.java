package pl.lonski.dzibdzikon;


import pl.lonski.dzibdzikon.entity.EntityFactory;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.map.Glyph;
import pl.lonski.dzibdzikon.map.Room;
import pl.lonski.dzibdzikon.map.RoomMapGeneratorV2;
import pl.lonski.dzibdzikon.map.TileGrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static pl.lonski.dzibdzikon.Dzibdzikon.RANDOM;

public class LevelFactory {

    public static Level generate() {

        var level = new Level(RoomMapGeneratorV2.generate(30, 20));

        for (Room room : level.getMap().getRooms()) {
            int zombieCount = RANDOM.nextInt(0, 3);
            while (zombieCount-- > 0) {
                var pos = room.getRandomPosition();
                if (level.getEntityAt(pos, null).isEmpty()) {
                    var zombie = EntityFactory.createZombie();
                    zombie.addFeature(FeatureType.POSITION, new Position(pos, 0, 10));
                    level.addEntity(zombie);
                }
            }
        }

        // put doors
        var map = level.getMap();
        Collections.shuffle(map.getRooms());
        int minDoors = map.getRooms().size() / 2;
        int maxDoors = map.getRooms().size();
        for (Room room : map.getRooms().subList(0, RANDOM.nextInt(minDoors, maxDoors))) {
            var possibleDoors = findPossibleDoorPositions(map, room);
            Collections.shuffle(possibleDoors);
            if (!possibleDoors.isEmpty()) {
                var doorInRoom = RANDOM.nextInt(1, possibleDoors.size() + 1);
                for (int i = 0; i < doorInRoom; i++) {
                    var possiblePos = possibleDoors.get(i);
                    if (level.getEntityAt(possiblePos.pos, null).isEmpty()) {
                        var door = EntityFactory.createDoor();
                        door.addFeature(FeatureType.POSITION, new Position(possiblePos.pos, 0, 1));
                        level.addEntity(door);
                    }
                }
            }
        }

        // put downstairs
        var downstairs = EntityFactory.createDownstairs();
        var downstairsPos = map.getRooms().get(RANDOM.nextInt(map.getRooms().size())).getCenter();
        downstairs.addFeature(FeatureType.POSITION, new Position(downstairsPos));
        level.addEntity(downstairs);

        return level;
    }

    private static List<PossiblePosition> findPossibleDoorPositions(TileGrid map, Room room) {
        List<PossiblePosition> possibleDoors = new ArrayList<>();

        for (int rx = 0; rx < room.w(); rx++) {
            int x = room.x() + rx;

            // top wall
            int y = room.y() - 1;
            if (map.inBounds(x, y) && map.getTile(x, y) == Glyph.FLOOR && horizontalNeighboursAreWalls(x, y, map)) {
                possibleDoors.add(new PossiblePosition(new Point(x, y), 0));
            }

            // bottom wall
            y = room.y() + room.h();
            if (map.inBounds(x, y) && map.getTile(x, y) == Glyph.FLOOR && horizontalNeighboursAreWalls(x, y, map)) {
                possibleDoors.add(new PossiblePosition(new Point(x, y), 0));
            }
        }

        for (int ry = 0; ry < room.h(); ry++) {
            int y = room.y() + ry;

            // left wall
            int x = room.x() - 1;
            if (map.inBounds(x, y) && map.getTile(x, y) == Glyph.FLOOR && verticalNeighboursAreWalls(x, y, map)) {
                possibleDoors.add(new PossiblePosition(new Point(x, y), 90));
            }

            // right wall
            x = room.x() + room.w();
            if (map.inBounds(x, y) && map.getTile(x, y) == Glyph.FLOOR && verticalNeighboursAreWalls(x, y, map)) {
                possibleDoors.add(new PossiblePosition(new Point(x, y), 90));
            }
        }

        return possibleDoors;
    }

    private static boolean horizontalNeighboursAreWalls(int x, int y, TileGrid map) {
        var nbPos1 = new Point(x - 1, y);
        var nbPos2 = new Point(x + 1, y);
        return (map.inBounds(nbPos1)
            && map.getTile(nbPos1) == Glyph.WALL
            && map.inBounds(nbPos2)
            && map.getTile(nbPos2) == Glyph.WALL);
    }

    private static boolean verticalNeighboursAreWalls(int x, int y, TileGrid map) {
        var nbPos1 = new Point(x, y - 1);
        var nbPos2 = new Point(x, y + 1);
        return (map.inBounds(nbPos1)
            && map.getTile(nbPos1) == Glyph.WALL
            && map.inBounds(nbPos2)
            && map.getTile(nbPos2) == Glyph.WALL);
    }

    record PossiblePosition(Point pos, float rotation) {
    }
}
