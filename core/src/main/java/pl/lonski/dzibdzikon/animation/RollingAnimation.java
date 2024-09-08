package pl.lonski.dzibdzikon.animation;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Position;

public class RollingAnimation implements Animation {

    private final float rollTime = 0.01f;
    private final int rollSpeedDeg;
    private float time = 0;

    public RollingAnimation(int rollSpeedDeg) {
        this.rollSpeedDeg = rollSpeedDeg;
    }

    @Override
    public void update(float delta, World world, Entity entity) {
        Position pos = entity.getFeature(FeatureType.POSITION);

        // not in player view, skip
        if (!world.getCurrentLevel().getVisible().contains(pos.getCoords())) {
            return;
        }

        time += delta;
        if (time >= rollTime) {
            time = 0;
            pos.setRotation((pos.getRotation() + rollSpeedDeg) % 360);
        }
    }
}
