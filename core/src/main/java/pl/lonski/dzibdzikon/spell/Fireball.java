package pl.lonski.dzibdzikon.spell;

import pl.lonski.dzibdzikon.map.TextureId;

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
}
