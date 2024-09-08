package pl.lonski.dzibdzikon.action;

import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;

import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Position;

public class MoveAnimationAction implements Action {

    private float moveTime = 0.01f;
    private int moveSpeed = 3;

    private final Entity entity;
    private final Point targetRenderPosition;
    private final Point targetCoords;
    private boolean done = false;
    private float time = 0;
    private boolean backToOriginalPosition = false;
    private final Point originalPosition;

    public MoveAnimationAction(Entity entity, Point targetCoords) {
        this(entity, targetCoords, new Point(targetCoords.x() * TILE_WIDTH, targetCoords.y() * TILE_HEIGHT));
    }

    public MoveAnimationAction(Entity entity, Point targetCoords, Point targetRenderPosition) {
        this.entity = entity;
        this.targetCoords = targetCoords;
        this.targetRenderPosition = targetRenderPosition;
        this.originalPosition = new Point(entity.<Position>getFeature(FeatureType.POSITION).getCoords());
    }

    public void setMoveTime(float moveTime) {
        this.moveTime = moveTime;
    }

    public void setMoveSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    @Override
    public void update(float delta, World world) {
        Position pos = entity.getFeature(FeatureType.POSITION);

        // move instantly if not in player view
        if (!world.getCurrentLevel().getVisible().contains(targetCoords) && !world.getCurrentLevel().getVisible().contains(pos.getCoords())) {
            finishMove();
            return;
        }

        time += delta;
        if (time >= moveTime) {
            time = 0;
            Point posDiff = getRenderPositionDiff(pos.getRenderPosition());
            pos.setRenderPosition(new Point(pos.getRenderPosition().x() + posDiff.x(),
                pos.getRenderPosition().y() + posDiff.y()));

            if (pos.getRenderPosition().equals(new Point(targetRenderPosition.x(), targetRenderPosition.y()))) {
                finishMove();
            }
        }
    }

    private void finishMove() {
        if (backToOriginalPosition) {
            entity.<Position>getFeature(FeatureType.POSITION).setCoords(originalPosition);
        }
        done = true;
    }

    private Point getRenderPositionDiff(Point currentPos) {
        int dx = 0;
        int dy = 0;

        if (currentPos.x() < targetRenderPosition.x()) {
            dx = Math.min(moveSpeed, targetRenderPosition.x() - currentPos.x());
        } else if (currentPos.x() > targetRenderPosition.x()) {
            dx = Math.max(-moveSpeed, targetRenderPosition.x() - currentPos.x());
        }

        if (currentPos.y() < targetRenderPosition.y()) {
            dy = Math.min(moveSpeed, targetRenderPosition.y() - currentPos.y());
        } else if (currentPos.y() > targetRenderPosition.y()) {
            dy = Math.max(-moveSpeed, targetRenderPosition.y() - currentPos.y());
        }

        return new Point(dx, dy);
    }

    @Override
    public boolean isDone() {
        return done;
    }

    public void setBackToOriginalPosition(boolean backToOriginalPosition) {
        this.backToOriginalPosition = backToOriginalPosition;
    }
}
