package pl.lonski.dzibdzikon;

import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;

public record Point(int x, int y) {

    public boolean isZero() {
        return x == 0 && y == 0;
    }

    public Point(Point p) {
        this(p.x, p.y);
    }

    public Point sub(Point other) {
        return new Point(x - other.x, y - other.y);
    }

    public Point add(Point other) {
        return new Point(x + other.x, y + other.y);
    }

    public Point toPixels() {
        return new Point(x * TILE_WIDTH, y * Dzibdzikon.TILE_HEIGHT);
    }

    public Point toCoords() {
        return new Point(Math.round((float) x / (float) TILE_WIDTH), Math.round((float) y / (float) TILE_HEIGHT));
    }
}
