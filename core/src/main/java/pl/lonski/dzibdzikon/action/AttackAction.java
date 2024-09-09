package pl.lonski.dzibdzikon.action;

import com.badlogic.gdx.graphics.Color;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Attackable;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.screen.Hud;

public class AttackAction implements Action {

    private final Entity attacker;
    private final Entity target;
    private boolean done = false;
    private final AttackAnimationAction animation;

    public AttackAction(Entity attacker, Entity target) {
        this(attacker, target, true);
    }

    public AttackAction(Entity attacker, Entity target, boolean withAnimation) {
        this.attacker = attacker;
        this.target = target;
        var targetCoords = target.<Position>getFeature(FeatureType.POSITION).getCoords();
        if (withAnimation) {
            this.animation = new AttackAnimationAction(attacker, targetCoords);
        } else {
            this.animation = null;
        }
    }

    @Override
    public void update(float delta, World world) {
        if (animation != null) {
            animation.update(delta, world);
        }
        if (animation == null || animation.isDone()) {
            doFight(world);
            done = true;
        }
    }

    private void doFight(World world) {
        Attackable attacking = attacker.getFeature(FeatureType.ATTACKABLE);
        Attackable defending = target.getFeature(FeatureType.ATTACKABLE);

        var result = attacking.attack(defending);

        if (result.hit()) {
            defending.setHp(defending.getHp() - result.damage());
            if (defending.getHp() <= 0) {
                if (target == world.getPlayer()) {
                    Hud.addMessage("Zabił Cię " + attacker.getName(), Color.RED);
                    // TODO: game over
                } else {
                    Hud.addMessage(
                            attacker.getName() + " zabił " + target.getName().toLowerCase(), Color.RED);
                    world.getCurrentLevel().removeEntity(target);
                }
            } else {
                Hud.addMessage(
                        attacker.getName() + " uderzył " + target.getName().toLowerCase() + " i zadał "
                                + result.damage() + " punktów obrażeń.",
                        Color.PINK);
            }
        } else {
            Hud.addMessage(attacker.getName() + " chybił " + target.getName().toLowerCase() + ".", Color.CYAN);
        }
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
