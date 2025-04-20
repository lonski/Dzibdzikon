package pl.lonski.dzibdzikon.effect;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.animation.BurnAnimation;
import pl.lonski.dzibdzikon.entity.Entity;

public class BurnEffect implements Effect {

    private final int damagePerTurn;
    private int turns;

    public BurnEffect(int damagePerTurn, int turns) {
        this.damagePerTurn = damagePerTurn;
        this.turns = turns;
    }

    @Override
    public boolean stackable() {
        return false;
    }

    @Override
    public void apply(Entity target) {
        var anim = new BurnAnimation(() -> target.getPosition().getCoords());
        anim.setOwner(this);
        target.addAnimation(anim);
    }

    @Override
    public boolean isActive() {
        return turns > 0;
    }

    @Override
    public void takeTurn(World world, Entity target) {
        target.applyEffect(new DamageEffect(damagePerTurn));
        turns--;
    }

    @Override
    public void remove(Entity target) {
        target.removeAnimationByOwner(this);
    }
}
