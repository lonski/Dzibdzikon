package pl.lonski.dzibdzikon.command;

import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Player;

public interface Command {

    boolean accepts(DzibdziInput.DzibdziKey key);

    void execute(Player player, World world);

    default void update(float delta) {}
}
