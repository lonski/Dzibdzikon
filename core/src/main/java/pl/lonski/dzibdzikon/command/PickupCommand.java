package pl.lonski.dzibdzikon.command;

import com.badlogic.gdx.Input;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.PickupAction;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.Position;

public class PickupCommand implements Command {
    @Override
    public boolean accept(DzibdziInput.DzibdziKey key) {
        return key.keyCode() == Input.Keys.COMMA;
    }

    @Override
    public void execute(Player player, World world) {
        var pos = player.<Position>getFeature(FeatureType.POSITION).getCoords();
        var item = world.getCurrentLevel().getEntityAt(pos, FeatureType.PICKABLE);
        if (item != null) {
            player.takeAction(new PickupAction(player, item));
        }
    }
}
