package pl.lonski.dzibdzikon.entity.features;

import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.MoveAction;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;

public class MonsterAi implements EntityFeature {

    private final Entity entity;

    public MonsterAi(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void update(float delta, World world) {
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
