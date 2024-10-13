package pl.lonski.dzibdzikon.animation;

import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.function.Supplier;
import pl.lonski.dzibdzikon.Debouncer;
import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.map.TextureId;

public class BurnAnimation extends BaseAnimation {

    private final Debouncer debouncer = new Debouncer(0.05f);
    private final Supplier<Point> coords;
    private final TextureRegion[] frames;
    private int frameIndex = 0;

    public BurnAnimation(Supplier<Point> coords) {
        this.coords = coords;
        this.frames = getGameResources()
                .textures
                .get(TextureId.ANIMATION_BURN)
                .split(Dzibdzikon.TILE_WIDTH, Dzibdzikon.TILE_HEIGHT)[0];
    }

    @Override
    public void update(float delta, World world) {
        if (debouncer.debounce(delta)) {
            frameIndex = (frameIndex + 1) % frames.length;
        }
    }

    @Override
    public void render() {
        TextureRegion texture = frames[frameIndex];
        float originX = texture.getRegionWidth() / 2f;
        float originY = texture.getRegionHeight() / 2f;
        var pos = coords.get();
        getGameResources()
                .batch
                .draw(
                        texture,
                        pos.toPixels().x() - originX,
                        pos.toPixels().y() - originY,
                        originX,
                        originY,
                        texture.getRegionWidth(),
                        texture.getRegionHeight(),
                        1.0f,
                        1.0f,
                        0);
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void finish() {}
}
