package pl.lonski.dzibdzikon.action.targeting;

import com.badlogic.gdx.Input;
import java.util.ArrayList;
import java.util.List;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.Action;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.FieldOfView;
import pl.lonski.dzibdzikon.screen.Hud;

public class SingleAttackableTargeter implements Action {

    private boolean done = false;
    private boolean succeeded = false;
    private int currentTargetIdx = 0;
    private final Player.InputListener input;
    private final TargetConsumer onTargetSelected;
    private List<Point> possibleTargets;
    private Action consumerAction;
    private Player player;

    public SingleAttackableTargeter(Player player, TargetConsumer onTargetSelected) {
        this.input = player.getInputListener();
        this.player = player;
        this.onTargetSelected = onTargetSelected;
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

        this.possibleTargets =
                new ArrayList<>(player.<FieldOfView>getFeature(FeatureType.FOV).getHostiles());

        if (possibleTargets.isEmpty()) {
            Hud.showTargetingButtons(false);
            done = true;
            return;
        }

        if (done) {
            return;
        }

        Hud.setActionMessage("Wybierz cel..");
        Hud.showTargetingButtons(true);
        if (!input.empty()) {
            var key = input.getKey();
            if (key.touchCoords() != null) {
                for (int i = 0; i < possibleTargets.size(); i++) {
                    if (possibleTargets.get(i).equals(key.touchCoords())) {
                        if (i == currentTargetIdx) {
                            Hud.showTargetingButtons(false);
                            Hud.setTargets(List.of());
                            consumerAction = onTargetSelected.accept(possibleTargets.get(currentTargetIdx));
                            currentTargetIdx = -1;
                            done = consumerAction == null || consumerAction.isDone();
                            succeeded = consumerAction != null && consumerAction.succeeded();
                            input.reset();
                            return;
                        } else {
                            currentTargetIdx = i;
                        }
                        break;
                    }
                }
                input.resetClick();
            } else if (key.keyCode() == Input.Keys.TAB) {
                currentTargetIdx = (currentTargetIdx + 1) % possibleTargets.size();
                input.reset();
            } else if (key.isEnterKey()) {
                Hud.showTargetingButtons(false);
                consumerAction = onTargetSelected.accept(possibleTargets.get(currentTargetIdx));
                currentTargetIdx = -1;
                done = consumerAction == null || consumerAction.isDone();
                succeeded = consumerAction != null && consumerAction.succeeded();
                input.reset();
            } else if (key.keyCode() == Input.Keys.ESCAPE) {
                Hud.setActionMessage("");
                Hud.setTargets(List.of());
                Hud.showTargetingButtons(false);
                consumerAction = null;
                done = true;
                succeeded = false;
                input.reset();
                return;
            } else {
                input.reset();
            }
        }

        if (currentTargetIdx >= 0 && currentTargetIdx < possibleTargets.size()) {
            Hud.setTargets(List.of(possibleTargets.get(currentTargetIdx)));
        } else {
            Hud.setTargets(List.of());
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
