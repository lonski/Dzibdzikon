package pl.lonski.dzibdzikon.entity.features;

import com.badlogic.gdx.graphics.Color;
import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.Action;
import pl.lonski.dzibdzikon.action.AttackAction;
import pl.lonski.dzibdzikon.action.ChainAction;
import pl.lonski.dzibdzikon.action.CustomAction;
import pl.lonski.dzibdzikon.action.MoveAction;
import pl.lonski.dzibdzikon.action.RemoveEntityAction;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.screen.Hud;

import java.util.List;

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

        entity.setCurrentAction(takeTurn(world));
    }

    private Action takeTurn(World world) {
        var newPos = myPos.getCoords().add(direction);
        if (!world.getCurrentLevel().isObstacle(newPos)) {
            return new MoveAction(entity, newPos);
        }

        var mobOpt = world.getCurrentLevel().getEntityAt(newPos, FeatureType.ATTACKABLE);
        if (mobOpt.isPresent()) {
            var mob = mobOpt.get();

            // try to push back mob
            var nextRollingRockPos = newPos.add(direction);
            if (!world.getCurrentLevel().isObstacle(nextRollingRockPos, true)) {
                mob.<Position>getFeature(FeatureType.POSITION).setCoords(nextRollingRockPos);
                return new ChainAction(List.of(
                    new MoveAction(entity, newPos),
                    new AttackAction(entity, mob, false)
                ));
            }

            //// cant push back mob

            // tile next to mob is wall
            if (world.getCurrentLevel().getMap().getTile(nextRollingRockPos).isWall()) {
                return new ChainAction(List.of(
                    new MoveAction(entity, newPos),
                    new AttackAction(entity, mob, false),
                    new RemoveEntityAction(entity),
                    new CustomAction(() -> Hud.addMessage("Głaz uderza w ścianę i rozbija się na kawałki", Color.ORANGE))
                ));
            }

            // another blocking entity behind this mob
            // TODO
            // - roll over?
            // - add destroying openables?
            // - increase speed? introduce game speed...
        }

        if (world.getCurrentLevel().getMap().getTile(newPos).isWall()) {
            if (Dzibdzikon.RANDOM.nextBoolean()) {
                return new CustomAction(() -> {
                    // destroy wall
                    var floorTile = world.getCurrentLevel().getMap().getTile(myPos.getCoords());
                    world.getCurrentLevel().getMap().setTile(newPos, floorTile);
                    world.getCurrentLevel().removeEntity(entity);
                    Hud.addMessage("Głaz kruszy ścianę po uderzeniu w nią z wielkim impetem!", Color.ORANGE);
                });
            } else {
                return new CustomAction(() -> {
                    Hud.addMessage("Głaz uderza w ścianę i rozbija się na kawałki", Color.ORANGE);
                    world.getCurrentLevel().removeEntity(entity);
                });
            }
        }

        return null;
    }
}
