package pl.lonski.dzibdzikon.entity.features;


import pl.lonski.dzibdzikon.DzibdziRandom;

public class Attackable implements EntityFeature {

    private int hp;

    private final int maxHp;
    private final int attack;
    private final int defense;

    public Attackable(int hp, int maxHp, int attack, int defense) {
        this.hp = hp;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public FightResult attack(Attackable target) {
        var attackRoll = DzibdziRandom.nextInt(0, getAttack() + 1);
        var defenceRoll = DzibdziRandom.nextInt(0, target.getDefense() + 1);

        if (attackRoll > defenceRoll) {
            var damage = attackRoll - defenceRoll;
            return new FightResult(true, damage);
        }

        return new FightResult(false, 0);
    }

    public record FightResult(boolean hit, int damage) {
    }
}
