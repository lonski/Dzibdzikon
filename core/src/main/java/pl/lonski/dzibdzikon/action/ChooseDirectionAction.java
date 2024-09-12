package pl.lonski.dzibdzikon.action;

import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.PositionUtils;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.screen.Hud;

public class ChooseDirectionAction implements Action {

    private boolean done = false;
    private boolean succeeded = false;
    private final DirectionConsumer directionConsumer;
    private Action consumerAction;

    public ChooseDirectionAction(DirectionConsumer directionConsumer) {
        this.directionConsumer = directionConsumer;
    }

    @Override
    public void update(float delta, World world) {
        if (consumerAction != null) {
            Hud.setActionMessage("");
            consumerAction.update(delta, world);
            done = consumerAction.isDone();
            succeeded = consumerAction.succeeded();
            return;
        }

        Hud.setActionMessage("Wybierz kierunek..");
        Point dPos = PositionUtils.getPositionChange(
                world.getPlayer().getInputListener().getKey());
        if (!dPos.isZero()) {
            world.getPlayer().getInputListener().reset();
            consumerAction = directionConsumer.accept(dPos);
            done = consumerAction == null || consumerAction.isDone();
            succeeded = consumerAction != null && consumerAction.succeeded();
        }
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public boolean succeeded() {
        return succeeded;
    }

    public interface DirectionConsumer {
        Action accept(Point direction);
    }
}
