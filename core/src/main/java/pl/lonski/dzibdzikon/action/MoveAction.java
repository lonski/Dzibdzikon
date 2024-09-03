package pl.lonski.dzibdzikon.action;

import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;

import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Position;

public class MoveAction implements Action {

    private static final float MOVE_TIME = 0.01f;
    private static final int MOVE_SPEED = 4;

    private final Entity entity;
    private final Point target;
    private boolean done = false;
    private float time = 0;

    public MoveAction(Entity entity, Point target) {
        this.entity = entity;
        this.target = target;
    }

    @Override
    public void update(float delta) {
        time += delta;
        if (time >= MOVE_TIME) {
            time = 0;
            Position pos = entity.getFeature(FeatureType.POSITION);
            Point posDiff = getRenderPositionDiff(pos.getRenderPosition());
            pos.setRenderPosition(new Point(pos.getRenderPosition().x() + posDiff.x(),
                pos.getRenderPosition().y() + posDiff.y()));

            if (pos.getRenderPosition().equals(new Point(target.x() * TILE_WIDTH, target.y() * TILE_HEIGHT))) {
                done = true;
                pos.setCoords(target);
            }
        }
    }

    private Point getRenderPositionDiff(Point currentPos) {
        int dx = 0;
        int dy = 0;

        if (currentPos.x() < target.x() * TILE_WIDTH) {
            dx = Math.min(MOVE_SPEED, target.x() * TILE_WIDTH - currentPos.x());
        } else if (currentPos.x() > target.x() * TILE_WIDTH) {
            dx = Math.max(-MOVE_SPEED, target.x() * TILE_WIDTH - currentPos.x());
        }

        if (currentPos.y() < target.y() * TILE_HEIGHT) {
            dy = Math.min(MOVE_SPEED, target.y() * TILE_HEIGHT - currentPos.y());
        } else if (currentPos.y() > target.y() * TILE_HEIGHT) {
            dy = Math.max(-MOVE_SPEED, target.y() * TILE_HEIGHT - currentPos.y());
        }

        return new Point(dx, dy);
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
