package pl.lonski.dzibdzikon;

import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.map.RoomMapGeneratorV2;

public class World {

    private Level currentLevel;

    private final Player player = new Player();
    private int currentEntityIdx = 0;

    public World() {
        currentLevel = new Level(RoomMapGeneratorV2.generate(160, 48));
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
        return true;
//        return SHOW_WHOLE_LEVEL
//            || getCurrentLevel().getVisible().contains(entityPos)
//            || (getCurrentLevel().getVisited().contains(entityPos) && entity.isVisibleInFog());
    }

    public void update(float delta) {

        if (currentEntityIdx < currentLevel.getEntities().size()) {
            var entity = currentLevel.getEntities().get(currentEntityIdx);

            if (entity.getCurrentAction() == null) {
                entity.update(delta, this);
                if (entity.getCurrentAction() == null) {
                    currentEntityIdx = (currentEntityIdx + 1) % currentLevel.getEntities().size();
                }
            } else {
                entity.getCurrentAction().update(delta);
                if (entity.getCurrentAction().isDone()) {
                    entity.setCurrentAction(null);
                    currentEntityIdx = (currentEntityIdx + 1) % currentLevel.getEntities().size();
                }
            }
        }
    }
}
