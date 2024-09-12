package pl.lonski.dzibdzikon.command;

import com.badlogic.gdx.Input;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.screen.Hud;

public class GoDownCommand implements Command {
    @Override
    public boolean accepts(DzibdziInput.DzibdziKey key) {
        return (key.keyCode() == Input.Keys.PERIOD && DzibdziInput.isShiftDown);
    }

    @Override
    public void execute(Player player, World world) {
        var myPos = player.<Position>getFeature(FeatureType.POSITION);
        if (world.getCurrentLevel()
                .getEntityAt(myPos.getCoords(), FeatureType.DOWNSTAIRS)
                .isPresent()) {
            Hud.addMessage("Schodzenie w dół...");
            world.nextLevel();
        } else {
            Hud.addMessage("Nie ma tutaj schodów po których można zejść.");
        }
        player.getInputListener().reset();
    }
}
