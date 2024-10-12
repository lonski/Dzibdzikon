package pl.lonski.dzibdzikon.spell;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import pl.lonski.dzibdzikon.DzibdziRandom;
import pl.lonski.dzibdzikon.ExplosionSimulator;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.targeting.TargetingMode;
import pl.lonski.dzibdzikon.animation.Animation;
import pl.lonski.dzibdzikon.animation.ChainAnimation;
import pl.lonski.dzibdzikon.animation.CircleExplodeAnimation;
import pl.lonski.dzibdzikon.animation.ThrowAnimation;
import pl.lonski.dzibdzikon.effect.DamageEffect;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.map.TextureId;

public class Fireball extends SpellBase {

    private final int range = 3;

    public Fireball() {
        super(10);
    }

    @Override
    public String getName() {
        return "Kula ognia";
    }

    @Override
    public SpellDescription getDescription() {
        return new SpellDescription(
                "Wystrzeliwuje kulę ognia, która po uderzeniu w przeszkodę wybucha raniąc wszystkich przeciwników w promieniu 3 kafli.",
                "punkt",
                "okrąg o promieniu 3 kafli",
                mpCost + " MP");
    }

    @Override
    public TextureId getIcon() {
        return TextureId.SPELL_FIREBALL;
    }

    @Override
    public TargetingMode getTargetingMode() {
        return TargetingMode.COORDS;
    }

    @Override
    public void cast(World world, Entity caster, Point target) {
        var targetPoints = ExplosionSimulator.simulate(target, range, world);
        var targets = world.getCurrentLevel().getEntitiesAt(new HashSet<>(targetPoints), FeatureType.ATTACKABLE);

        for (Entity targetEntity : targets) {
            var damage = DzibdziRandom.nextInt(4, 12);
            targetEntity.applyEffect(new DamageEffect(damage));
        }
        consumeResources(caster);
    }

    public Optional<Animation> getAnimation(Point startPosPix, Point targetPix) {
        return Optional.of(new ChainAnimation(List.of(
                new ThrowAnimation(TextureId.SPELL_EFFECT_FIREBALL, startPosPix, targetPix, 6),
                new CircleExplodeAnimation(targetPix.toCoords(), range))));
    }
}
