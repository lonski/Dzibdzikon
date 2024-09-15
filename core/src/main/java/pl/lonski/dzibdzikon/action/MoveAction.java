package pl.lonski.dzibdzikon.action;

import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.screen.Hud;

public class MoveAction implements Action {

    private final Entity entity;
    private final Point target;
    private boolean done = false;

    private final MoveAnimationAction moveAnimation;

    public MoveAction(Entity entity, Point target) {
        this.entity = entity;
        this.target = target;
        this.moveAnimation = new MoveAnimationAction(entity, target);
    }

    @Override
    public void update(float delta, World world) {
        moveAnimation.update(delta, world);
        if (entity.getFeature(FeatureType.PLAYER) != null) {
            world.getPlayer()
                    .setCameraPosition(
                            entity.<Position>getFeature(FeatureType.POSITION).getRenderPosition());
        }
        if (moveAnimation.isDone()) {
            finishMove(world);
        }
    }

    private void finishMove(World world) {
        Position pos = entity.getFeature(FeatureType.POSITION);
        pos.setCoords(target);
        if (entity instanceof Player) {
            world.getCurrentLevel()
                    .getEntityAt(target, FeatureType.PICKABLE)
                    .ifPresent(item ->
                            Hud.addMessage("Leży tutaj " + item.getName().toLowerCase()));
        }
        done = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
