package pl.lonski.dzibdzikon.spell;

import com.badlogic.gdx.graphics.Color;
import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.PositionUtils;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.DieAction;
import pl.lonski.dzibdzikon.action.targeting.TargetingMode;
import pl.lonski.dzibdzikon.animation.Animation;
import pl.lonski.dzibdzikon.animation.BurnAnimation;
import pl.lonski.dzibdzikon.animation.ChainAnimation;
import pl.lonski.dzibdzikon.animation.TextFlowUpAnimation;
import pl.lonski.dzibdzikon.animation.ThrowAnimation;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Attackable;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.map.TextureId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class Fireball implements Spell {

    private int range = 3;

    @Override
    public String getName() {
        return "Kula ognia";
    }

    @Override
    public String getDescription() {
        return "Wystrzeliwuje kulę ognia, która po uderzeniu w przeszkodę wybucha raniąc wszystkich przeciwników w promieniu 3 kafli.";
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
        var targets = new HashSet<Entity>();
        PositionUtils.inCircleOf(range, circlePoint -> world.getCurrentLevel()
                .getEntityAt(target.add(circlePoint), FeatureType.ATTACKABLE)
                .ifPresent(targets::add));

        for (Entity targetEntity : targets) {
            var targetAttackable = targetEntity.<Attackable>getFeature(FeatureType.ATTACKABLE);
            var targetPos =
                    targetEntity.<Position>getFeature(FeatureType.POSITION).getCoords();

            var damage = Dzibdzikon.RANDOM.nextInt(4, 12);

            targetAttackable.setHp(targetAttackable.getHp() - damage);
            targetEntity.addAnimation(new TextFlowUpAnimation("-" + damage, targetPos, Color.SCARLET));
            if (targetAttackable.getHp() <= 0) {
                targetEntity.takeAction(new DieAction(targetEntity));
            }
        }
    }

    public Optional<Animation> getAnimation(Point startPosPix, Point targetPix) {
        var points = new ArrayList<Point>();
        PositionUtils.inCircleOf(
                range, circlePoint -> points.add(targetPix.toCoords().add(circlePoint)));

        return Optional.of(new ChainAnimation(List.of(
                new ThrowAnimation(TextureId.SPELL_EFFECT_FIREBALL, startPosPix, targetPix, 6),
                new BurnAnimation(points))));
    }
}
