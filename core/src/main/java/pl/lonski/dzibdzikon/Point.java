package pl.lonski.dzibdzikon;

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
}

