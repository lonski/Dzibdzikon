package pl.lonski.dzibdzikon.action;

import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Attackable;

public class AttackAction implements Action {

    private final Entity attacker;
    private final Entity target;
    private boolean done = false;

    public AttackAction(Entity attacker, Entity target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public void update(float delta, World world) {

        Attackable attacking = attacker.getFeature(FeatureType.ATTACKABLE);
        Attackable defending = target.getFeature(FeatureType.ATTACKABLE);

        var attackRoll = Dzibdzikon.RANDOM.nextInt(0, attacking.getAttack() + 1);
        var defenceRoll = Dzibdzikon.RANDOM.nextInt(0, defending.getDefense() + 1);
        var message = "";

        if (attackRoll > defenceRoll) {
            var damage = attackRoll - defenceRoll;
            defending.setHp(defending.getHp() - damage);
            if (defending.getHp() <= 0) {
                if (target == world.getPlayer()) {
                    message = attacker.getName() + " killed you.";
                    // TODO: game over
                } else {
                    message = attacker.getName() + " killed " + target.getName().toLowerCase() + ".";
                    world.getCurrentLevel().removeEntity(target);
                }
            } else {
                message = attacker.getName() + " hit " + target.getName().toLowerCase() + " for " + damage + " hp.";
            }
        } else {
            message = attacker.getName() + " missed " + target.getName().toLowerCase() + ".";
        }

        System.out.println(message);

        done = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
