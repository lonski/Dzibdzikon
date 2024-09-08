package pl.lonski.dzibdzikon;

import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.FieldOfView;
import pl.lonski.dzibdzikon.entity.features.Position;

import static pl.lonski.dzibdzikon.Dzibdzikon.SHOW_WHOLE_LEVEL;

public class World {

    private Level currentLevel;

    private final Player player = new Player();
    private Entity currentEntity = player;

    public World() {
        DzibdziInput.listeners.add(player.getInputListener());
        nextLevel();
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
        // break if any entity is still performing an action or did not take a turn yet
        while (currentLevel.getEntities().indexOf(currentEntity)
            < currentLevel.getEntities().size()) {

            // no action, update entity to take turn
            if (currentEntity.getCurrentAction() == null) {
                currentEntity.update(delta, this);
            }

            // entity did not take a turn, give it another chance
            if (currentEntity.getCurrentAction() == null) {
                break;
            }

            // perform action
            currentEntity.getCurrentAction().update(delta, this);

            // action not done yet
            if (!currentEntity.getCurrentAction().isDone()) {
                break;
            }

            // action failed, clear it, but do not take turn
            if (!currentEntity.getCurrentAction().succeeded()) {
                currentEntity.setCurrentAction(null);
                break;
            }

            currentEntity.setCurrentAction(null);

            // proceed to next entity
            // recalculate the idx in case the entity was removed during the update
            var currentEntityIdx = currentLevel.getEntities().indexOf(currentEntity);
            var nextEntityIdx =
                (currentEntityIdx + 1) % currentLevel.getEntities().size();
            currentEntity = currentLevel.getEntities().get(nextEntityIdx);
        }

        // update map visibility & fov
        currentLevel.getVisible().clear();
        player.<FieldOfView>getFeature(FeatureType.FOV).getVisible().forEach(p -> {
            currentLevel.getVisited().add(p);
            currentLevel.getVisible().add(p);
        });
    }

    public void nextLevel() {
        currentLevel = LevelFactory.generate();
        currentLevel.addEntity(player);
        int maxTries = 5;
        while (maxTries-- > 0) {
            var pos = currentLevel.getMap().getRandomRoom().getCenter();
            if (currentLevel.getEntitiesAt(pos, null).isEmpty()) {
                player.<Position>getFeature(FeatureType.POSITION).setCoords(pos);
                break;
            }
        }
    }
}
