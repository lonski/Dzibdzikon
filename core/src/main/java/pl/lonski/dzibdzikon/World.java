package pl.lonski.dzibdzikon;

import static pl.lonski.dzibdzikon.Dzibdzikon.SHOW_WHOLE_LEVEL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import pl.lonski.dzibdzikon.effect.tile.TileEffect;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.FieldOfView;

public class World {

    private long turn = 0;
    private Level currentLevel;

    private final Player player;
    private Entity currentEntity;

    public World() {
        player = new Player();
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
        var entityPos = entity.getPosition().getCoords();
        return SHOW_WHOLE_LEVEL
                || getCurrentLevel().getVisible().contains(entityPos)
                || (getCurrentLevel().getVisited().contains(entityPos) && entity.isVisibleInFog());
    }

    public boolean visible(Point pos) {
        return SHOW_WHOLE_LEVEL || getCurrentLevel().getVisible().contains(pos);
    }

    public void update(float delta) {
        // update all entities
        while (currentLevel.getEntities().indexOf(currentEntity)
                < currentLevel.getEntities().size()) {

            //            // store start pos
            //            var entityPos = currentEntity.<Position>getFeature(FeatureType.POSITION);
            //            var startCoords = entityPos.getCoords();

            // take new turn
            if (currentEntity.getCurrentAction() == null) {

                // cant take turn because has no energy, recharge energy and proceed to next entity
                if (!currentEntity.hasEnergyForAction()) {
                    currentEntity.rechargeEnergy();
                    proceedToNextEntity();
                    continue;
                }

                // take turn, update entity to get action
                currentEntity.update(delta, this);

                // Usually entity gets an action here, but not always.
                // If window is involved to assign an action
                // the action is set in a callback after window is closed,
                // thus at this moment here, entity might not have action yet.
                // The energy for taking an action is consumed in the method that
                // assigns the action to the entity.
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
                currentEntity.cancelCurrentAction();
                break;
            }

            // action succeeded, clear it to make ready for next turn
            currentEntity.clearAction();

            // check if entity can take another action, if not proceed to next entity
            if (!currentEntity.hasEnergyForAction()) {
                if (isLastEntity()) {
                    turn++;
                    // update tile effects
                    var remainedTileEffects = new HashMap<Point, List<TileEffect>>();
                    currentLevel.getTileEffects().forEach((point, tileEffects) -> {
                        var remainedEffectOnTile = new ArrayList<TileEffect>();
                        for (TileEffect tileEffect : tileEffects) {
                            if (!tileEffect.takeTurn(point, this)) {
                                remainedEffectOnTile.add(tileEffect);
                            }
                        }
                        remainedTileEffects.put(point, remainedEffectOnTile);
                    });
                    currentLevel.setTileEffects(remainedTileEffects);
                }

                //                // update entity pos map
                //                if (!entityPos.getCoords().equals(startCoords)) {
                //                    currentLevel.updateEntityPos(currentEntity, startCoords,
                // entityPos.getCoords());
                //                }
                proceedToNextEntity();
            }
        }

        // update all entities animations
        for (Entity entity : currentLevel.getEntities()) {
            entity.updateAnimation(delta, this);
        }

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

        // set next entity as current
        currentEntity = currentLevel.getEntities().get(nextEntityIdx);

        // notify entity new turn started to eg. tick entity effects
        currentEntity.onTurnStarted(this);
    }

    private boolean isLastEntity() {
        var currentEntityIdx = currentLevel.getEntities().indexOf(currentEntity);
        return currentEntityIdx == currentLevel.getEntities().size() - 1;
    }

    public void nextLevel() {
        currentLevel = LevelFactory.generate();
        int maxTries = 5;
        while (maxTries-- > 0) {
            var coords = currentLevel.getMap().getRandomRoom().getCenter();
            if (currentLevel.getEntitiesAt(coords, null).isEmpty() && !currentLevel.isObstacle(coords)) {
                currentLevel.addEntity(player, coords);
                player.setCameraPosition(player.getPosition().getRenderPosition());
                break;
            }
        }
    }

    public long getTurn() {
        return turn;
    }
}
