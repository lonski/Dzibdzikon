package pl.lonski.dzibdzikon.map;

import java.util.ArrayList;
import java.util.List;
import pl.lonski.dzibdzikon.Point;

public class Line {

    public static List<Point> calculate(Point start, Point end) {
        return calculate(start.x(), end.x(), start.y(), end.y());
    }

    public static List<Point> calculate(int x1, int x2, int y1, int y2) {
        var points = new ArrayList<Point>();
        points.add(new Point(x1, y1));

        var dx = Math.abs(x2 - x1);
        var dy = Math.abs(y2 - y1);
        var xi = x1 < x2 ? 1 : -1;
        var yi = y1 < y2 ? 1 : -1;
        var err = dx - dy;

        var end = new Point(x2, y2);

        var cx = x1;
        var cy = y1;

        while (!new Point(cx, cy).equals(end)) {
            var lastErr = err;
            if (lastErr > -dx / 2f) {
                err -= dy;
                cx += xi;
            }

            if (lastErr < dx / 2f) {
                err += dx;
                cy += yi;
            }
            points.add(new Point(cx, cy));
        }

        return points;
    }

    public static List<Point> calculateStrightOrDiagonal(Point start, Point end, boolean includeStart) {

        var dx = Math.abs(end.x() - start.x());
        var dy = Math.abs(end.y() - start.y());

        if (dx != 0 && dy != 0 && dx != dy) {
            return new ArrayList<>();
        }

        var points = new ArrayList<Point>();

        var cur = new Point(start.x(), start.y());
        if (includeStart) {
            points.add(cur);
        }

        var xi = start.x() < end.x() ? 1 : -1;
        var yi = start.y() < end.y() ? 1 : -1;
        if (dx == 0) {
            xi = 0;
        }
        if (dy == 0) {
            yi = 0;
        }

        while (!cur.equals(end)) {
            cur = new Point(cur.x() + xi, cur.y() + yi);
            points.add(cur);
        }

        return points;
    }
}
