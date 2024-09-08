package pl.lonski.dzibdzikon.action;

import pl.lonski.dzibdzikon.World;

public class CustomAction implements Action {

    private boolean done = false;
    private final Runnable runnable;

    public CustomAction(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void update(float delta, World world) {
        runnable.run();
        done = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
