package pl.lonski.dzibdzikon.entity.features;

import java.util.ArrayList;
import java.util.List;
import pl.lonski.dzibdzikon.spell.Spell;

public class SpellBook implements EntityFeature {

    private final List<Spell> spells = new ArrayList<>();

    public List<Spell> getSpells() {
        return spells;
    }

    public SpellBook(List<Spell> spells) {
        this.spells.addAll(spells);
    }
}
