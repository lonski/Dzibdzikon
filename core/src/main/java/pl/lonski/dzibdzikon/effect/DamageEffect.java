package pl.lonski.dzibdzikon.effect;

import com.badlogic.gdx.graphics.Color;
import pl.lonski.dzibdzikon.action.DieAction;
import pl.lonski.dzibdzikon.animation.TextFlowUpAnimation;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.Attackable;

public class DamageEffect implements Effect {

    private final int damage;

    public DamageEffect(int damage) {
        this.damage = damage;
    }

    @Override
    public void apply(Entity target) {
        var attackable = target.<Attackable>getFeature(FeatureType.ATTACKABLE);
        var pos = target.getPosition();
        var newHp = attackable.getHp() - damage;
        attackable.setHp(newHp);
        target.addAnimation(new TextFlowUpAnimation("-" + damage, pos.getCoords(), Color.SCARLET));
        if (attackable.getHp() <= 0 && !(target instanceof Player)) {
            target.takeAction(new DieAction(target));
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }
}
