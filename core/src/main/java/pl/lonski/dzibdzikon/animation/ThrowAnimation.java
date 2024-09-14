package pl.lonski.dzibdzikon.animation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pl.lonski.dzibdzikon.Debouncer;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.map.Line;
import pl.lonski.dzibdzikon.map.TextureId;

import java.util.List;

import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

public class ThrowAnimation implements Animation {

    private final TextureRegion texture;
    private Point currentPosPix;
    private final List<Point> line;
    private Debouncer debouncer = new Debouncer(0.01f);
    private boolean done = false;
    private int speed;
    private float rotation = 0;

    public ThrowAnimation(TextureId textureId, Point startPix, Point endPix) {
        this(textureId, startPix, endPix, 1);
    }

    public ThrowAnimation(TextureId textureId, Point startPix, Point endPix, int speed) {
        this.texture = getGameResources().textures.get(textureId);
        this.currentPosPix = startPix;
        this.line = Line.calculate(startPix, endPix);
        this.speed = speed;

        double dx = startPix.x() - endPix.x();
        double dy = startPix.y() - endPix.y();
        rotation = (float) Math.toDegrees(Math.atan(dy / dx));
        if (dx >= 0) {
            rotation += 180;
        }
    }

    @Override
    public void update(float delta, World world) {
        if (isDone()) {
            return;
        }

        if (debouncer.debounce(delta)) {
            var i = speed;
            while (i-- > 0 && !line.isEmpty()) {
                currentPosPix = line.remove(0);
            }

            if (line.isEmpty()) {
                done = true;
            }
        }
    }

    @Override
    public void render() {
        if (isDone()) {
            return;
        }

        float originX = texture.getRegionWidth() / 2f;
        float originY = texture.getRegionHeight() / 2f;
        getGameResources().batch.draw(
            texture,
            currentPosPix.x() - originX,
            currentPosPix.y() - originY,
            originX,
            originY,
            texture.getRegionWidth(),
            texture.getRegionHeight(),
            1.0f,
            1.0f,
            rotation);
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
