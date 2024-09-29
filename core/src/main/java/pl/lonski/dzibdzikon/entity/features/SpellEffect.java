package pl.lonski.dzibdzikon.entity.features;

import pl.lonski.dzibdzikon.spell.Spell;

public class SpellEffect implements EntityFeature {
    private final Spell spell;

    public SpellEffect(Spell spell) {
        this.spell = spell;
    }

    public Spell getSpell() {
        return spell;
    }
}
