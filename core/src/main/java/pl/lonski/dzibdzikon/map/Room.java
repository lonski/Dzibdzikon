package pl.lonski.dzibdzikon.map;

import pl.lonski.dzibdzikon.Point;

public record Room(int x, int y, int w, int h) {

    public Point getCenter() {
        return new Point(x + w / 2, y + h / 2);
    }
}
