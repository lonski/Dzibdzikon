package pl.lonski.dzibdzikon;

import static pl.lonski.dzibdzikon.Dzibdzikon.SHOW_WHOLE_LEVEL;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;

import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.FieldOfView;
import pl.lonski.dzibdzikon.entity.features.Position;

public class World {

    private long turn = 0;
    private Level currentLevel;

    private final Player player;
    private Entity currentEntity;

    public World(Dzibdzikon game) {
        player = new Player(game);
        DzibdziInput.listeners.add(player.getInputListener());
        nextLevel();
        currentEntity = player;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean visible(Entity entity) {
        var entityPos = entity.<Position>getFeature(FeatureType.POSITION).getCoords();
        return SHOW_WHOLE_LEVEL
                || getCurrentLevel().getVisible().contains(entityPos)
                || (getCurrentLevel().getVisited().contains(entityPos) && entity.isVisibleInFog());
    }

    public void update(float delta) {
        // update all entities
        while (currentLevel.getEntities().indexOf(currentEntity)
                < currentLevel.getEntities().size()) {

            // take new turn
            if (currentEntity.getCurrentAction() == null) {

                // cant take turn because has no energy, recharge energy and proceed to next entity
                if (!currentEntity.canTakeAction()) {
                    currentEntity.rechargeEnergy();
                    proceedToNextEntity();
                    continue;
                }

                // take turn, update entity to get action
                currentEntity.update(delta, this);

                // new action set, use energy for it
                if (currentEntity.getCurrentAction() != null) {
                    currentEntity.useEnergyForAction();
                }
            }

            // entity did not take a turn, give it another chance
            if (currentEntity.getCurrentAction() == null) {
                break;
            }

            // perform the action
            currentEntity.getCurrentAction().update(delta, this);

            // action not done yet, break to continue at the next update
            if (!currentEntity.getCurrentAction().isDone()) {
                break;
            }

            // action failed, clear it, do not take turn - break, to acquire new action on next update
            if (!currentEntity.getCurrentAction().succeeded()) {
                currentEntity.setCurrentAction(null);
                break;
            }

            // action succeeded, clear it to make ready for next turn
            currentEntity.setCurrentAction(null);

            // check if entity can take another action, if not proceed to next entity
            if (!currentEntity.canTakeAction()) {
                if (isLastEntity()) {
                    turn++;
                }
                proceedToNextEntity();
            }
        }

        // update all entities animations
        currentLevel.getEntities().forEach(e -> e.updateAnimation(delta, this));

        // update map visibility & fov
        currentLevel.getVisible().clear();
        player.<FieldOfView>getFeature(FeatureType.FOV).getVisible().forEach(p -> {
            currentLevel.getVisited().add(p);
            currentLevel.getVisible().add(p);
        });
    }

    private void proceedToNextEntity() {
        // calculate index to handle entities removed during update
        var currentEntityIdx = currentLevel.getEntities().indexOf(currentEntity);
        var nextEntityIdx = (currentEntityIdx + 1) % currentLevel.getEntities().size();
        currentEntity = currentLevel.getEntities().get(nextEntityIdx);
    }

    private boolean isLastEntity() {
        var currentEntityIdx = currentLevel.getEntities().indexOf(currentEntity);
        return currentEntityIdx == currentLevel.getEntities().size() - 1;
    }

    public void nextLevel() {
        currentLevel = LevelFactory.generate();
        currentLevel.addEntity(player);
        int maxTries = 5;
        while (maxTries-- > 0) {
            var pos = currentLevel.getMap().getRandomRoom().getCenter();
            if (currentLevel.getEntitiesAt(pos, null).isEmpty()) {
                player.<Position>getFeature(FeatureType.POSITION).setCoords(pos);
                player.setCameraPosition(new Point(pos.x() * TILE_WIDTH, pos.y() * TILE_HEIGHT));
                break;
            }
        }
    }

    public long getTurn() {
        return turn;
    }
}
