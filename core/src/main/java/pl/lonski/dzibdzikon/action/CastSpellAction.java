package pl.lonski.dzibdzikon.action;

import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.spell.Spell;

public class CastSpellAction implements Action {

    private boolean done = false;
    private final Entity caster;
    private final Point target;
    private final Spell spell;

    public CastSpellAction(Entity caster, Point target, Spell spell) {
        this.caster = caster;
        this.target = target;
        this.spell = spell;
    }

    @Override
    public void update(float delta, World world) {
        // todo: animation
        spell.cast(world, caster, target);
        done = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
