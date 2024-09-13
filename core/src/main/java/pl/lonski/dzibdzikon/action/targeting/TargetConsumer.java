package pl.lonski.dzibdzikon.action.targeting;

import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.action.Action;

public interface TargetConsumer {

    Action accept(Point direction);
}
