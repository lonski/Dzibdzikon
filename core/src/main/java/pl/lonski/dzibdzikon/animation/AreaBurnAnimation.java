package pl.lonski.dzibdzikon.animation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pl.lonski.dzibdzikon.Debouncer;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.map.TextureId;

import java.util.List;

import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

public class AreaBurnAnimation implements Animation {

    private final Debouncer debouncer = new Debouncer(0.01f);
    private final List<Point> points;
    private boolean done = false;
    private float speed = 0.1f;
    private TextureRegion texture;
    private float alpha = 0;
    private boolean appearing = true;

    public AreaBurnAnimation(List<Point> points) {
        this.points = points;
        texture = getGameResources().textures.get(TextureId.SPELL_EFFECT_BURN);
    }

    @Override
    public void update(float delta, World world) {
        if (isDone()) {
            return;
        }

        if (!appearing && alpha <= 0.0f) {
            finish();
            return;
        }

        if (appearing && alpha < 1.0f) {
            alpha += speed;
        } else if (appearing) {
            appearing = false;
        } else {
            alpha -= speed;
        }
    }

    @Override
    public void render() {
        if (isDone()) {
            return;
        }

        float originX = texture.getRegionWidth() / 2f;
        float originY = texture.getRegionHeight() / 2f;
        getGameResources().batch.setColor(1, 1, 1, alpha);
        for (Point point : points) {
            getGameResources()
                    .batch
                    .draw(
                            texture,
                            point.toPixels().x() - originX,
                            point.toPixels().y() - originY,
                            originX,
                            originY,
                            texture.getRegionWidth(),
                            texture.getRegionHeight(),
                            1.0f,
                            1.0f,
                            0);
        }
        getGameResources().batch.setColor(1, 1, 1, 1);
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void finish() {
        done = true;
    }
}
