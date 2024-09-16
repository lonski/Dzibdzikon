package pl.lonski.dzibdzikon.map;

import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.PositionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CircleRoom extends Room {

    private List<Point> points = new ArrayList<>();

    public CircleRoom(int x, int y, int w, int h) {
        super(x, y, w, h);
        PositionUtils.inFilledEllipseOf(h, w, p -> points.add(p.add(new Point(x, y))));

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
        return points.get(Dzibdzikon.RANDOM.nextInt(0, points.size()));
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

        return true;
    }

    @Override
    public void put(TileGrid map, TextureId floor) {
        for (Point point : points) {
            map.setTile(point, floor);
        }
        map.addRoom(this);
    }
}
