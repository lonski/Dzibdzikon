package pl.lonski.dzibdzikon.entity.features;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.ThrowAction;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.map.Position;

public class ThrowerAi extends RangeAttackerAi {

    protected final Entity toThrow;
    protected int ammo;
    protected final int renewTurns;
    protected int renewRemainingTurns;

    public ThrowerAi(Entity entity, Entity toThrow, int ammo, int renewTurns) {
        super(entity);
        this.toThrow = toThrow;
        this.ammo = ammo;
        this.renewTurns = renewTurns;
        this.renewRemainingTurns = renewTurns;
    }

    @Override
    protected boolean rangeAttack(World world) {
        if (ammo <= 0) { // out of ammo
            renewRemainingTurns -= 1;
            if (renewRemainingTurns <= 0) {
                ammo += 1;
            }
            return false;
        }

        return super.rangeAttack(world);
    }

    protected boolean takeRangeAttackAction(Position throwerPos) {
        entity.takeAction(new ThrowAction(entity, throwerPos, toThrow, playerPos.getCoords()));
        renewRemainingTurns = renewTurns;
        ammo -= 1;
        return true;
    }
}
