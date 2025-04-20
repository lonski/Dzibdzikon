package pl.lonski.dzibdzikon.command;

import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.PositionUtils;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.AttackAction;
import pl.lonski.dzibdzikon.action.MoveAction;
import pl.lonski.dzibdzikon.action.OpenAction;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.map.Position;

public class PositionChangeCommand implements Command {
    private Point dPos = new Point(0, 0);
    private final Player player;

    public PositionChangeCommand(Player player) {
        this.player = player;
    }

    @Override
    public boolean accept(DzibdziInput.DzibdziKey key) {
        var pos = player.getPosition();
        dPos = PositionUtils.getPositionChange(pos.getCoords(), key);
        if (!dPos.isZero()) {
            player.getInputListener().resetClick();
        }
        return !dPos.isZero();
    }

    @Override
    public void execute(Player player, World world) {
        Position pos = player.getPosition();
        Point targetPos =
                new Point(pos.getCoords().x() + dPos.x(), pos.getCoords().y() + dPos.y());

        // move
        if (!world.getCurrentLevel().isObstacle(targetPos)) {
            player.takeAction(new MoveAction(
                    player,
                    new Point(pos.getCoords().x() + dPos.x(), pos.getCoords().y() + dPos.y())));
        } else {

            // Check fight possibility
            var mob = world.getCurrentLevel().getEntityAt(targetPos, FeatureType.ATTACKABLE);
            if (mob != null) {
                player.takeAction(new AttackAction(player, mob));
            } else {
                var openable = world.getCurrentLevel().getEntityAt(targetPos, FeatureType.OPENABLE);
                if (openable != null) {
                    player.takeAction(new OpenAction(player, openable));
                    player.getInputListener().reset();
                }
            }
        }
    }
}
