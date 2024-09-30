package pl.lonski.dzibdzikon.action;

import com.badlogic.gdx.graphics.Color;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.animation.TextFlowUpAnimation;
import pl.lonski.dzibdzikon.effect.DamageEffect;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Attackable;
import pl.lonski.dzibdzikon.entity.features.Position;

public class AttackAction implements Action {

    private final Entity attacker;
    private final Entity target;
    private boolean done = false;
    private final AttackAnimationAction animation;

    public AttackAction(Entity attacker, Entity target) {
        this(attacker, target, true);
    }

    public AttackAction(Entity attacker, Entity target, boolean withAnimation) {
        this.attacker = attacker;
        this.target = target;
        var targetCoords = target.<Position>getFeature(FeatureType.POSITION).getCoords();
        if (withAnimation) {
            this.animation = new AttackAnimationAction(attacker, targetCoords);
        } else {
            this.animation = null;
        }
    }

    @Override
    public void update(float delta, World world) {
        if (animation != null) {
            animation.update(delta, world);
        }
        if (animation == null || animation.isDone()) {
            doFight(world);
            done = true;
        }
    }

    private void doFight(World world) {
        Attackable attacking = attacker.getFeature(FeatureType.ATTACKABLE);
        Attackable defending = target.getFeature(FeatureType.ATTACKABLE);
        var targetPos = target.<Position>getFeature(FeatureType.POSITION).getCoords();

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
