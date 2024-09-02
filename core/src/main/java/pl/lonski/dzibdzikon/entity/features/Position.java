package pl.lonski.dzibdzikon.entity.features;


import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;

import pl.lonski.dzibdzikon.Point;

public class Position implements EntityFeature {

    private Point coords;
    private Point renderPosition;

    public Position(Point pos) {
        this.coords = pos;
        this.renderPosition = new Point(pos.x() * TILE_WIDTH, pos.y() * TILE_HEIGHT);
    }

    public Point getCoords() {
        return coords;
    }

    public void setCoords(Point coords) {
        this.coords = coords;
        this.renderPosition = new Point(coords.x() * TILE_WIDTH, coords.y() * TILE_HEIGHT);
    }

    public void setRenderPosition(Point renderPosition) {
        this.renderPosition = renderPosition;
    }

    public Point getRenderPosition() {
        return renderPosition;
    }

    public void move(Point dpos) {
        setCoords(new Point(coords.x() + dpos.x(), coords.y() + dpos.y()));
    }
}
