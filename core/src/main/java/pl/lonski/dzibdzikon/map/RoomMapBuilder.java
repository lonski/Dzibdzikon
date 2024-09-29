package pl.lonski.dzibdzikon.map;

import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RoomMapBuilder {

    private int width = 50;
    private int height = 30;
    private int maxRooms = 30;
    private TextureId wall = TextureId.WALL;
    private TextureId floor = TextureId.FLOOR;
    private int roomSizeMin = 3;
    private int roomSizeMax = 8;
    private float connectWithClosesRoomPercent = 0.7f;
    private RoomGenerator roomGenerator = new RoomGenerator() {
        @Override
        public Room createRoom(int x, int y, int w, int h) {
            return new Room(x, y, w, h);
        }

        @Override
        public void placed() {}
    };

    public RoomMapBuilder width(int val) {
        this.width = val;
        return this;
    }

    public RoomMapBuilder height(int val) {
        this.height = val;
        return this;
    }

    public RoomMapBuilder maxRooms(int val) {
        this.maxRooms = val;
        return this;
    }

    public RoomMapBuilder wallTexture(TextureId val) {
        this.wall = val;
        return this;
    }

    public RoomMapBuilder floorTexture(TextureId val) {
        this.floor = val;
        return this;
    }

    public RoomMapBuilder roomSizeMin(int val) {
        this.roomSizeMin = val;
        return this;
    }

    public RoomMapBuilder roomSizeMax(int val) {
        this.roomSizeMax = val;
        return this;
    }

    public RoomMapBuilder connectWithClosesRoomPercent(float val) {
        this.connectWithClosesRoomPercent = val;
        return this;
    }

    public RoomMapBuilder roomGenerator(RoomGenerator gen) {
        this.roomGenerator = gen;
        return this;
    }

    public TileGrid build() {

        var map = new TileGrid(width, height);
        var random = new SecureRandom();

        // Fill whole map with '#'
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map.setTile(x, y, wall);
            }
        }

        int maxRoomsCounter = maxRooms;
        // try place X rooms
        while (maxRoomsCounter-- > 0) {
            int roomWidth = random.nextInt(roomSizeMin, roomSizeMax);
            int roomHeight = random.nextInt(roomSizeMin, roomSizeMax);
            int roomX = random.nextInt(1, width - roomWidth);
            int roomY = random.nextInt(1, height - roomHeight);
            Room room = roomGenerator.createRoom(roomX, roomY, roomWidth, roomHeight);
            if (room.canPlace(map)) {
                room.put(map, floor);
                map.addRoom(room);
                roomGenerator.placed();
            }
        }

        var unconnectedRooms = new ArrayList<>(map.getRooms());
        Collections.shuffle(unconnectedRooms);
        var currentRoom = unconnectedRooms.remove(0);
        while (!unconnectedRooms.isEmpty()) {
            // find closest room
            var toConnectWith = Dzibdzikon.RANDOM.nextDouble() < connectWithClosesRoomPercent
                    ? findClosestRoom(currentRoom, unconnectedRooms)
                    : unconnectedRooms.remove(0);

            unconnectedRooms.remove(toConnectWith);

            // connect rooms
            Point room1p = currentRoom.getRandomNonCornerPerimeterPosition();
            Point room2p = toConnectWith.getRandomNonCornerPerimeterPosition();

            MapUtils.pathfind(room1p, room2p, pos -> MapUtils.inBounds(pos, width, height), false)
                    .forEach(pos -> map.setTile(pos.x(), pos.y(), floor));
            map.setTile(room1p.x(), room1p.y(), floor);
            map.setTile(room2p.x(), room2p.y(), floor);

            // go to next room
            currentRoom = toConnectWith;
        }

        map.getRooms().forEach(r -> r.afterMapGenerated(map));

        return map;
    }

    private static Room findClosestRoom(Room currentRoom, ArrayList<Room> unconnectedRooms) {
        return unconnectedRooms.stream()
                .min(Comparator.comparingDouble(r -> calculateDistance(currentRoom, r)))
                .orElseThrow();
    }

    private static double calculateDistance(Room r1, Room r2) {
        var candidateCenterX = r1.x() + r2.width() / 2;
        var candidateCenterY = r1.y() + r2.height() / 2;
        var currentCenterX = r2.x() + r2.width() / 2;
        var currentCenterY = r2.y() + r2.height() / 2;

        return MapUtils.euclideanDistance(
                new Point(candidateCenterX, candidateCenterY), new Point(currentCenterX, currentCenterY));
    }

    public interface RoomGenerator {
        Room createRoom(int x, int y, int w, int h);

        void placed();
    }
}
