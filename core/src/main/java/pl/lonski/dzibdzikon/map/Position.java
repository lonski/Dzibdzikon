package pl.lonski.dzibdzikon.map;

import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;

import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.entity.features.EntityFeature;

public class Position implements EntityFeature {

    private Point coords;
    private Point renderPosition;
    private float rotation = 0;

    private int zLevel = 0;

    public Position() {
        this(new Point(0, 0), 0, 0);
    }

    public Position(Point pos) {
        this(pos, 0, 0);
    }

    public Position(Point pos, float rotation, int zLevel) {
        this.zLevel = zLevel;
        this.coords = pos;
        this.rotation = rotation;
        this.renderPosition = new Point(pos.x() * TILE_WIDTH, pos.y() * TILE_HEIGHT);
    }

    public int getzLevel() {
        return zLevel;
    }

    public void setzLevel(int zLevel) {
        this.zLevel = zLevel;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public Point getCoords() {
        return coords;
    }

    void setCoords(Point coords) {
        this.coords = coords;
        this.renderPosition = new Point(coords.x() * TILE_WIDTH, coords.y() * TILE_HEIGHT);
    }

    public void setRenderPosition(Point renderPosition) {
        this.renderPosition = renderPosition;
    }

    public Point getRenderPosition() {
        return renderPosition;
    }
}
