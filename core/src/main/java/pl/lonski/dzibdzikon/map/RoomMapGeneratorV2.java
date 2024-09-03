package pl.lonski.dzibdzikon.map;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;

public class RoomMapGeneratorV2 {

    public static TileGrid generate(int width, int height) {

        var map = new TileGrid(width, height);
        var random = new SecureRandom();

        // Fill whole map with '#'
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map.setTile(x, y, Glyph.WALL);
            }
        }

        int maxRooms = 50;
        // try place X rooms
        while (maxRooms-- > 0) {
            int roomWidth = random.nextInt(3, 14);
            int roomHeight = random.nextInt(3, 10);
            int roomX = random.nextInt(1, width - roomWidth);
            int roomY = random.nextInt(1, height - roomHeight);
            if (canPlace(map, roomWidth, roomHeight, roomX, roomY)) {
                // put room
                for (int ry = 0; ry < roomHeight; ry++) {
                    for (int rx = 0; rx < roomWidth; rx++) {
                        map.setTile(rx + roomX, ry + roomY, Glyph.FLOOR);
                    }
                }
                map.addRoom(new Room(roomX, roomY, roomWidth, roomHeight));
            }
        }

        var unconnectedRooms = new ArrayList<>(map.getRooms());
        Collections.shuffle(unconnectedRooms);
        var currentRoom = unconnectedRooms.remove(0);
        while (!unconnectedRooms.isEmpty()) {
            // find closest room
            var toConnectWith = Dzibdzikon.RANDOM.nextDouble() > 0.3
                ? findClosestRoom(currentRoom, unconnectedRooms)
                : unconnectedRooms.remove(0);

            unconnectedRooms.remove(toConnectWith);

            // connect rooms
            int room1x = currentRoom.x() + random.nextInt(currentRoom.w());
            int room1y = currentRoom.y() + random.nextInt(currentRoom.h());

            int room2x = toConnectWith.x() + random.nextInt(toConnectWith.w());
            int room2y = toConnectWith.y() + random.nextInt(toConnectWith.h());

            MapUtils.pathfind(
                    new Point(room1x, room1y),
                    new Point(room2x, room2y),
                    pos -> MapUtils.inBounds(pos, width, height),
                    false)
                .forEach(pos -> map.setTile(pos.x(), pos.y(), Glyph.FLOOR));

            // go to next room
            currentRoom = toConnectWith;
        }

        return map;
    }

    private static Room findClosestRoom(Room currentRoom, ArrayList<Room> unconnectedRooms) {
        return unconnectedRooms.stream()
            .min(Comparator.comparingInt(r -> calculateDistance(currentRoom, r)))
            .orElseThrow();
    }

    private static int calculateDistance(Room r1, Room r2) {
        var candidateCenterX = r1.x() + r2.w() / 2;
        var candidateCenterY = r1.y() + r2.h() / 2;
        var currentCenterX = r2.x() + r2.w() / 2;
        var currentCenterY = r2.y() + r2.h() / 2;

        return MapUtils.distance(
            new Point(candidateCenterX, candidateCenterY), new Point(currentCenterX, currentCenterY));
    }

    private static boolean canPlace(TileGrid tileGrid, int roomWidth, int roomHeight, int roomX, int roomY) {
        for (int ry = -1; ry <= roomHeight; ry++) {
            for (int rx = -1; rx <= roomWidth; rx++) {
                var pos = new Point(roomX + rx, roomY + ry);
                if (!tileGrid.inBounds(pos.x(), pos.y()) || tileGrid.getTile(pos.x(), pos.y()) != Glyph.WALL) {
                    return false;
                }
            }
        }

        return true;
    }
}
