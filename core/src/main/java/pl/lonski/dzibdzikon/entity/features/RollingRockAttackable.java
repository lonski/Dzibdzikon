package pl.lonski.dzibdzikon.entity.features;

import pl.lonski.dzibdzikon.Dzibdzikon;

public class RollingRockAttackable extends Attackable {

    public RollingRockAttackable(int hp, int maxHp, int damage) {
        super(hp, maxHp, damage, 0);
    }

    @Override
    public FightResult attack(Attackable target) {
        var defenceRoll = Dzibdzikon.RANDOM.nextInt(Math.max(1, target.getDefense()));
        var damageReduction = Math.min(getAttack() / 2, defenceRoll);
        var damage = getAttack() - damageReduction;

        return new FightResult(true, damage);
    }
}
