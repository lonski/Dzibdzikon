package pl.lonski.dzibdzikon.spell;

import java.util.List;
import java.util.Optional;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.PositionUtils;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.targeting.TargetingMode;
import pl.lonski.dzibdzikon.animation.Animation;
import pl.lonski.dzibdzikon.effect.tile.AcidTileEffect;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.map.TextureId;

public class AcidPuddle extends SpellBase {
    public AcidPuddle() {
        super(2);
    }

    @Override
    public String getName() {
        return "Kałuża kwasu";
    }

    @Override
    public SpellDescription getDescription() {
        return new SpellDescription(
                "We wskazanym miejscu rozlewa się kałuża kwasu zadając obrażenia wszyskim nie-latającym istotom na obszarze. Kałuża wyparowywuje po upływie 5 tur.",
                "punkt",
                "okrąg o promieniu 2 kafli",
                mpCost + " MP");
    }

    @Override
    public TextureId getIcon() {
        return TextureId.SPELL_ACID_PUDDLE;
    }

    @Override
    public Optional<Animation> getAnimation(Point startPosPix, Point targetPix) {
        return Optional.empty();
    }

    @Override
    public TargetingMode getTargetingMode() {
        return TargetingMode.COORDS;
    }

    @Override
    public void cast(World world, Entity caster, Point target) {
        PositionUtils.inFilledCircleOf(1, p -> {
            var pos = target.add(p);
            var isFloor = world.getCurrentLevel().getMap().getTile(pos).isFloor();
            var noEffectOnTile = world.getCurrentLevel()
                    .getTileEffects()
                    .getOrDefault(pos, List.of())
                    .isEmpty();
            if (isFloor && noEffectOnTile) {
                world.getCurrentLevel().addTileEffect(target.add(p), new AcidTileEffect(15));
            }
        });
        consumeResources(caster);
    }
}
