package pl.lonski.dzibdzikon.action;

import java.util.Optional;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.FieldOfView;
import pl.lonski.dzibdzikon.entity.features.Openable;
import pl.lonski.dzibdzikon.screen.Hud;

public class OpenAction implements Action {

    private boolean done = false;
    private boolean success = false;

    private final Entity actionPerformer;
    private final Entity toOpen;

    public OpenAction(Entity actionPerformer, Entity toOpen) {
        this.actionPerformer = actionPerformer;
        this.toOpen = toOpen;
    }

    private void log(String message) {
        if (actionPerformer.getFeature(FeatureType.PLAYER) != null) {
            Hud.addMessage(message);
        }
    }

    @Override
    public void update(float delta, World world) {
        if (toOpen.getFeature(FeatureType.OPENABLE) == null) {
            log("Nie można otworzyć " + toOpen.getName() + " - nieotwieralne.");
            done = true;
            success = false;
            return;
        }

        Openable openable = toOpen.getFeature(FeatureType.OPENABLE);
        if (openable.opened()) {
            log(toOpen.getName() + " jest/są już otwarte.");
            done = true;
            success = false;
            return;
        }

        openable.open(world);
        Optional.ofNullable(actionPerformer.<FieldOfView>getFeature(FeatureType.FOV))
                .ifPresent(fov -> fov.update(delta, world));

        success = true;
        done = true;
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
