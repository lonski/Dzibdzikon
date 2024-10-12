package pl.lonski.dzibdzikon.action;

import java.util.function.Consumer;
import pl.lonski.dzibdzikon.World;

public class CustomAction implements Action {

    private boolean done = false;
    private final Consumer<World> runnable;

    public CustomAction(Consumer<World> action) {
        this.runnable = action;
    }

    @Override
    public void update(float delta, World world) {
        runnable.accept(world);
        done = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
