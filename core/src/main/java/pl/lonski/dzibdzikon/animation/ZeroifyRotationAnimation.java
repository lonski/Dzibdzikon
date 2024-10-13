package pl.lonski.dzibdzikon.animation;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Position;

public class ZeroifyRotationAnimation extends BaseAnimation {

    private final float speed = 0.01f;
    private float time = 0;
    private final Position entityPos;
    private boolean done = false;

    private int updatesCounter = 0;

    public ZeroifyRotationAnimation(Entity entity) {
        this.entityPos = entity.getFeature(FeatureType.POSITION);
    }

    @Override
    public void update(float delta, World world) {
        time += delta;
        if (time >= speed) {
            time = 0;
            updatesCounter += 1;

            var rotDiff = Math.min(10, entityPos.getRotation());
            entityPos.setRotation((entityPos.getRotation() - rotDiff));

            if (entityPos.getRotation() <= 0 || updatesCounter > 15) {
                finish();
            }
        }
    }

    @Override
    public void render() {}

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void finish() {
        entityPos.setRotation(0);
        done = true;
    }
}
