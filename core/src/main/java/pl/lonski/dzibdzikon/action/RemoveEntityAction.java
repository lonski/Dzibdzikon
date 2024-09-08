package pl.lonski.dzibdzikon.action;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;

public class RemoveEntityAction implements Action {

    final Entity entity;
    private boolean done = false;

    public RemoveEntityAction(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void update(float delta, World world) {
        world.getCurrentLevel().removeEntity(entity);
        done = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
