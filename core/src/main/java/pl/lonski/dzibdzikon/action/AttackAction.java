package pl.lonski.dzibdzikon.action;

import com.badlogic.gdx.graphics.Color;
import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;
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

    private final MoveAnimation moveAnimation;

    public AttackAction(Entity attacker, Entity target) {
        this.attacker = attacker;
        this.target = target;
        var myPos = attacker.<Position>getFeature(FeatureType.POSITION);
        var targetPos = target.<Position>getFeature(FeatureType.POSITION);

        var diff = targetPos.getCoords().sub(myPos.getCoords());
        var targetRenderPos = new Point(
            myPos.getRenderPosition().x() + diff.x() * Dzibdzikon.TILE_WIDTH / 2,
            myPos.getRenderPosition().y() + diff.y() * Dzibdzikon.TILE_HEIGHT / 2);

        this.moveAnimation = new MoveAnimation(attacker, new Point(targetPos.getCoords()), targetRenderPos);
        this.moveAnimation.setMoveSpeed(2);
        this.moveAnimation.setBackToOriginalPosition(true);
    }

    @Override
    public void update(float delta, World world) {
        moveAnimation.update(delta, world);
        if (moveAnimation.isDone()) {
            doFight(world);
            done = true;
        }
    }

    private void doFight(World world) {
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
                    Hud.addMessage("Zabił Cię " + attacker.getName(), Color.RED);
                    // TODO: game over
                } else {
                    Hud.addMessage(attacker.getName() + " zabił " + target.getName().toLowerCase(), Color.RED);
                    world.getCurrentLevel().removeEntity(target);
                }
            } else {
                Hud.addMessage(attacker.getName() + " uderzył " + target.getName().toLowerCase() + " i zadał " + damage + " punktów obrażeń.", Color.PINK);
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
