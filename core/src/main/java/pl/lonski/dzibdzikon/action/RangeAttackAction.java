package pl.lonski.dzibdzikon.action;

import com.badlogic.gdx.graphics.Color;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.animation.Animation;
import pl.lonski.dzibdzikon.animation.TextFlowUpAnimation;
import pl.lonski.dzibdzikon.animation.ThrowAnimation;
import pl.lonski.dzibdzikon.effect.DamageEffect;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Attackable;
import pl.lonski.dzibdzikon.entity.features.RangeAttackable;

public class RangeAttackAction implements Action {

    private final Entity attacker;
    private final Entity target;
    private boolean done = false;
    private final Animation animation;

    public RangeAttackAction(Entity attacker, Entity target) {
        this.attacker = attacker;
        this.target = target;

        var ammoTex = attacker.<RangeAttackable>getFeature(FeatureType.RANGE_ATTACKABLE)
                .getAmmo();
        var myPosPix = attacker.getPosition().getRenderPosition();
        var targetPosPix = target.getPosition().getRenderPosition();
        this.animation = new ThrowAnimation(ammoTex, myPosPix, targetPosPix);
        attacker.addAnimation(this.animation);
    }

    @Override
    public void update(float delta, World world) {
        if (animation != null) {
            animation.update(delta, world);
        }
        if (animation == null || animation.isDone()) {
            doRangeAttack(world);
            done = true;
        }
    }

    private void doRangeAttack(World world) {
        RangeAttackable attacking = attacker.getFeature(FeatureType.RANGE_ATTACKABLE);
        Attackable defending = target.getFeature(FeatureType.ATTACKABLE);
        var targetPos = target.getPosition().getCoords();
        var result = attacking.attack(defending);

        if (result.hit()) {
            target.applyEffect(new DamageEffect(result.damage()));
        } else {
            target.addAnimation(new TextFlowUpAnimation("unik", targetPos, Color.YELLOW));
        }
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
