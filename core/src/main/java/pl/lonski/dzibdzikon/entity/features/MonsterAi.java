package pl.lonski.dzibdzikon.entity.features;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import pl.lonski.dzibdzikon.DzibdziRandom;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.AttackAction;
import pl.lonski.dzibdzikon.action.MoveAction;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.map.MapUtils;
import pl.lonski.dzibdzikon.map.Position;

public class MonsterAi implements EntityFeature {

    protected final Entity entity;
    protected Point lastSeenPlayerPos;
    protected List<Point> path;
    protected Position myPos;
    protected Entity player;
    protected Position playerPos;

    public MonsterAi(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void update(float delta, World world) {
        if (!init(world)) {
            return;
        }

        if (seesPlayer(world) && attackEnemyAtNeighbourTile(world)) {
            return;
        }

        if (chasePlayer(world)) {
            return;
        }

        mindlessWander(world);
    }

    protected boolean seesPlayer(World world) {
        FieldOfView fov = entity.getFeature(FeatureType.FOV);
        var playerInFov = fov.getVisible().contains(playerPos.getCoords());

        return playerInFov;
    }

    protected boolean init(World world) {
        if (entity.getCurrentAction() != null) {
            return false;
        }

        if (!entity.alive()) {
            return false;
        }

        myPos = entity.getPosition();
        player = world.getPlayer();
        playerPos = player.getPosition();

        if (path != null && path.isEmpty()) {
            path = null;
        }

        if (lastSeenPlayerPos != null && lastSeenPlayerPos == myPos.getCoords()) {
            lastSeenPlayerPos = null;
        }

        return true;
    }

    protected boolean attackEnemyAtNeighbourTile(World world) {
        Set<Point> nbPositions = MapUtils.getNeighbourPositions(myPos.getCoords());
        if (nbPositions.contains(playerPos.getCoords())) {
            lastSeenPlayerPos = playerPos.getCoords();
            entity.takeAction(new AttackAction(entity, player));
            return true;
        }
        return false;
    }

    protected boolean chasePlayer(World world) {
        var seesPlayer = seesPlayer(world);
        if (path != null || seesPlayer) {

            if (path == null || (seesPlayer && !Objects.equals(lastSeenPlayerPos, playerPos.getCoords()))) {
                path = MapUtils.pathfind(myPos.getCoords(), playerPos.getCoords(), p -> !world.getCurrentLevel()
                        .isObstacle(p));
            }

            if (path.isEmpty() && seesPlayer) {
                // try to find path without considering entites
                path = MapUtils.pathfind(myPos.getCoords(), playerPos.getCoords(), p -> !world.getCurrentLevel()
                        .isObstacle(p, false));
            }

            if (!path.isEmpty()) {
                var newPos = path.remove(0);
                if (!world.getCurrentLevel().isObstacle(newPos)) {
                    entity.takeAction(new MoveAction(entity, newPos));
                } else {
                    path = null;
                }
            }

            if (seesPlayer) {
                lastSeenPlayerPos = playerPos.getCoords();
            }

            return true;
        }

        return false;
    }

    protected void mindlessWander(World world) {
        var pos = entity.getPosition();

        // wander
        var dx = DzibdziRandom.nextInt(-1, 2);
        var dy = DzibdziRandom.nextInt(-1, 2);

        var targetPos = new Point(pos.getCoords().x() + dx, pos.getCoords().y() + dy);
        if (!world.getCurrentLevel().isObstacle(targetPos)) {
            entity.takeAction(new MoveAction(entity, targetPos));
        }
    }
}
