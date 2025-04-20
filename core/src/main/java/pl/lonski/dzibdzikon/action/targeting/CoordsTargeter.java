package pl.lonski.dzibdzikon.action.targeting;

import com.badlogic.gdx.Input;
import java.util.List;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.PositionUtils;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.Action;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.screen.Hud;

public class CoordsTargeter implements Action {

    private boolean done = false;
    private boolean succeeded = false;
    private final TargetConsumer targetConsumer;
    private Action consumerAction;
    private Player.InputListener input;
    private Point currentTarget;

    public CoordsTargeter(Player player, TargetConsumer targetConsumer) {
        this.targetConsumer = targetConsumer;
        this.input = player.getInputListener();
        this.currentTarget = player.getPosition().getCoords();
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

        if (done) {
            return;
        }

        Hud.setTargets(List.of(currentTarget));
        Hud.setActionMessage("Wybierz cel..");
        if (!input.empty()) {

            if (input.getKey().isEnterKey()) {
                consumerAction = targetConsumer.accept(currentTarget);
                done = consumerAction == null || consumerAction.isDone();
                succeeded = consumerAction != null && consumerAction.succeeded();
                Hud.setTargets(List.of());
            } else if (input.getKey().keyCode() == Input.Keys.ESCAPE) {
                Hud.setActionMessage("");
                Hud.setTargets(List.of());
                consumerAction = null;
                done = true;
                succeeded = false;
                Hud.setTargets(List.of());
            } else {
                var dpos = PositionUtils.getPositionChange(currentTarget, input.getKey());
                var newTarget = currentTarget.add(dpos);
                if (world.getCurrentLevel().getVisible().contains(newTarget)
                        && !world.getCurrentLevel().isObstacle(newTarget, false)) {
                    currentTarget = newTarget;
                    Hud.setTargets(List.of(currentTarget));
                }
            }

            input.reset();
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
}
