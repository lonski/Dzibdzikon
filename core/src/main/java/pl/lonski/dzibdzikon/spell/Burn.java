package pl.lonski.dzibdzikon.spell;

import java.util.Optional;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.targeting.TargetingMode;
import pl.lonski.dzibdzikon.animation.Animation;
import pl.lonski.dzibdzikon.effect.BurnEffect;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.map.TextureId;

public class Burn extends SpellBase {

    public Burn() {
        super(4);
    }

    @Override
    public String getName() {
        return "Podpalenie";
    }

    @Override
    public SpellDescription getDescription() {
        return new SpellDescription("Podpala wybrany cel na 5 tur zadając 2 punkty obrażeń w każdej turze.", "wróg", "pojedynczy wróg", "3 MP");
    }

    @Override
    public TextureId getIcon() {
        return TextureId.SPELL_ENTITY_BURN;
    }

    @Override
    public Optional<Animation> getAnimation(Point startPosPix, Point targetPix) {
        return Optional.empty();
    }

    @Override
    public TargetingMode getTargetingMode() {
        return TargetingMode.SINGLE_ATTACKABLE;
    }

    @Override
    public void cast(World world, Entity caster, Point target) {
        var targetEntityOpt = world.getCurrentLevel().getEntityAt(target, FeatureType.ATTACKABLE);
        if (targetEntityOpt.isEmpty()) {
            return;
        }

        var targetEntity = targetEntityOpt.get();
        targetEntity.applyEffect(new BurnEffect(2, 5));

        consumeResources(caster);
    }
}
