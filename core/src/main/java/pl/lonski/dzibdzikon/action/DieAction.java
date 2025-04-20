package pl.lonski.dzibdzikon.action;

import java.util.List;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;

public class DieAction implements Action {

    private final Action action;

    public DieAction(Entity entity) {
        action = new ChainAction(List.of(
                new FallAnimationAction(entity.getPosition()),
                new RemoveEntityAction(entity),
                new CustomAction(entity::onAfterDeath)));
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
