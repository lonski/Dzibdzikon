package pl.lonski.dzibdzikon.spell;

import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.targeting.TargetingMode;
import pl.lonski.dzibdzikon.animation.Animation;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.map.TextureId;

import java.util.Optional;

public interface Spell {

    String getName();

    String getDescription();

    TextureId getIcon();

    Optional<Animation> getAnimation(Point startPosPix, Point targetPix);

    TargetingMode getTargetingMode();

    void cast(World world, Entity caster, Point target);
}
