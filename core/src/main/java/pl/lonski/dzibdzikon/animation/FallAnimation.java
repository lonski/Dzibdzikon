package pl.lonski.dzibdzikon.animation;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Position;

public class FallAnimation implements Animation {

    private static final float speed = 0.01f;
    private float time = 0;
    private final Position entityPos;
    private static final float targetRotation = 90;

    public FallAnimation(Entity entity) {
        this.entityPos = entity.getFeature(FeatureType.POSITION);
    }

    @Override
    public void update(float delta, World world) {
        time += delta;
        if (time >= speed) {
            time = 0;
            entityPos.setRotation((entityPos.getRotation() + 10) % 360);
        }
    }

    @Override
    public void render() {}

    @Override
    public boolean isDone() {
        return entityPos.getRotation() >= targetRotation;
    }
}
