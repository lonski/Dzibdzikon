package pl.lonski.dzibdzikon.effect;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.animation.FallAnimation;
import pl.lonski.dzibdzikon.animation.ZeroifyRotationAnimation;
import pl.lonski.dzibdzikon.entity.Entity;

public class KnockDownEffect implements Effect {

    private int turnsLeft;

    public KnockDownEffect(int turns) {
        turnsLeft = turns + 1;
    }

    @Override
    public void apply(Entity target) {
        target.addAnimation(new FallAnimation(target.getPosition()));
    }

    @Override
    public void remove(Entity target) {
        target.finishAllAnimation();
        target.addAnimation(new ZeroifyRotationAnimation(target));
    }

    @Override
    public boolean isActive() {
        return turnsLeft > 0;
    }

    @Override
    public void takeTurn(World world, Entity target) {
        turnsLeft -= 1;
    }

    @Override
    public boolean blockEntityActingPossibility() {
        return true;
    }

    @Override
    public boolean stackable() {
        return false;
    }
}
