package pl.lonski.dzibdzikon.spell;

import com.badlogic.gdx.graphics.Color;
import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.DieAction;
import pl.lonski.dzibdzikon.animation.TextFlowUpAnimation;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Attackable;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.map.TextureId;
import pl.lonski.dzibdzikon.action.targeting.TargetingMode;

public class SpikeSpell implements Spell {
    @Override
    public String getName() {
        return "Kolec";
    }

    @Override
    public String getDescription() {
        return "Wystrzeliwuje ostry, magiczny, kolec we wskazanym kierunku.";
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
        var targetAttackable = targetEntity.<Attackable>getFeature(FeatureType.ATTACKABLE);
        var targetPos = targetEntity.<Position>getFeature(FeatureType.POSITION).getCoords();

        var damage = Dzibdzikon.RANDOM.nextInt(2, 6);

        targetAttackable.setHp(targetAttackable.getHp() - damage);
        targetEntity.addAnimation(new TextFlowUpAnimation("-" + damage, targetPos, Color.SCARLET));
        if (targetAttackable.getHp() <= 0) {
            targetEntity.takeAction(new DieAction(targetEntity));
        }
    }
}
