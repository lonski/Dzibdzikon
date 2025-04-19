package pl.lonski.dzibdzikon.action;

import java.util.Optional;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.FieldOfView;
import pl.lonski.dzibdzikon.entity.features.Openable;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.screen.Hud;

public class CloseAction implements Action {

    private boolean done = false;
    private boolean success = false;

    private final Entity actionPerformer;
    private final Point toCloseCoords;

    public CloseAction(Entity actionPerformer, Point toClose) {
        this.actionPerformer = actionPerformer;
        this.toCloseCoords = toClose;
    }

    private void log(String message) {
        if (actionPerformer.getFeature(FeatureType.PLAYER) != null) {
            Hud.addMessage(message);
        }
    }

    @Override
    public void update(float delta, World world) {
        var toClose = world.getCurrentLevel().getEntityAt(toCloseCoords, null);

        if (toClose == null) {
            log("Tutaj nie ma nic do zamknięcia");
            done = true;
            return;
        }

        Position openablePos = toClose.getFeature(FeatureType.POSITION);

        if (toClose.getFeature(FeatureType.OPENABLE) == null) {
            log("Nie można zamknąć " + toClose.getName() + " - nieotwieralne.");
            done = true;
            return;
        }

        var entities = world.getCurrentLevel().getEntitiesAt(openablePos.getCoords(), null);
        if (entities.size() > 1) {
            log("Nie można zamknąć " + toClose.getName() + " - zablokowane.");
            done = true;
            return;
        }

        Openable openable = toClose.getFeature(FeatureType.OPENABLE);
        if (!openable.opened()) {
            log("Nie można zamknąć zamkniętego (" + toClose.getName() + ").");
            done = true;
            return;
        }

        openable.close(world);
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
