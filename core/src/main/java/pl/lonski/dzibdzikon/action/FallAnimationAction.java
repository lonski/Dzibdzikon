package pl.lonski.dzibdzikon.action;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.animation.FallAnimation;
import pl.lonski.dzibdzikon.map.Position;

public class FallAnimationAction implements Action {

    private final FallAnimation animation;

    public FallAnimationAction(Position entityPos) {
        this.animation = new FallAnimation(entityPos);
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
