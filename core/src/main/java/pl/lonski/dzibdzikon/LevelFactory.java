package pl.lonski.dzibdzikon;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.EntityFactory;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.map.CircleRoom;
import pl.lonski.dzibdzikon.map.MapUtils;
import pl.lonski.dzibdzikon.map.PtakodrzewoRoom;
import pl.lonski.dzibdzikon.map.Room;
import pl.lonski.dzibdzikon.map.RoomMapBuilder;
import pl.lonski.dzibdzikon.map.RoomType;
import pl.lonski.dzibdzikon.map.TileGrid;

public class LevelFactory {

    public static Level generate() {
        return new LevelBuilder()
                .map(new RoomMapBuilder()
                        .width(50)
                        .height(30)
                        .roomGenerator(new RoomGeneratorFn())
                        .build())
                .minMobsPerRoom(0)
                .maxMobsPerRoom(2)
                .minItemsPerRoom(-1)
                .maxItemsPerRoom(1)
                .minDoorPercentage(0.2f)
                .maxDoorPercentage(0.7f)
                .openedDoorPercentage(0.2f)
                .generateMobFn(generateMobFn())
                .generateItemFn(generateItemFn())
                .build();
    }

    private static class RoomGeneratorFn implements RoomMapBuilder.RoomGenerator {

        private boolean lastPlaced = false;

        @Override
        public Room createRoom(int x, int y, int w, int h) {
            if (!lastPlaced) {
                return new PtakodrzewoRoom(x, y, w, h);
            }

            return DzibdziRandom.nextDouble() > 0.7 ? new CircleRoom(x, y, w, h) : new Room(x, y, w, h);
        }

        @Override
        public void placed() {
            lastPlaced = true;
        }
    }

    private static Supplier<Entity> generateMobFn() {
        return () -> {
            int mobType = DzibdziRandom.nextInt(100);

            Entity mob;
            if (mobType > 80) {
                mob = EntityFactory.createGlazolud();
            } else {
                mob = EntityFactory.createZombie();
            }

            return mob;
        };
    }

    private static Supplier<Entity> generateItemFn() {
        return () -> {
            return EntityFactory.createHealingPotion();
        };
    }

    private static List<Point> findPossibleDoorPositions(Level level, Room room) {
        return room.getEntrances(level.getMap()).stream()
                .filter(p -> noDoorsNearby(p.x(), p.y(), level))
                .collect(Collectors.toList());
    }

    private static boolean noDoorsNearby(int x, int y, Level level) {
        for (Point neighbourPosition : MapUtils.getNeighbourPositions(new Point(x, y))) {
            if (level.getEntityAt(neighbourPosition, FeatureType.OPENABLE) != null) {
                return false;
            }
        }
        return true;
    }

    public static class LevelBuilder {

        private TileGrid map;

        // mobs
        private int minMobsPerRoom = 0;
        private int maxMobsPerRoom = 2;
        private Supplier<Entity> mobGenerateFn;

        // items
        private int minItemsPerRoom = 0;
        private int maxItemsPerRoom = 1;
        private Supplier<Entity> itemGenerateFn;

        // doors
        private float minDoorPercentage = 0.8f;
        private float maxDoorPercentage = 0.8f;
        private float openedDoorPercentage = 0.2f;

        public LevelBuilder map(TileGrid map) {
            this.map = map;
            return this;
        }

        public LevelBuilder minMobsPerRoom(int val) {
            this.minMobsPerRoom = val;
            return this;
        }

        public LevelBuilder maxMobsPerRoom(int val) {
            this.maxMobsPerRoom = val;
            return this;
        }

        public LevelBuilder minItemsPerRoom(int val) {
            this.minItemsPerRoom = val;
            return this;
        }

        public LevelBuilder maxItemsPerRoom(int val) {
            this.maxItemsPerRoom = val;
            return this;
        }

        public LevelBuilder minDoorPercentage(float val) {
            this.minDoorPercentage = val;
            return this;
        }

        public LevelBuilder maxDoorPercentage(float val) {
            this.maxDoorPercentage = val;
            return this;
        }

        public LevelBuilder openedDoorPercentage(float val) {
            this.openedDoorPercentage = val;
            return this;
        }

        public LevelBuilder generateMobFn(Supplier<Entity> fn) {
            this.mobGenerateFn = fn;
            return this;
        }

        public LevelBuilder generateItemFn(Supplier<Entity> fn) {
            this.itemGenerateFn = fn;
            return this;
        }

        public Level build() {
            var level = new Level(map);

            // spawn mobs
            for (Room room : level.getMap().getRooms()) {
                int mobsCountInRoom = DzibdziRandom.nextInt(minMobsPerRoom, maxMobsPerRoom + 1);
                while (mobsCountInRoom-- > 0) {
                    var pos = room.getRandomPosition();
                    if (level.getEntityAt(pos, null) == null) {
                        Entity mob = mobGenerateFn.get();
                        if (room.acceptsEntity(mob)) {
                            level.addEntity(mob, pos);
                            mob.getPosition().setzLevel(10);
                        }
                    }
                }
            }

            map.getRooms().stream()
                    .filter(room -> room.getRoomType() == RoomType.PTAKODRZEWO)
                    .forEach(dzibdziDrzewoRoom -> {
                        var center = dzibdziDrzewoRoom.getCenter();
                        Entity mob = EntityFactory.createPtakodrzewo();
                        level.addEntity(mob, center);
                        mob.getPosition().setzLevel(10);
                    });

            // spawn items
            for (Room room : level.getMap().getRooms()) {
                int maxItemsInRoom = DzibdziRandom.nextInt(minItemsPerRoom, maxItemsPerRoom + 1);
                while (maxItemsInRoom-- > 0) {
                    var pos = room.getRandomPosition();
                    if (level.getEntityAt(pos, null) == null) {
                        Entity item = itemGenerateFn.get();
                        if (room.acceptsEntity(item)) {
                            level.addEntity(item, pos);
                            item.getPosition().setzLevel(10);
                        }
                    }
                }
            }

            // put doors
            var map = level.getMap();
            Collections.shuffle(map.getRooms());
            int minDoors = Math.round(map.getRooms().size() * minDoorPercentage);
            int maxDoors = Math.round(map.getRooms().size() * maxDoorPercentage);
            for (Room room : map.getRooms().subList(0, DzibdziRandom.nextInt(minDoors, maxDoors + 1))) {
                var possibleDoors = findPossibleDoorPositions(level, room);
                Collections.shuffle(possibleDoors);
                if (!possibleDoors.isEmpty()) {
                    var doorInRoom = DzibdziRandom.nextInt(1, possibleDoors.size() + 1);
                    for (int i = 0; i < doorInRoom; i++) {
                        var possiblePos = possibleDoors.get(i);
                        if (level.getEntityAt(possiblePos, null) == null) {
                            var door = EntityFactory.createDoor(DzibdziRandom.nextDouble() < openedDoorPercentage);
                            level.addEntity(door, possiblePos);
                            door.getPosition().setzLevel(1);
                        }
                    }
                }
            }

            // put downstairs
            var downstairs = EntityFactory.createDownstairs();
            var downstairsPos = map.getRooms()
                    .get(DzibdziRandom.nextInt(map.getRooms().size()))
                    .getCenter();
            var maxTries = 5;
            while (level.isObstacle(downstairsPos, true) && maxTries-- > 0) {
                downstairsPos = map.getRooms()
                        .get(DzibdziRandom.nextInt(map.getRooms().size()))
                        .getCenter();
            }
            level.addEntity(downstairs, downstairsPos);

            return level;
        }
    }
}
