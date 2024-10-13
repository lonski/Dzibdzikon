package pl.lonski.dzibdzikon.animation;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Position;

public class FallAnimation extends BaseAnimation {

    private static final float speed = 0.01f;
    private float time = 0;
    private final Position entityPos;
    private static final float targetRotation = 90;
    private boolean done = false;
    private int updatesCounter = 0;

    public FallAnimation(Entity entity) {
        this.entityPos = entity.getFeature(FeatureType.POSITION);
    }

    @Override
    public void update(float delta, World world) {
        time += delta;
        if (time >= speed) {
            updatesCounter += 1;
            time = 0;
            if (entityPos.getRotation() < 0 || entityPos.getRotation() >= targetRotation || updatesCounter > 15) {
                finish();
                return;
            }
            var rotDiff = Math.min(10, targetRotation - entityPos.getRotation());
            entityPos.setRotation(entityPos.getRotation() + rotDiff);
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
        entityPos.setRotation(targetRotation);
        done = true;
    }
}
