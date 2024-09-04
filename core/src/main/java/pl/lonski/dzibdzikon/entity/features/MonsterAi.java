package pl.lonski.dzibdzikon.entity.features;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.AttackAction;
import pl.lonski.dzibdzikon.action.MoveAction;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.map.MapUtils;

public class MonsterAi implements EntityFeature {

    private final Entity entity;
    private Point lastSeenPlayerPos;
    private List<Point> path;

    public MonsterAi(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void update(float delta, World world) {
        if (entity.getCurrentAction() != null) {
            return;
        }

        if (!entity.alive()) {
            return;
        }

        Position myPos = entity.getFeature(FeatureType.POSITION);
        Entity player = world.getPlayer();
        Position playerPos = player.getFeature(FeatureType.POSITION);

        if (path != null && path.isEmpty()) {
            path = null;
        }

        if (lastSeenPlayerPos != null && lastSeenPlayerPos == myPos.getCoords()) {
            lastSeenPlayerPos = null;
        }

        // attack if player in neighbour position
        Set<Point> nbPositions = MapUtils.getNeighbourPositions(myPos.getCoords());
        if (nbPositions.contains(playerPos.getCoords())) {
            lastSeenPlayerPos = playerPos.getCoords();
            entity.setCurrentAction(new AttackAction(entity, player));
            return;
        }

        // chase player
        FieldOfView fov = entity.getFeature(FeatureType.FOV);
        var playerInFov = fov.getVisible().contains(playerPos.getCoords());
        if (path != null || playerInFov) {

            if (path == null || (playerInFov && !Objects.equals(lastSeenPlayerPos, playerPos.getCoords()))) {
                path = MapUtils.pathfind(myPos.getCoords(), playerPos.getCoords(), p -> !world.getCurrentLevel()
                    .isObstacle(p));
            }

            if (path.isEmpty() && playerInFov) {
                // try to find path without considering entites
                path = MapUtils.pathfind(myPos.getCoords(), playerPos.getCoords(), p -> !world.getCurrentLevel()
                    .isObstacle(p, false));
            }

            if (!path.isEmpty()) {
                var newPos = path.remove(0);
                if (!world.getCurrentLevel().isObstacle(newPos)) {
                    entity.setCurrentAction(new MoveAction(entity, newPos));
                } else {
                    path = null;
                }
            }

            if (playerInFov) {
                lastSeenPlayerPos = playerPos.getCoords();
            }

            return;
        }

        mindlessWander(world);
    }

    protected void mindlessWander(World world) {
        var pos = entity.<Position>getFeature(FeatureType.POSITION);

        // wander
        var dx = Dzibdzikon.RANDOM.nextInt(-1, 2);
        var dy = Dzibdzikon.RANDOM.nextInt(-1, 2);

        var targetPos = new Point(pos.getCoords().x() + dx, pos.getCoords().y() + dy);
        if (!world.getCurrentLevel().isObstacle(targetPos)) {
            entity.setCurrentAction(new MoveAction(entity, targetPos));
        }
    }
}
