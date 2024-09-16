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
import java.util.function.Supplier;

import static pl.lonski.dzibdzikon.Dzibdzikon.RANDOM;

public class LevelFactory {

    public static Level generate() {
        return new LevelBuilder()
                .width(50)
                .height(30)
                .minMobsPerRoom(0)
                .maxMobsPerRoom(2)
                .minItemsPerRoom(0)
                .maxItemsPerRoom(1)
                .minDoorPercentage(0.2f)
                .maxDoorPercentage(0.7f)
                .openedDoorPercentage(0.2f)
                .generateMobFn(generateMobFn())
                .generateItemFn(generateItemFn())
                .build();
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

    public static class LevelBuilder {

        // dimensions
        private int width = 50;
        private int height = 30;

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

        public LevelBuilder width(int width) {
            this.width = width;
            return this;
        }

        public LevelBuilder height(int height) {
            this.height = height;
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
            var level = new Level(RoomMapGeneratorV2.generate(width, height));

            // spawn mobs
            for (Room room : level.getMap().getRooms()) {
                int mobsCountInRoom = RANDOM.nextInt(minMobsPerRoom, maxMobsPerRoom + 1);
                while (mobsCountInRoom-- > 0) {
                    var pos = room.getRandomPosition();
                    if (level.getEntityAt(pos, null).isEmpty()) {
                        Entity mob = mobGenerateFn.get();
                        mob.addFeature(FeatureType.POSITION, new Position(pos, 0, 10));
                        level.addEntity(mob);
                    }
                }
            }

            // spawn items
            for (Room room : level.getMap().getRooms()) {
                int maxItemsInRoom = RANDOM.nextInt(minItemsPerRoom, maxItemsPerRoom + 1);
                while (maxItemsInRoom-- > 0) {
                    var pos = room.getRandomPosition();
                    if (level.getEntityAt(pos, null).isEmpty()) {
                        Entity item = itemGenerateFn.get();
                        item.addFeature(FeatureType.POSITION, new Position(pos, 0, 10));
                        level.addEntity(item);
                    }
                }
            }

            // put doors
            var map = level.getMap();
            Collections.shuffle(map.getRooms());
            int minDoors = Math.round(map.getRooms().size() * minDoorPercentage);
            int maxDoors = Math.round(map.getRooms().size() * maxDoorPercentage);
            for (Room room : map.getRooms().subList(0, RANDOM.nextInt(minDoors, maxDoors))) {
                var possibleDoors = findPossibleDoorPositions(level, room);
                Collections.shuffle(possibleDoors);
                if (!possibleDoors.isEmpty()) {
                    var doorInRoom = RANDOM.nextInt(1, possibleDoors.size() + 1);
                    for (int i = 0; i < doorInRoom; i++) {
                        var possiblePos = possibleDoors.get(i);
                        if (level.getEntityAt(possiblePos.pos, null).isEmpty()) {
                            var door = EntityFactory.createDoor(RANDOM.nextFloat() < openedDoorPercentage);
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
    }
}
