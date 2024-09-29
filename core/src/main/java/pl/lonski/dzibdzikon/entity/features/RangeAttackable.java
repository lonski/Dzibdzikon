package pl.lonski.dzibdzikon.entity.features;

import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.map.TextureId;

public class RangeAttackable implements EntityFeature {

    private final int range;
    private final int attack;
    private final TextureId ammo;

    public RangeAttackable(TextureId ammo, int range, int attack) {
        this.range = range;
        this.attack = attack;
        this.ammo = ammo;
    }

    public int getRange() {
        return range;
    }

    public int getAttack() {
        return attack;
    }

    public TextureId getAmmo() {
        return ammo;
    }

    public FightResult attack(Attackable target) {
        var attackRoll = Dzibdzikon.RANDOM.nextInt(0, getAttack() + 1);
        var defenceRoll = Dzibdzikon.RANDOM.nextInt(0, target.getDefense() + 1);

        if (attackRoll > defenceRoll) {
            var damage = attackRoll - defenceRoll;
            return new FightResult(true, damage);
        }

        return new FightResult(false, 0);
    }

    public record FightResult(boolean hit, int damage) {}
}
