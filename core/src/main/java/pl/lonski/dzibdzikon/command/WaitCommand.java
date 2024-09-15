package pl.lonski.dzibdzikon.command;

import com.badlogic.gdx.Input;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.NoOpAction;
import pl.lonski.dzibdzikon.entity.Player;

public class WaitCommand implements Command {

    private float timeSinceLastWait = 0;
    private final float waitDebounce = 0.3f;

    @Override
    public boolean accept(DzibdziInput.DzibdziKey key) {
        return (key.keyCode() == Input.Keys.NUMPAD_5 || key.keyCode() == Input.Keys.SPACE);
    }

    @Override
    public void update(float delta) {
        timeSinceLastWait += delta;
    }

    @Override
    public void execute(Player player, World world) {
        if (timeSinceLastWait >= waitDebounce) {
            timeSinceLastWait = 0;
            player.takeAction(new NoOpAction());
        }
    }
}
