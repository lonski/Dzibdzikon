package pl.lonski.dzibdzikon.action;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.Inventory;
import pl.lonski.dzibdzikon.entity.features.Useable;
import pl.lonski.dzibdzikon.screen.Hud;

public class UseAction implements Action {

    private final Entity user;
    private final Entity target;
    private final Entity useableEntity;
    private boolean done = false;
    private boolean success = false;

    public UseAction(Entity user, Entity target, Entity useable) {
        this.user = user;
        this.target = target;
        this.useableEntity = useable;
    }

    @Override
    public void update(float delta, World world) {
        if (done) {
            return;
        }

        var useable = useableEntity.<Useable>getFeature(FeatureType.USEABLE);
        if (useable == null) {
            done = true;
            success = false;
            return;
        }

        useable.use(user, target, world);
        if (user instanceof Player) {
            Hud.addMessage("Używasz " + useableEntity.getName().toLowerCase());
        }

        var inventory = user.<Inventory>getFeature(FeatureType.INVENTORY);
        if (inventory != null) {
            inventory.removeItem(useableEntity);
        }

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
