package pl.lonski.dzibdzikon.entity.features;

import java.util.ArrayList;
import java.util.List;
import pl.lonski.dzibdzikon.spell.Spell;

public class MagicUser implements EntityFeature {

    public MagicUser(List<Spell> spells, int manaMax, int mana) {
        this.manaMax = manaMax;
        this.mana = mana;
        this.spells.addAll(spells);
    }

    private final List<Spell> spells = new ArrayList<>();
    private int manaMax;
    private int mana;

    public List<Spell> getSpells() {
        return spells;
    }

    public boolean knowsSpell(Spell spell) {
       return getSpells().stream().anyMatch(s -> s.getName().equals(spell.getName()));
    }

    public int getManaMax() {
        return manaMax;
    }

    public void setManaMax(int manaMax) {
        this.manaMax = manaMax;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void modMana(int mod) {
        this.mana += mod;
        this.mana = Math.min(this.mana, this.manaMax);
        this.mana = Math.max(this.mana, 0);
    }
}
