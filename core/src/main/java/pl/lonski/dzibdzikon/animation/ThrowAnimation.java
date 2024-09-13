package pl.lonski.dzibdzikon.animation;

import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.List;
import pl.lonski.dzibdzikon.Debouncer;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.map.Line;
import pl.lonski.dzibdzikon.map.TextureId;

public class ThrowAnimation implements Animation {

    private final TextureRegion texture;
    private Point currentPosPix;
    private final List<Point> line;
    private Debouncer debouncer = new Debouncer(0.01f);
    private boolean done = false;
    private int speed;

    public ThrowAnimation(TextureId textureId, Point startPix, Point endPix) {
        this(textureId, startPix, endPix, 1);
    }

    public ThrowAnimation(TextureId textureId, Point startPix, Point endPix, int speed) {
        this.texture = getGameResources().textures.get(textureId);
        this.currentPosPix = startPix;
        this.line = Line.calculate(startPix, endPix);
        this.speed = speed;
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

        getGameResources().batch.draw(texture, currentPosPix.x(), currentPosPix.y());
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
