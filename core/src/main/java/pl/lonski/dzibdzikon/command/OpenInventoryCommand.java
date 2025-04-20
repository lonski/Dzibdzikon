package pl.lonski.dzibdzikon.command;

import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

import com.badlogic.gdx.Input;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.UseAction;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.Useable;
import pl.lonski.dzibdzikon.screen.Hud;
import pl.lonski.dzibdzikon.screen.WindowManager;
import pl.lonski.dzibdzikon.ui.window.InventoryWindow;

public class OpenInventoryCommand implements Command {

    @Override
    public boolean accept(DzibdziInput.DzibdziKey key) {
        return key.keyCode() == Input.Keys.I;
    }

    @Override
    public void execute(Player player, World world) {
        getGameResources()
                .windowManager
                .executeInWindow(
                        WindowManager.WindowType.INVENTORY,
                        window -> ((InventoryWindow) window).getResult().ifPresent(r -> handleWindowResult(r, player)));
        player.getInputListener().reset();
    }

    private void handleWindowResult(InventoryWindow.InventoryWindowResult result, Player player) {
        if (result.action() == InventoryWindow.ItemAction.USE) {
            var useable = result.item().<Useable>getFeature(FeatureType.USEABLE);
            if (useable == null) {
                Hud.addMessage(result.item().getName() + " - nie można użyć");
                return;
            }

            player.takeAction(new UseAction(player, player, result.item()));
        }
    }
}
