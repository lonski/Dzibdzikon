package pl.lonski.dzibdzikon.entity.features;

import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.MoveAction;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.map.MapUtils;
import pl.lonski.dzibdzikon.map.TextureId;

import java.util.List;
import java.util.Optional;

public class BirdPlankerAi extends RangeAttackerAi {

    private boolean hasPlank = true;
    private List<Point> pathToTree;

    public BirdPlankerAi(Entity entity) {
        super(entity);
        allowRangeAttackOnNeighbourTile = true;
    }

    @Override
    public void update(float delta, World world) {
        if (!init(world)) {
            return;
        }

        if (fetchPlank(world)) {
            entity.setGlyph(hasPlank ? TextureId.MOB_BIRD_PLANKER : TextureId.MOB_BIRD_PLANKER_EMPTY);
            return;
        }

        if (seesPlayer(world)) {
            if (rangeAttack()) {
                entity.setGlyph(hasPlank ? TextureId.MOB_BIRD_PLANKER : TextureId.MOB_BIRD_PLANKER_EMPTY);
                return;
            }

            if (attackEnemyAtNeighbourTile(world)) {
                return;
            }
        }

        if (chasePlayer(world)) {
            return;
        }

        mindlessWander(world);
    }

    private boolean fetchPlank(World world) {
        if (hasPlank) {
            return false;
        }

        // tree on neighour tile - take plank
        var neighbourTree = MapUtils.getNeighbourPositions(myPos.getCoords()).stream()
                .map(p -> world.getCurrentLevel().getEntityAt(p, FeatureType.PTAKODRZEWO))
                .filter(Optional::isPresent)
                .flatMap(Optional::stream)
                .findFirst();
        if (neighbourTree.isPresent()) {
            hasPlank = true;
            var treeAttackable = neighbourTree.get().<Attackable>getFeature(FeatureType.ATTACKABLE);
            // TODO: apply effect damage
            // take tree hp
            return false;
        }

        // on a path to tree, follow
        if (pathToTree != null && !pathToTree.isEmpty()) {
            var newPos = pathToTree.remove(0);
            if (!world.getCurrentLevel().isObstacle(newPos)) {
                entity.takeAction(new MoveAction(entity, newPos));
                return true;
            } else {
                pathToTree = null; // cant follow path, try find new one
            }
        }

        // try to find a tree to go for plank
        var treesNearby = world.getCurrentLevel().getEntitiesAtCircle(myPos.getCoords(), 10, FeatureType.PTAKODRZEWO);
        if (treesNearby.isEmpty()) {
            return false;
        }

        var tree = treesNearby.get(0);
        var treePos = tree.<Position>getFeature(FeatureType.POSITION);
        pathToTree = MapUtils.pathfind(myPos.getCoords(), treePos.getCoords(), p -> !world.getCurrentLevel()
                .isObstacle(p));

        if (pathToTree.isEmpty()) {
            return false; // cant find path; skip this action
        }

        var newPos = pathToTree.remove(0);
        entity.takeAction(new MoveAction(entity, newPos));

        return true;
    }

    @Override
    protected boolean rangeAttack() {
        if (!hasPlank) {
            return false;
        }

        if (super.rangeAttack()) {
            hasPlank = false;
            return true;
        }

        return false;
    }
}
