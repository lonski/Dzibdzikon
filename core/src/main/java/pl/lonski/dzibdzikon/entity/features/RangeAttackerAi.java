package pl.lonski.dzibdzikon.entity.features;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.RangeAttackAction;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.map.MapUtils;

public class RangeAttackerAi extends MonsterAi {

    public RangeAttackerAi(Entity entity) {
        super(entity);
    }

    @Override
    public void update(float delta, World world) {
        if (!init(world)) {
            return;
        }

        if (seesPlayer(world)) {
            if (attackEnemyAtNeighbourTile(world)) {
                return;
            }

            if (rangeAttack()) {
                return;
            }
        }

        if (chasePlayer(world)) {
            return;
        }

        mindlessWander(world);
    }

    protected boolean rangeAttack() {
        var rangeAttackable = entity.<RangeAttackable>getFeature(FeatureType.RANGE_ATTACKABLE);

        if (rangeAttackable == null) {
            return false;
        }

        var enemyPos = playerPos.getCoords();
        var distance = MapUtils.euclideanDistance(enemyPos, myPos.getCoords());

        if (distance < 1.5) { // on neighbour tile
            return false;
        }

        if (Math.round(distance) > rangeAttackable.getRange()) { // out of range
            return false;
        }

        return takeRangeAttackAction();
    }

    protected boolean takeRangeAttackAction() {
        entity.takeAction(new RangeAttackAction(entity, player));
        return true;
    }
}
