package pl.lonski.dzibdzikon.effect.tile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.effect.DamageEffect;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.map.TextureId;

import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

public class AcidTileEffect implements TileEffect {

    private static final TextureRegion tex = getGameResources().textures.get(TextureId.SPELL_EFFECT_ACID_PUDDLE);
    private int ttl;

    public AcidTileEffect(int ttl) {
        this.ttl = ttl;
    }

    @Override
    public void render(Point pos) {
        float originX = tex.getRegionWidth() / 2f;
        float originY = tex.getRegionHeight() / 2f;
        getGameResources()
                .batch
                .draw(tex, pos.toPixels().x() - originX, pos.toPixels().y() - originY);
    }

    @Override
    public boolean takeTurn(Point pos, World world) {
        var entities = world.getCurrentLevel().getEntitiesAt(pos, FeatureType.ATTACKABLE);
        for (Entity entity : entities) {
            if (entity.isFlying()) {
                continue;
            }

            entity.applyEffect(new DamageEffect(1));
        }

        ttl -= 1;

        return ttl <= 0;
    }
}
