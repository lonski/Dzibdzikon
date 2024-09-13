package pl.lonski.dzibdzikon.map;

import pl.lonski.dzibdzikon.DzibdziRandom;
import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.PositionUtils;

import java.util.ArrayList;
import java.util.Comparator;

public class CircleRoom extends Room {

    public CircleRoom(int x, int y, int w, int h) {
        super(x, y, w, h);

        final int radius = Math.max(w, h);

        var circlePoints = PositionUtils.inFilledCircleWithPerimeterOf(radius);
        points.clear();
        perimeterPoints.clear();
        circlePoints.points().stream().map(p -> p.add(new Point(x, y))).forEach(points::add);
        circlePoints.perimeter().stream().map(p -> p.add(new Point(x, y))).forEach(perimeterPoints::add);

        this.w = points.stream().map(Point::x).max(Comparator.naturalOrder()).get()
                - points.stream().map(Point::x).min(Comparator.naturalOrder()).get();
        this.h = points.stream().map(Point::y).max(Comparator.naturalOrder()).get()
                - points.stream().map(Point::y).min(Comparator.naturalOrder()).get();
    }

    @Override
    public Point getCenter() {
        return new Point(x, y);
    }

    @Override
    public Point getRandomPosition() {
        return new ArrayList<>(points).get(DzibdziRandom.nextInt(0, points.size()));
    }

    @Override
    public boolean canPlace(TileGrid tileGrid) {
        for (Point point : points) {
            if (!tileGrid.inBoundsWithFrame(point)) {
                return false;
            }
            if (tileGrid.getTile(point).isFloor()) {
                return false;
            }
        }

        for (Point point : perimeterPoints) {
            if (!tileGrid.inBoundsWithFrame(point)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void put(TileGrid map, TextureId floor) {
        for (Point point : points) {
            map.setTile(point, floor);
        }
        map.addRoom(this);
    }

    public RoomType getRoomType() {
        return RoomType.CIRCLE;
    }
}
