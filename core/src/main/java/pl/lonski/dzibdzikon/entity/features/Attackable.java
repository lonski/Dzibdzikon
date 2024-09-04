package pl.lonski.dzibdzikon.entity.features;


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
}
