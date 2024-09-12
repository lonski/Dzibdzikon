package pl.lonski.dzibdzikon.spell;

import pl.lonski.dzibdzikon.map.TextureId;

public class SpikeSpell implements Spell {
    @Override
    public String getName() {
        return "Kolec";
    }

    @Override
    public String getDescription() {
        return "Wystrzeliwuje ostry, magiczny, kolec we wskazanym kierunku.";
    }

    @Override
    public TextureId getIcon() {
        return TextureId.SPELL_SPIKE;
    }
}
