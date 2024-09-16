package pl.lonski.dzibdzikon.map;

import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;

public class Room {

    protected int x;
    protected int y;
    protected int w;
    protected int h;

    public Room(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public boolean canPlace(TileGrid tileGrid) {
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
}
