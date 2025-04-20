package pl.lonski.dzibdzikon.command;

import com.badlogic.gdx.Input;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.CloseAction;
import pl.lonski.dzibdzikon.action.targeting.DirectionTargeter;
import pl.lonski.dzibdzikon.entity.Player;

public class CloseCommand implements Command {
    @Override
    public boolean accept(DzibdziInput.DzibdziKey key) {
        return key.keyCode() == Input.Keys.C;
    }

    @Override
    public void execute(Player player, World world) {
        player.takeAction(new DirectionTargeter(dir -> {
            var openablePos = player.getPosition().getCoords().add(dir);

            return new CloseAction(player, openablePos);
        }));
        player.getInputListener().reset();
    }
}
