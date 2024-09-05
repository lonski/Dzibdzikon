package pl.lonski.dzibdzikon;


import static pl.lonski.dzibdzikon.Dzibdzikon.RANDOM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pl.lonski.dzibdzikon.entity.EntityFactory;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.map.Room;
import pl.lonski.dzibdzikon.map.RoomMapGeneratorV2;

public class LevelFactory {

    public static Level generate() {

        var level = new Level(RoomMapGeneratorV2.generate(80, 80));

        for (Room room : level.getMap().getRooms()) {
            int zombieCount = RANDOM.nextInt(0, 3);
            while (zombieCount-- > 0) {
                var pos = room.getRandomPosition();
                if (level.getEntityAt(pos, null).isEmpty()) {
                    var zombie = EntityFactory.createZombie();
                    zombie.addFeature(FeatureType.POSITION, new Position(pos));
                    level.addEntity(zombie);
                }
            }
        }

        // put doors
//        var map = level.getMap();
//        Collections.shuffle(map.getRooms());
//        int minDoors = map.getRooms().size() / 5;
//        int maxDoors = map.getRooms().size();
//        for (Room room : map.getRooms().subList(0, RANDOM.nextInt(minDoors, maxDoors))) {
//            var possibleDoors = findPossibleDoorPositions(map.getTiles(), room);
//            if (!possibleDoors.isEmpty()) {
//                Collections.shuffle(possibleDoors);
//                if (level.getEntityAt(possibleDoors.get(0), null).isEmpty()) {
//                    var door = EntityFactory.createDoor();
//                    door.addFeature(FeatureType.POSITION, new Position(possibleDoors.get(0)));
//                    level.addEntity(door);
//                }
//            }
//        }

        // put downstairs
//        var downstairs = EntityFactory.createDownstairs();
//        var downstairsPos = map.getRooms().get(0).getRandomPosition();
//        downstairs.addFeature(FeatureType.POSITION, new Position(downstairsPos));
//        level.addEntity(downstairs);

        return level;
    }

//    private static List<Integer> findPossibleDoorPositions(List<Tile> tiles, Room room) {
//        List<Integer> possibleDoors = new ArrayList<>();
//
//        for (int rx = 0; rx < room.w(); rx++) {
//            int x = room.x() + rx;
//
//            // top wall
//            int y = room.y() - 1;
//            int pos = MapUtils.toPos(x, y);
//            if (MapUtils.inBounds(pos) && tiles.get(pos) == Tile.FLOOR && horizontalNeighboursAreWalls(x, y, tiles)) {
//                possibleDoors.add(MapUtils.toPos(x, y));
//            }
//
//            // bottom wall
//            y = room.y() + room.h();
//            pos = MapUtils.toPos(x, y);
//            if (MapUtils.inBounds(pos) && tiles.get(pos) == Tile.FLOOR && horizontalNeighboursAreWalls(x, y, tiles)) {
//                possibleDoors.add(MapUtils.toPos(x, y));
//            }
//        }
//
//        for (int ry = 0; ry < room.h(); ry++) {
//            int y = room.y() + ry;
//
//            // left wall
//            int x = room.x() - 1;
//            int pos = MapUtils.toPos(x, y);
//            if (MapUtils.inBounds(pos) && tiles.get(pos) == Tile.FLOOR && verticalNeighboursAreWalls(x, y, tiles)) {
//                possibleDoors.add(MapUtils.toPos(x, y));
//            }
//
//            // right wall
//            x = room.x() + room.w();
//            pos = MapUtils.toPos(x, y);
//            if (MapUtils.inBounds(pos) && tiles.get(pos) == Tile.FLOOR && verticalNeighboursAreWalls(x, y, tiles)) {
//                possibleDoors.add(MapUtils.toPos(x, y));
//            }
//        }
//
//        return possibleDoors;
//    }
//
//    private static boolean horizontalNeighboursAreWalls(int x, int y, List<Tile> tiles) {
//        int nbPos1 = MapUtils.toPos(x - 1, y);
//        int nbPos2 = MapUtils.toPos(x + 1, y);
//        return (MapUtils.inBounds(nbPos1)
//            && tiles.get(nbPos1) == Tile.WALL
//            && MapUtils.inBounds(nbPos2)
//            && tiles.get(nbPos2) == Tile.WALL);
//    }
//
//    private static boolean verticalNeighboursAreWalls(int x, int y, List<Tile> tiles) {
//        int nbPos1 = MapUtils.toPos(x, y - 1);
//        int nbPos2 = MapUtils.toPos(x, y + 1);
//        return (MapUtils.inBounds(nbPos1)
//            && tiles.get(nbPos1) == Tile.WALL
//            && MapUtils.inBounds(nbPos2)
//            && tiles.get(nbPos2) == Tile.WALL);
//    }
}
