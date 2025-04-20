package pl.lonski.dzibdzikon.action;

import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.animation.Animation;
import pl.lonski.dzibdzikon.animation.ThrowAnimation;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.SpellEffect;
import pl.lonski.dzibdzikon.map.Position;

public class ThrowAction implements Action {

    private final Entity thrower;
    private final Position throwerPos;
    private final Entity throwItem;
    private final Point target;
    private final Animation throwAnimation;
    private Animation effectAnimation;
    private boolean done = false;

    public ThrowAction(Entity thrower, Entity throwItem, Point target) {
        this.thrower = thrower;
        this.throwItem = throwItem;
        this.target = target;
        this.throwerPos = thrower.getPosition();
        var myPosPix = throwerPos.getRenderPosition();
        this.throwAnimation = new ThrowAnimation(throwItem.getGlyph(), myPosPix, target.toPixels());
        thrower.addAnimation(throwAnimation);
    }

    @Override
    public void update(float delta, World world) {
        if (throwAnimation != null) {
            throwAnimation.update(delta, world);
        }

        if (throwAnimation == null || throwAnimation.isDone()) {

            if (effectAnimation == null) {
                activateEffectAnimation(world);
            }

            if (effectAnimation != null) {
                effectAnimation.update(delta, world);
            }

            if (effectAnimation == null || effectAnimation.isDone()) {
                executeEffect(world);
                done = true;
            }
        }
    }

    private void executeEffect(World world) {
        var spellEffect = throwItem.<SpellEffect>getFeature(FeatureType.SPELL_EFFECT);
        if (spellEffect == null) {
            return;
        }

        var spell = spellEffect.getSpell();
        spell.cast(world, thrower, target);
    }

    private void activateEffectAnimation(World world) {
        var spellEffect = throwItem.<SpellEffect>getFeature(FeatureType.SPELL_EFFECT);
        if (spellEffect == null) {
            return;
        }

        var spell = spellEffect.getSpell();
        var myPosPix = throwerPos.getRenderPosition();
        spell.getAnimation(myPosPix, target.toPixels()).ifPresent(a -> {
            this.effectAnimation = a;
            thrower.addAnimation(a);
        });
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
