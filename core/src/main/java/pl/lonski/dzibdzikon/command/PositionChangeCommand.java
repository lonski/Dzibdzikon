package pl.lonski.dzibdzikon.command;

import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.PositionUtils;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.AttackAction;
import pl.lonski.dzibdzikon.action.MoveAction;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.Openable;
import pl.lonski.dzibdzikon.entity.features.Position;

public class PositionChangeCommand implements Command {
    private Point dPos = new Point(0, 0);

    @Override
    public boolean accepts(DzibdziInput.DzibdziKey key) {
        dPos = PositionUtils.getPositionChange(key);
        return !dPos.isZero();
    }

    @Override
    public void execute(Player player, World world) {
        Position pos = player.getFeature(FeatureType.POSITION);
        Point targetPos =
                new Point(pos.getCoords().x() + dPos.x(), pos.getCoords().y() + dPos.y());

        // move
        if (!world.getCurrentLevel().isObstacle(targetPos)) {
            player.setCurrentAction(new MoveAction(
                    player,
                    new Point(pos.getCoords().x() + dPos.x(), pos.getCoords().y() + dPos.y())));
        } else {

            // Check fight possibility
            world.getCurrentLevel()
                    .getEntityAt(targetPos, FeatureType.ATTACKABLE)
                    .ifPresentOrElse(
                            mob -> player.setCurrentAction(new AttackAction(player, mob)),
                            // check openable
                            // TODO: change to action
                            () -> world.getCurrentLevel()
                                    .getEntityAt(targetPos, FeatureType.OPENABLE)
                                    .ifPresent(openable -> {
                                        openable.<Openable>getFeature(FeatureType.OPENABLE)
                                                .open(world);
                                        player.getInputListener().reset();
                                    }));
        }
    }
}
