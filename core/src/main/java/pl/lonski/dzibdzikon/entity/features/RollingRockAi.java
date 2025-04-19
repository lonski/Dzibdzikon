package pl.lonski.dzibdzikon.entity.features;

import com.badlogic.gdx.graphics.Color;
import java.util.List;
import pl.lonski.dzibdzikon.DzibdziRandom;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.Action;
import pl.lonski.dzibdzikon.action.AttackAction;
import pl.lonski.dzibdzikon.action.AttackAnimationAction;
import pl.lonski.dzibdzikon.action.ChainAction;
import pl.lonski.dzibdzikon.action.CustomAction;
import pl.lonski.dzibdzikon.action.MoveAction;
import pl.lonski.dzibdzikon.action.RemoveEntityAction;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.screen.Hud;

public class RollingRockAi extends MonsterAi {

    private final Point direction; // position change every turn

    public RollingRockAi(Entity entity, Point direction) {
        super(entity);
        this.direction = direction;
    }

    @Override
    public void update(float delta, World world) {
        if (!init(world)) {
            return;
        }

        entity.takeAction(takeTurn(world));
    }

    private Action takeTurn(World world) {
        var newPos = myPos.getCoords().add(direction);
        if (!world.getCurrentLevel().isObstacle(newPos)) {
            return new MoveAction(entity, newPos);
        }

        var level = world.getCurrentLevel();
        var map = level.getMap();

        // if on next rock position is a mob
        var mob = level.getEntityAt(newPos, FeatureType.ATTACKABLE);
        if (mob != null) {
            var mobPos = mob.<Position>getFeature(FeatureType.POSITION);

            // try to push back mob
            var nextRollingRockPos = newPos.add(direction);
            if (!level.isObstacle(nextRollingRockPos, true)) {
                mobPos.setCoords(nextRollingRockPos, entity, level);
                return new ChainAction(List.of(new MoveAction(entity, newPos), new AttackAction(entity, mob, false)));
            }

            // cant push back mob because tile next to mob is wall
            if (map.getTile(nextRollingRockPos).isWall()) {
                return new ChainAction(List.of(
                        new MoveAction(entity, newPos),
                        new AttackAction(entity, mob, false),
                        new RemoveEntityAction(entity),
                        new CustomAction(
                                w -> Hud.addMessage("Głaz uderza w ścianę i rozbija się na kawałki", Color.ORANGE))));
            }

            // cant push back mob because another blocking entity behind this mob
            // - roll over the mob
            return new ChainAction(List.of(new MoveAction(entity, newPos), new AttackAction(entity, mob, false)));
        }

        // if on next rock position is a openable
        // - it has to be 'obstacle' because of check above at line 39
        var openable = level.getEntityAt(newPos, FeatureType.OPENABLE);
        if (openable != null) {
            return new ChainAction(List.of(new MoveAction(entity, newPos), new CustomAction(w -> {
                level.removeEntity(openable);
                Hud.addMessage("Głaz niszczy " + openable.getName().toLowerCase() + "!", Color.ORANGE);
            })));
        }

        // if on next rock position is a wall
        if (map.getTile(newPos).isWall()) {
            if (!map.isBorderTile(newPos) && DzibdziRandom.nextBoolean()) {
                return new ChainAction(List.of(new AttackAnimationAction(entity, newPos), new CustomAction(w -> {
                    // destroy wall
                    var floorTile = map.getTile(myPos.getCoords());
                    map.setTile(newPos, floorTile);
                    level.removeEntity(entity);
                    Hud.addMessage("Głaz kruszy ścianę po uderzeniu w nią z wielkim impetem!", Color.ORANGE);
                })));
            } else {
                return new ChainAction(List.of(new AttackAnimationAction(entity, newPos), new CustomAction(w -> {
                    Hud.addMessage("Głaz uderza w ścianę i rozbija się na kawałki", Color.ORANGE);
                    level.removeEntity(entity);
                })));
            }
        }

        return null;
    }
}
