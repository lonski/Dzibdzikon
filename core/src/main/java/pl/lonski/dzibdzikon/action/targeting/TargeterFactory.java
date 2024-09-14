package pl.lonski.dzibdzikon.action.targeting;

import pl.lonski.dzibdzikon.action.Action;
import pl.lonski.dzibdzikon.entity.Player;

public class TargeterFactory {

    public static Action create(Player player, TargetingMode mode, TargetConsumer onTargetSelected) {
        switch (mode) {
            case SINGLE_ATTACKABLE:
                return new SingleAttackableTargeter(player, onTargetSelected);
            case DIRECTION:
                return new DirectionTargeter(onTargetSelected);
            case COORDS:
                return new CoordsTargeter(player, onTargetSelected);
            default:
                throw new IllegalArgumentException("Unknown targeting mode: " + mode);
        }
    }
}
