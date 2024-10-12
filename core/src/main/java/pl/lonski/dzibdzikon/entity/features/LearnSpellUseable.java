package pl.lonski.dzibdzikon.entity.features;

import com.badlogic.gdx.graphics.Color;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.screen.Hud;
import pl.lonski.dzibdzikon.spell.Spell;

public class LearnSpellUseable extends Useable {

    private final Spell spell;

    public LearnSpellUseable(Spell spell) {
        this.spell = spell;
    }

    @Override
    public void use(Entity user, Entity target) {
        final MagicUser magicUser = user.getFeature(FeatureType.MAGIC_USER);
        if (magicUser == null) {
            return;
        }

        boolean alreadyKnows =
                magicUser.getSpells().stream().anyMatch(s -> s.getName().equals(spell.getName()));
        if (alreadyKnows) {
            if (user instanceof Player) {
                Hud.addMessage("Już znasz czar " + spell.getName());
            }
            return;
        }

        magicUser.getSpells().add(spell);
        if (user instanceof Player) {
            Hud.addMessage("Nauczyłeś się nowego czaru - " + spell.getName(), Color.GOLD);
        }
    }
}
