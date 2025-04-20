package pl.lonski.dzibdzikon.command;

import com.badlogic.gdx.Input;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.screen.Hud;

public class GoDownCommand implements Command {
    @Override
    public boolean accept(DzibdziInput.DzibdziKey key) {
        return (key.keyCode() == Input.Keys.PERIOD && DzibdziInput.isShiftDown);
    }

    @Override
    public void execute(Player player, World world) {
        var myPos = player.getPosition();
        if (world.getCurrentLevel().getEntityAt(myPos.getCoords(), FeatureType.DOWNSTAIRS) != null) {
            Hud.addMessage("Schodzenie w dół...");
            world.nextLevel();
        } else {
            Hud.addMessage("Nie ma tutaj schodów po których można zejść.");
        }
        player.getInputListener().reset();
    }
}
