package pl.lonski.dzibdzikon.targeting;

import com.badlogic.gdx.Input;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.Action;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.FieldOfView;
import pl.lonski.dzibdzikon.screen.Hud;

public class SingleAttackableTargeter implements Action {

    private boolean done = false;
    private List<Point> possibleTargets;
    private int currentTargetIdx = 0;
    private Player.InputListener input;
    private Consumer<Point> onTargetSelected;

    public SingleAttackableTargeter(Player player, Consumer<Point> onTargetSelected) {
        this.input = player.getInputListener();
        this.possibleTargets =
                new ArrayList<>(player.<FieldOfView>getFeature(FeatureType.FOV).getHostiles());
        this.onTargetSelected = onTargetSelected;
    }

    @Override
    public void update(float delta, World world) {
        if (possibleTargets.isEmpty()) {
            done = true;
            return;
        }

        if (done) {
            return;
        }

        if (!input.empty()) {
            if (input.getKey().keyCode() == Input.Keys.TAB) {
                currentTargetIdx = (currentTargetIdx + 1) % possibleTargets.size();
            } else if (input.getKey().isEnterKey()) {
                onTargetSelected.accept(possibleTargets.get(currentTargetIdx));
                currentTargetIdx = -1;
                done = true;
            }
            input.reset();
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
}
