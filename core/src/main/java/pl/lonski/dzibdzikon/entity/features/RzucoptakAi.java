package pl.lonski.dzibdzikon.entity.features;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.map.TextureId;

public class RzucoptakAi extends ThrowerAi {

    public RzucoptakAi(Entity entity, Entity toThrow, int ammo, int renewTurns) {
        super(entity, toThrow, ammo, renewTurns);
    }

    @Override
    protected boolean rangeAttack(World world) {
        var attacked = super.rangeAttack(world);

        if (ammo == 0) {
            entity.setGlyph(TextureId.MOB_BIRD_THROWER_0BOTTLE);
        } else if (ammo == 1) {
            entity.setGlyph(TextureId.MOB_BIRD_THROWER_1BOTTLE);
        } else {
            entity.setGlyph(TextureId.MOB_BIRD_THROWER_2BOTTLE);
        }

        return attacked;
    }
}
