package pl.lonski.dzibdzikon.animation;

import pl.lonski.dzibdzikon.World;

public interface Animation {

    void update(float delta, World world);

    void render();

    boolean isDone();
}
