package pl.lonski.dzibdzikon.spell;

import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.MagicUser;

public abstract class SpellBase implements Spell {

    protected final int mpCost;

    protected SpellBase(int mpCost) {
        this.mpCost = mpCost;
    }

    @Override
    public boolean hasResources(Entity caster) {
        return caster.<MagicUser>getFeature(FeatureType.MAGIC_USER).getMana() >= mpCost;
    }

    @Override
    public void consumeResources(Entity caster) {
        var mpUser = caster.<MagicUser>getFeature(FeatureType.MAGIC_USER);
        if (mpUser == null || mpUser.getMana() <= 0) {
            return;
        }

        mpUser.modMana(-mpCost);
    }
}
