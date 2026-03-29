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
            Hud.showTargetingButtons(false);
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
        Hud.showTargetingButtons(true);
        if (!input.empty()) {
            var key = input.getKey();

            if (key.isEnterKey()) {
                Hud.showTargetingButtons(false);
                consumerAction = targetConsumer.accept(currentTarget);
                done = consumerAction == null || consumerAction.isDone();
                succeeded = consumerAction != null && consumerAction.succeeded();
                Hud.setTargets(List.of());
            } else if (key.keyCode() == Input.Keys.ESCAPE) {
                Hud.setActionMessage("");
                Hud.setTargets(List.of());
                Hud.showTargetingButtons(false);
                consumerAction = null;
                done = true;
                succeeded = false;
            } else if (key.touchCoords() != null) {
                // Direct tap sets the target position
                var tapped = key.touchCoords();
                if (world.getCurrentLevel().getVisible().contains(tapped)
                        && !world.getCurrentLevel().isObstacle(tapped, false)) {
                    currentTarget = tapped;
                    Hud.setTargets(List.of(currentTarget));
                }
                input.resetClick();
                return;
            } else {
                var dpos = PositionUtils.getPositionChange(currentTarget, key);
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
