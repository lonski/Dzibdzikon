package pl.lonski.dzibdzikon.animation;

import java.util.ArrayList;
import java.util.List;
import pl.lonski.dzibdzikon.World;

public class ChainAnimation extends BaseAnimation {

    private final List<Animation> animations;
    private Animation currentAnimation;
    private boolean done = false;

    public ChainAnimation(List<Animation> animations) {
        this.animations = new ArrayList<>(animations);
    }

    @Override
    public void update(float delta, World world) {
        if (animations.isEmpty() && (currentAnimation == null || currentAnimation.isDone())) {
            finish();
            return;
        }

        if (currentAnimation == null || currentAnimation.isDone()) {
            currentAnimation = animations.remove(0);
        }

        currentAnimation.update(delta, world);
    }

    @Override
    public void render() {
        if (!done && currentAnimation != null && !currentAnimation.isDone()) {
            currentAnimation.render();
        }
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void finish() {
        done = true;
    }
}
