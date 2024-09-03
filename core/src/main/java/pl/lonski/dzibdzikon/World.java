package pl.lonski.dzibdzikon;

import static pl.lonski.dzibdzikon.Dzibdzikon.SHOW_WHOLE_LEVEL;

import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.FieldOfView;
import pl.lonski.dzibdzikon.entity.features.Position;

public class World {

    private Level currentLevel;

    private final Player player = new Player();
    private int currentEntityIdx = 0;

    public World() {
        currentLevel = LevelFactory.generate();
        currentLevel.addEntity(player);
        player.<Position>getFeature(FeatureType.POSITION).setCoords(currentLevel.getMap().getRandomRoom().getCenter());

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

        while (currentEntityIdx < currentLevel.getEntities().size()) {
            var entity = currentLevel.getEntities().get(currentEntityIdx);

            if (entity.getCurrentAction() == null) {
                entity.update(delta, this);
            }

            if (entity.getCurrentAction() == null) {
                break;
            }

            entity.getCurrentAction().update(delta, this);
            if (!entity.getCurrentAction().isDone()) {
                break;
            }

            entity.setCurrentAction(null);
            currentEntityIdx = (currentEntityIdx + 1) % currentLevel.getEntities().size();
        }

        // update map visibility & fov
        currentLevel.getVisible().clear();
        player.<FieldOfView>getFeature(FeatureType.FOV).getVisible().forEach(p -> {
            currentLevel.getVisited().add(p);
            currentLevel.getVisible().add(p);
        });
    }
}
