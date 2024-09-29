package pl.lonski.dzibdzikon.map;

import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.entity.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Room {

    protected int x;
    protected int y;
    protected int w;
    protected int h;
    protected Set<Point> perimeterPoints = new HashSet<>();
    protected Set<Point> points = new HashSet<>();

    public Room(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        for (int i = -1; i <= w; i++) {
            perimeterPoints.add(new Point(x + i, y - 1));
            perimeterPoints.add(new Point(x + i, y + h));
        }
        for (int j = -1; j <= h; j++) {
            perimeterPoints.add(new Point(x - 1, y + j));
            perimeterPoints.add(new Point(x + w, y + j));
        }

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                points.add(new Point(x + i, y + j));
            }
        }
    }

    public boolean acceptsEntity(Entity entity) {
        return true;
    }

    public boolean canPlace(TileGrid tileGrid) {
        for (Point perimeterPoint : perimeterPoints) {
            if (!tileGrid.inBoundsWithFrame(perimeterPoint)) {
                return false;
            }
        }

        for (int ry = -1; ry <= height(); ry++) {
            for (int rx = -1; rx <= width(); rx++) {
                var pos = new Point(x() + rx, y() + ry);
                if (!tileGrid.inBounds(pos.x(), pos.y())
                        || !tileGrid.getTile(pos.x(), pos.y()).isWall()) {
                    return false;
                }
            }
        }

        return true;
    }

    public void put(TileGrid map, TextureId floor) {
        for (int ry = 0; ry < height(); ry++) {
            for (int rx = 0; rx < width(); rx++) {
                map.setTile(rx + x(), ry + y(), floor);
            }
        }

        map.addRoom(this);
    }

    public Point getCenter() {
        return new Point(x + w / 2, y + h / 2);
    }

    public Point getRandomPosition() {
        return new Point(
                x + (int) (Dzibdzikon.RANDOM.nextDouble() * w), y + (int) (Dzibdzikon.RANDOM.nextDouble() * h));
    }

    public Point getRandomNonCornerPerimeterPosition() {
        var p = new Point(new ArrayList<>(perimeterPoints).get(Dzibdzikon.RANDOM.nextInt(perimeterPoints.size())));
        int maxTries = 5;
        while (isPerimeterCorner(p) && maxTries-- > 0) {
            p = new Point(new ArrayList<>(perimeterPoints).get(Dzibdzikon.RANDOM.nextInt(perimeterPoints.size())));
        }

        return p;
    }

    private boolean isPerimeterCorner(Point p) {
        if (p.x() == x - 1 && p.y() == y - 1) {
            return true;
        }

        if (p.x() == x - 1 && p.y() == y + h) {
            return true;
        }

        if (p.x() == x + w && p.y() == y - 1) {
            return true;
        }

        if (p.x() == x + w && p.y() == y + h) {
            return true;
        }

        return false;
    }

    public int x() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int y() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int width() {
        return w;
    }

    public void setWidth(int w) {
        this.w = w;
    }

    public int height() {
        return h;
    }

    public void setHeight(int h) {
        this.h = h;
    }

    public RoomType getRoomType() {
        return RoomType.RECT;
    }

    public void afterMapGenerated(TileGrid map) {}

    public List<Point> getPerimeter() {
        return new ArrayList<>(perimeterPoints);
    }

    public List<Point> getEntrances(TileGrid map) {
        List<Point> entrances = new ArrayList<>();

        for (Point perimeterPoint : perimeterPoints) {
            if (!map.getTile(perimeterPoint).isFloor()) {
                continue;
            }

            if (map.horizontalNeighboursAreWalls(perimeterPoint) && map.verticalNeighboursAreFloors(perimeterPoint)) {
                entrances.add(new Point(perimeterPoint));
                continue;
            }

            if (map.horizontalNeighboursAreFloors(perimeterPoint) && map.verticalNeighboursAreWalls(perimeterPoint)) {
                entrances.add(new Point(perimeterPoint));
            }
        }

        //        for (int rx = 0; rx < width(); rx++) {
        //            int x = x() + rx;
        //
        //            // top wall
        //            int y = y() - 1;
        //            if (map.inBounds(x, y)
        //                    && map.getTile(x, y).isFloor()
        //                    && map.horizontalNeighboursAreWalls(x, y)
        //                    && map.verticalNeighboursAreFloors(x, y)) {
        //                entrances.add(new Point(x, y));
        //            }
        //
        //            // bottom wall
        //            y = y() + height();
        //            if (map.inBounds(x, y)
        //                    && map.getTile(x, y).isFloor()
        //                    && map.horizontalNeighboursAreWalls(x, y)
        //                    && map.verticalNeighboursAreFloors(x, y)) {
        //                entrances.add(new Point(x, y));
        //            }
        //        }
        //
        //        for (int ry = 0; ry < height(); ry++) {
        //            int y = y() + ry;
        //
        //            // left wall
        //            int x = x() - 1;
        //            if (map.inBounds(x, y)
        //                    && map.getTile(x, y).isFloor()
        //                    && map.verticalNeighboursAreWalls(x, y)
        //                    && map.horizontalNeighboursAreFloors(x, y)) {
        //                entrances.add(new Point(x, y));
        //            }
        //
        //            // right wall
        //            x = x() + width();
        //            if (map.inBounds(x, y)
        //                    && map.getTile(x, y).isFloor()
        //                    && map.verticalNeighboursAreWalls(x, y)
        //                    && map.horizontalNeighboursAreFloors(x, y)) {
        //                entrances.add(new Point(x, y));
        //            }
        //        }

        return entrances;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;
        return x == room.x
                && y == room.y
                && w == room.w
                && h == room.h
                && Objects.equals(perimeterPoints, room.perimeterPoints)
                && Objects.equals(points, room.points);
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + w;
        result = 31 * result + h;
        result = 31 * result + Objects.hashCode(perimeterPoints);
        result = 31 * result + Objects.hashCode(points);
        return result;
    }
}
