package pl.lonski.dzibdzikon.action;

import pl.lonski.dzibdzikon.World;

import java.util.ArrayList;
import java.util.List;

public class ChainAction implements Action {

    private final List<Action> actions;
    private boolean success = true;

    public ChainAction(List<Action> actions) {
        this.actions = new ArrayList<>(actions);
    }

    @Override
    public void update(float delta, World world) {
        if (actions.isEmpty()) {
            return;
        }

        var currentAction = actions.get(0);
        currentAction.update(delta, world);

        if (currentAction.isDone()) {
            if (!currentAction.succeeded()) {
                success = false;
            }
            this.actions.remove(0);
        }
    }

    @Override
    public boolean isDone() {
        return actions.isEmpty();
    }

    @Override
    public boolean succeeded() {
        return success;
    }
}
