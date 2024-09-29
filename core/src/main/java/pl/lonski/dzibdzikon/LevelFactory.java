package pl.lonski.dzibdzikon;

import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.EntityFactory;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.map.CircleRoom;
import pl.lonski.dzibdzikon.map.PtakodrzewoRoom;
import pl.lonski.dzibdzikon.map.MapUtils;
import pl.lonski.dzibdzikon.map.Room;
import pl.lonski.dzibdzikon.map.RoomMapBuilder;
import pl.lonski.dzibdzikon.map.RoomType;
import pl.lonski.dzibdzikon.map.TileGrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static pl.lonski.dzibdzikon.Dzibdzikon.RANDOM;

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

            return RANDOM.nextDouble() > 0.7 ? new CircleRoom(x, y, w, h) : new Room(x, y, w, h);
        }

        @Override
        public void placed() {
            lastPlaced = true;
        }
    }

    private static Supplier<Entity> generateMobFn() {
        return () -> {
            int mobType = RANDOM.nextInt(100);

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
        return new ArrayList<>(room.getEntrances(level.getMap()).stream()
                .filter(p -> noDoorsNearby(p.x(), p.y(), level))
                .toList());
    }

    private static boolean noDoorsNearby(int x, int y, Level level) {
        for (Point neighbourPosition : MapUtils.getNeighbourPositions(new Point(x, y))) {
            if (level.getEntityAt(neighbourPosition, FeatureType.OPENABLE).isPresent()) {
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
                int mobsCountInRoom = RANDOM.nextInt(minMobsPerRoom, maxMobsPerRoom + 1);
                while (mobsCountInRoom-- > 0) {
                    var pos = room.getRandomPosition();
                    if (level.getEntityAt(pos, null).isEmpty()) {
                        Entity mob = mobGenerateFn.get();
                        if (room.acceptsEntity(mob)) {
                            mob.addFeature(FeatureType.POSITION, new Position(pos, 0, 10));
                            level.addEntity(mob);
                        }
                    }
                }
            }

            map.getRooms().stream()
                    .filter(room -> room.getRoomType() == RoomType.PTAKODRZEWO)
                    .forEach(dzibdziDrzewoRoom -> {
                        var center = dzibdziDrzewoRoom.getCenter();
                        Entity mob = EntityFactory.createPtakodrzewo();
                        mob.addFeature(FeatureType.POSITION, new Position(center, 0, 10));
                        level.addEntity(mob);
                    });

            // spawn items
            for (Room room : level.getMap().getRooms()) {
                int maxItemsInRoom = RANDOM.nextInt(minItemsPerRoom, maxItemsPerRoom + 1);
                while (maxItemsInRoom-- > 0) {
                    var pos = room.getRandomPosition();
                    if (level.getEntityAt(pos, null).isEmpty()) {
                        Entity item = itemGenerateFn.get();
                        if (room.acceptsEntity(item)) {
                            item.addFeature(FeatureType.POSITION, new Position(pos, 0, 10));
                            level.addEntity(item);
                        }
                    }
                }
            }

            // put doors
            var map = level.getMap();
            Collections.shuffle(map.getRooms());
            int minDoors = Math.round(map.getRooms().size() * minDoorPercentage);
            int maxDoors = Math.round(map.getRooms().size() * maxDoorPercentage);
            for (Room room : map.getRooms().subList(0, RANDOM.nextInt(minDoors, maxDoors + 1))) {
                var possibleDoors = findPossibleDoorPositions(level, room);
                Collections.shuffle(possibleDoors);
                if (!possibleDoors.isEmpty()) {
                    var doorInRoom = RANDOM.nextInt(1, possibleDoors.size() + 1);
                    for (int i = 0; i < doorInRoom; i++) {
                        var possiblePos = possibleDoors.get(i);
                        if (level.getEntityAt(possiblePos, null).isEmpty()) {
                            var door = EntityFactory.createDoor(RANDOM.nextFloat() < openedDoorPercentage);
                            door.addFeature(FeatureType.POSITION, new Position(possiblePos, 0, 1));
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
    }
}
