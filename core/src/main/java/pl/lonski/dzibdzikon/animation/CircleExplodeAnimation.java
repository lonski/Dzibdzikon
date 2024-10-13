package pl.lonski.dzibdzikon.animation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pl.lonski.dzibdzikon.Debouncer;
import pl.lonski.dzibdzikon.ExplosionSimulator;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.map.TextureId;

import java.util.ArrayList;
import java.util.List;

import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

public class CircleExplodeAnimation extends BaseAnimation {

    private final Debouncer debouncer = new Debouncer(0.06f);
    private final Point center;
    private final List<Point> burningPoints = new ArrayList<>();
    private final int radius;
    private boolean done = false;
    private TextureRegion texture;
    private ExplosionSimulator simulator;

    public CircleExplodeAnimation(Point center, int radius) {
        this.center = center;
        this.radius = radius;
        texture = getGameResources().textures.get(TextureId.SPELL_EFFECT_BURN);
    }

    @Override
    public void update(float delta, World world) {
        if (isDone() || !debouncer.debounce(delta)) {
            return;
        }

        if (simulator == null) {
            simulator = new ExplosionSimulator(center, radius, world);
        }

        if (!simulator.hasMoreSteps()) {
            finish();
        }

        burningPoints.clear();
        burningPoints.addAll(simulator.step());
    }

    @Override
    public void render() {
        if (isDone()) {
            return;
        }

        float originX = texture.getRegionWidth() / 2f;
        float originY = texture.getRegionHeight() / 2f;
        //        getGameResources().batch.setColor(1, 1, 1, alpha);
        for (Point point : burningPoints) {
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
