package pl.lonski.dzibdzikon.animation;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Position;

public class UnFallAnimation implements Animation {

    private final float speed = 0.01f;
    private float time = 0;
    private final Position entityPos;
    private final float targetRotation;
    private boolean done = false;

    public UnFallAnimation(Entity entity, float targetRotation) {
        this.targetRotation = targetRotation;
        this.entityPos = entity.getFeature(FeatureType.POSITION);
    }

    @Override
    public void update(float delta, World world) {
        time += delta;
        if (time >= speed) {
            time = 0;
            entityPos.setRotation((entityPos.getRotation() - 10));
            if (entityPos.getRotation() <= targetRotation) {
                entityPos.setRotation(targetRotation);
                done = true;
            }
        }
    }

    @Override
    public void render() {}

    @Override
    public boolean isDone() {
        return done;
    }
}
