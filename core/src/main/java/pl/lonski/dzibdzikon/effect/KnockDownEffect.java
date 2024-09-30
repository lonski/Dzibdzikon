package pl.lonski.dzibdzikon.effect;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.animation.FallAnimation;
import pl.lonski.dzibdzikon.animation.UnFallAnimation;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Position;

public class KnockDownEffect implements Effect {

    private int turnsLeft;

    public KnockDownEffect(int turns) {
        turnsLeft = turns + 1;
    }

    @Override
    public void apply(Entity target) {
        var pos = target.<Position>getFeature(FeatureType.POSITION);
        if (pos == null) {
            return;
        }

        target.addAnimation(new FallAnimation(target));
    }

    @Override
    public void remove(Entity target) {
        target.addAnimation(new UnFallAnimation(target, 0));
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
}
