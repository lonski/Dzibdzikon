package pl.lonski.dzibdzikon.action;

import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;

public class AttackAnimationAction implements Action {

    private final MoveAnimationAction moveAnimation;

    public AttackAnimationAction(Entity entity, Point target) {
        var myPos = entity.getPosition();
        var diff = target.sub(myPos.getCoords());
        var targetRenderPos = new Point(
                myPos.getRenderPosition().x() + diff.x() * Dzibdzikon.TILE_WIDTH / 2,
                myPos.getRenderPosition().y() + diff.y() * Dzibdzikon.TILE_HEIGHT / 2);
        this.moveAnimation = new MoveAnimationAction(entity, new Point(target), targetRenderPos);
        this.moveAnimation.setMoveSpeed(2);
        this.moveAnimation.setBackToOriginalPosition(true);
    }

    @Override
    public void update(float delta, World world) {
        moveAnimation.update(delta, world);
    }

    @Override
    public boolean isDone() {
        return moveAnimation.isDone();
    }
}
