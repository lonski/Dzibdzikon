package pl.lonski.dzibdzikon.action;

import com.badlogic.gdx.graphics.Color;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.Inventory;
import pl.lonski.dzibdzikon.screen.Hud;

public class PickupAction implements Action {

    private final Entity pickuper;
    private final Entity item;
    private boolean done = false;
    private boolean success = false;

    public PickupAction(Entity pickuper, Entity item) {
        this.pickuper = pickuper;
        this.item = item;
    }

    @Override
    public void update(float delta, World world) {
        if (isDone()) {
            return;
        }

        var inventory = pickuper.<Inventory>getFeature(FeatureType.INVENTORY);

        if (inventory == null) {
            done = true;
            success = false;
            return;
        }

        if (!inventory.addItem(item)) {
            done = true;
            success = false;
            return;
        }

        if (pickuper instanceof Player) {
            Hud.addMessage("Podniesiono " + item.getName().toLowerCase(), Color.CHARTREUSE);
        }

        world.getCurrentLevel().removeEntity(item);
        done = true;
        success = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public boolean succeeded() {
        return success;
    }
}
