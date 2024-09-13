package pl.lonski.dzibdzikon.spell;

import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.map.TextureId;
import pl.lonski.dzibdzikon.action.targeting.TargetingMode;

public interface Spell {

    String getName();

    String getDescription();

    TextureId getIcon();

    TargetingMode getTargetingMode();

    void cast(World world, Entity caster, Point target);
}
