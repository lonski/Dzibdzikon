package pl.lonski.dzibdzikon.spell;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.targeting.TargetingMode;
import pl.lonski.dzibdzikon.animation.Animation;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.map.TextureId;

public interface Spell {

    boolean hasResources(Entity caster);

    void consumeResources(Entity caster);

    String getName();

    SpellDescription getDescription();

    TextureId getIcon();

    Optional<Animation> getAnimation(Point startPosPix, Point targetPix);

    TargetingMode getTargetingMode();

    void cast(World world, Entity caster, Point target);

    record SpellDescription(String description, String targetingMode, String range, String cost) {

        private static final int MAX_LINE_WIDTH = 42;

        public List<String> descriptionLines() {
            var words = description.split(" ");
            var lines = new ArrayList<String>();
            var line = new StringBuilder();
            for (var word : words) {
                if (line.length() + word.length() > MAX_LINE_WIDTH) {
                    lines.add(line.toString());
                    line = new StringBuilder();
                }
                line.append(word).append(" ");
            }
            lines.add(line.toString());
            return lines;
        }
    }
}
