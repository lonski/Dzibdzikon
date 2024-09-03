package pl.lonski.dzibdzikon.action;

import pl.lonski.dzibdzikon.World;

public class NoOpAction implements Action {

    @Override
    public void update(float delta, World world) {
    }

    @Override
    public boolean isDone() {
        return true;
    }
}
