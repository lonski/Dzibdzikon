package pl.lonski.dzibdzikon.command;

import com.badlogic.gdx.Input;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.CloseAction;
import pl.lonski.dzibdzikon.action.targeting.DirectionTargeter;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.Openable;

public class CloseCommand implements Command {

    Point touchTarget = null;

    @Override
    public boolean accept(DzibdziInput.DzibdziKey key) {
        if (key.longPress() && key.touchCoords() != null) {
            touchTarget = key.touchCoords();
            return true;
        }
        touchTarget = null;
        return key.keyCode() == Input.Keys.C;
    }

    @Override
    public void execute(Player player, World world) {
        if (touchTarget != null) {
            var entity = world.getCurrentLevel().getEntityAt(touchTarget, FeatureType.OPENABLE);
            if (entity != null) {
                Openable openable = entity.getFeature(FeatureType.OPENABLE);
                if (openable.opened()) {
                    player.takeAction(new CloseAction(player, touchTarget));
                }
            }
            player.getInputListener().reset();
            touchTarget = null;
            return;
        }
        player.takeAction(new DirectionTargeter(dir -> {
            var openablePos = player.getPosition().getCoords().add(dir);
            return new CloseAction(player, openablePos);
        }));
        player.getInputListener().reset();
    }
}
