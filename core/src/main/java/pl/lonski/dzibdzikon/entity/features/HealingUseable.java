package pl.lonski.dzibdzikon.entity.features;

import com.badlogic.gdx.graphics.Color;
import pl.lonski.dzibdzikon.DzibdziRandom;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.animation.TextFlowUpAnimation;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;

public class HealingUseable extends Useable {

    private final int minHeal;
    private final int maxHeal;

    public HealingUseable(int minHeal, int maxHeal) {
        this.minHeal = minHeal;
        this.maxHeal = maxHeal;
    }

    @Override
    public void use(Entity user, Entity target, World world) {
        var heal = DzibdziRandom.nextInt(minHeal, maxHeal + 1);
        var attackable = target.<Attackable>getFeature(FeatureType.ATTACKABLE);
        var pos = target.getPosition();
        if (attackable != null) {
            var newHp = Math.min(attackable.getHp() + heal, attackable.getMaxHp());
            var diff = newHp - attackable.getHp();
            if (diff > 0) {
                attackable.setHp(newHp);
                target.addAnimation(new TextFlowUpAnimation("+" + diff, pos.getCoords(), Color.CHARTREUSE));
            }
        }
    }
}
