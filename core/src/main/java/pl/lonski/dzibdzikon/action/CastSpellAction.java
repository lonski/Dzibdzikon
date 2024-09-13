package pl.lonski.dzibdzikon.action;

import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.animation.ThrowAnimation;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.map.TextureId;
import pl.lonski.dzibdzikon.spell.Spell;

public class CastSpellAction implements Action {

    private boolean done = false;
    private final Entity caster;
    private final Point target;
    private final Spell spell;

    private final ThrowAnimation animation;

    public CastSpellAction(Entity caster, Point target, Spell spell) {
        this.caster = caster;
        this.target = target;
        this.spell = spell;

        var pos = caster.<Position>getFeature(FeatureType.POSITION);
        this.animation = new ThrowAnimation(TextureId.SPELL_EFFECT_SPIKE, pos.getRenderPosition(), target.toPixels(), 6);
        caster.addAnimation(animation);
    }

    @Override
    public void update(float delta, World world) {
        if (animation != null && !animation.isDone()) {
            animation.update(delta, world);
            return;
        }

        spell.cast(world, caster, target);
        done = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
