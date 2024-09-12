package pl.lonski.dzibdzikon.command;

import com.badlogic.gdx.Input;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.ChooseDirectionAction;
import pl.lonski.dzibdzikon.action.CloseAction;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.Position;

public class CloseCommand implements Command {
    @Override
    public boolean accepts(DzibdziInput.DzibdziKey key) {
        return key.keyCode() == Input.Keys.C;
    }

    @Override
    public void execute(Player player, World world) {
        player.takeAction(new ChooseDirectionAction(dir -> {
            var openablePos = player.<Position>getFeature(FeatureType.POSITION)
                    .getCoords()
                    .add(dir);

            return new CloseAction(player, openablePos);
        }));
        player.getInputListener().reset();
    }
}
