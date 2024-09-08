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
    private final MoveAnimationAction moveAnimation;

    public AttackAction(Entity attacker, Entity target) {
        this(attacker, target, true);
    }

    public AttackAction(Entity attacker, Entity target, boolean withAnimation) {
        this.attacker = attacker;
        this.target = target;
        var myPos = attacker.<Position>getFeature(FeatureType.POSITION);
        var targetPos = target.<Position>getFeature(FeatureType.POSITION);

        var diff = targetPos.getCoords().sub(myPos.getCoords());
        var targetRenderPos = new Point(
            myPos.getRenderPosition().x() + diff.x() * Dzibdzikon.TILE_WIDTH / 2,
            myPos.getRenderPosition().y() + diff.y() * Dzibdzikon.TILE_HEIGHT / 2);

        if (withAnimation) {
            this.moveAnimation = new MoveAnimationAction(attacker, new Point(targetPos.getCoords()), targetRenderPos);
            this.moveAnimation.setMoveSpeed(2);
            this.moveAnimation.setBackToOriginalPosition(true);
        } else {
            this.moveAnimation = null;
        }
    }

    @Override
    public void update(float delta, World world) {
        if (moveAnimation != null) {
            moveAnimation.update(delta, world);
        }
        if (moveAnimation == null || moveAnimation.isDone()) {
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
                    Hud.addMessage(attacker.getName() + " zabił " + target.getName().toLowerCase(), Color.RED);
                    world.getCurrentLevel().removeEntity(target);
                }
            } else {
                Hud.addMessage(attacker.getName() + " uderzył " + target.getName().toLowerCase() + " i zadał " + result.damage() + " punktów obrażeń.", Color.PINK);
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
