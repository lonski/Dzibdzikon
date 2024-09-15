package pl.lonski.dzibdzikon.action;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;

import java.util.List;

public class DieAction implements Action {

    private final Action action;

    public DieAction(Entity entity) {
        action = new ChainAction(List.of(new FallAnimationAction(entity), new RemoveEntityAction(entity)));
    }

    @Override
    public void update(float delta, World world) {
        action.update(delta, world);
    }

    @Override
    public boolean isDone() {
        return action.isDone();
    }
}
