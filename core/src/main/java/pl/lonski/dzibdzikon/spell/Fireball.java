package pl.lonski.dzibdzikon.spell;

import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.map.TextureId;
import pl.lonski.dzibdzikon.action.targeting.TargetingMode;

public class Fireball implements Spell {
    @Override
    public String getName() {
        return "Kula ognia";
    }

    @Override
    public String getDescription() {
        return "Wystrzeliwuje kulę ognia, która po uderzeniu w przeszkodę wybucha raniąc wszystkich przeciwników w promieniu 5 kafli.";
    }

    @Override
    public TextureId getIcon() {
        return TextureId.SPELL_SPIKE;
    }

    @Override
    public TargetingMode getTargetingMode() {
        return TargetingMode.COORDS;
    }

    @Override
    public void cast(World world, Entity caster, Point target) {}
}
