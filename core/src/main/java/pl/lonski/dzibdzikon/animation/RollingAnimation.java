package pl.lonski.dzibdzikon.animation;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.map.Position;

public class RollingAnimation extends BaseAnimation {

    private final int rollSpeedDeg;
    private float time = 0;
    private final Entity entity;

    public RollingAnimation(int rollSpeedDeg, Entity entity) {
        this.rollSpeedDeg = rollSpeedDeg;
        this.entity = entity;
    }

    @Override
    public void update(float delta, World world) {
        Position pos = entity.getPosition();

        // not in player view, skip
        if (!world.getCurrentLevel().getVisible().contains(pos.getCoords())) {
            return;
        }

        time += delta;
        float rollTime = 0.01f;
        if (time >= rollTime) {
            time = 0;
            pos.setRotation((pos.getRotation() + rollSpeedDeg) % 360);
        }
    }

    @Override
    public void render() {}

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void finish() {}
}
