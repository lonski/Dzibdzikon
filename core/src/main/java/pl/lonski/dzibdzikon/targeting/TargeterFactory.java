package pl.lonski.dzibdzikon.targeting;

import java.util.function.Consumer;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.action.Action;
import pl.lonski.dzibdzikon.entity.Player;

public class TargeterFactory {

    public static Action create(Player player, TargetingMode mode, Consumer<Point> onTargetSelected) {
        switch (mode) {
            case SINGLE_ATTACKABLE:
                return new SingleAttackableTargeter(player, onTargetSelected);
            default:
                throw new IllegalArgumentException("Unknown targeting mode: " + mode);
        }
    }
}
