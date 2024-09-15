package pl.lonski.dzibdzikon.action;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.animation.FallAnimation;
import pl.lonski.dzibdzikon.entity.Entity;

public class FallAnimationAction implements Action {

    private FallAnimation animation;

    public FallAnimationAction(Entity entity) {
        entity.clearAnimations();
        this.animation = new FallAnimation(entity);
    }

    @Override
    public void update(float delta, World world) {
        animation.update(delta, world);
    }

    @Override
    public boolean isDone() {
        return animation.isDone();
    }
}
