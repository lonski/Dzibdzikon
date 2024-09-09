package pl.lonski.dzibdzikon.animation;

import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;

public interface Animation {

    void update(float delta, World world);

    void render(Dzibdzikon game);

    boolean isDone();
}
