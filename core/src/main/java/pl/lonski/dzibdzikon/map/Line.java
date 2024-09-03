package pl.lonski.dzibdzikon.map;

import java.util.ArrayList;
import java.util.List;
import pl.lonski.dzibdzikon.Point;

public class Line {

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
}
