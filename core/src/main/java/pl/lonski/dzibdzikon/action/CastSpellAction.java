package pl.lonski.dzibdzikon.action;

import com.badlogic.gdx.graphics.Color;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.animation.Animation;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.screen.Hud;
import pl.lonski.dzibdzikon.spell.Spell;

public class CastSpellAction implements Action {

    private boolean done = false;
    private boolean success = false;
    private final Entity caster;
    private final Point target;
    private final Spell spell;

    private final Animation animation;

    public CastSpellAction(Entity caster, Point target, Spell spell) {
        this.caster = caster;
        this.target = target;
        this.spell = spell;
        this.animation = spell.getAnimation(caster.getPosition().getRenderPosition(), target.toPixels())
                .orElse(null);

        if (spell.hasResources(caster)) {
            caster.addAnimation(animation);
        } else {
            if (caster instanceof Player) {
                Hud.addMessage("Nie można rzucić czaru, brak zasobów.", Color.PINK);
            }
            done = true;
            success = false;
        }
    }

    @Override
    public void update(float delta, World world) {
        if (done) {
            return;
        }

        if (animation != null && !animation.isDone()) {
            animation.update(delta, world);
            return;
        }

        spell.cast(world, caster, target);
        done = true;
        success = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public boolean succeeded() {
        return success;
    }
}
