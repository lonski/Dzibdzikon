package pl.lonski.dzibdzikon.spell;

import java.util.Optional;
import pl.lonski.dzibdzikon.DzibdziRandom;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.targeting.TargetingMode;
import pl.lonski.dzibdzikon.animation.Animation;
import pl.lonski.dzibdzikon.animation.ThrowAnimation;
import pl.lonski.dzibdzikon.effect.DamageEffect;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.map.TextureId;

public class SpikeSpell extends SpellBase {

    public SpikeSpell() {
        super(1);
    }

    @Override
    public String getName() {
        return "Kolec";
    }

    @Override
    public SpellDescription getDescription() {
        return new SpellDescription(
                "Wystrzeliwuje ostry, magiczny, kolec we wskazanym kierunku.",
                "wróg",
                "pojedynczy wróg",
                mpCost + " MP");
    }

    @Override
    public TextureId getIcon() {
        return TextureId.SPELL_SPIKE;
    }

    @Override
    public TargetingMode getTargetingMode() {
        return TargetingMode.SINGLE_ATTACKABLE;
    }

    @Override
    public void cast(World world, Entity caster, Point target) {
        var targetEntityOpt = world.getCurrentLevel().getEntityAt(target, FeatureType.ATTACKABLE);
        if (targetEntityOpt.isEmpty()) {
            return;
        }

        var targetEntity = targetEntityOpt.get();
        var damage = DzibdziRandom.nextInt(2, 6);
        targetEntity.applyEffect(new DamageEffect(damage));

        consumeResources(caster);
    }

    public Optional<Animation> getAnimation(Point startPosPix, Point targetPix) {
        return Optional.of(new ThrowAnimation(TextureId.SPELL_EFFECT_SPIKE, startPosPix, targetPix, 6));
    }
}
