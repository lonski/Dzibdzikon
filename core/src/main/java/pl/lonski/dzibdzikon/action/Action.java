package pl.lonski.dzibdzikon.action;

import pl.lonski.dzibdzikon.World;

public interface Action {

    void update(float delta, World world);

    boolean isDone();

    default boolean succeeded() {
        return isDone();
    }
}
