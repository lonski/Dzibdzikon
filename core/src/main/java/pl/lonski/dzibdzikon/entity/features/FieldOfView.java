package pl.lonski.dzibdzikon.entity.features;

import java.util.HashSet;
import java.util.Set;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.map.Line;

public class FieldOfView implements EntityFeature {

    private final int radius;
    private final Entity entity;
    private final Set<Point> visible = new HashSet<>();
    private final Set<Point> hostiles = new HashSet<>();

    public FieldOfView(Entity entity, int radius) {
        this.entity = entity;
        this.radius = radius;
    }

    public Set<Point> getVisible() {
        return visible;
    }

    public Set<Point> getHostiles() {
        return hostiles;
    }

    @Override
    public void update(float delta, World world) {

        var pos = entity.getPosition();

        var x1 = pos.getCoords().x();
        var y1 = pos.getCoords().y();

        visible.clear();
        hostiles.clear();

        for (int ox = -radius; ox <= radius; ox++) {
            for (int oy = -radius; oy <= radius; oy++) {
                if (ox * ox + oy * oy <= radius * radius) {

                    for (Point p : Line.calculate(x1, x1 + ox, y1, y1 + oy)) {
                        visible.add(p);
                        if (world.getCurrentLevel().isOpaque(p)) {
                            break;
                        }
                        for (Entity e : world.getCurrentLevel().getEntitiesAt(p, FeatureType.ATTACKABLE)) {
                            if (e.isHostile(entity)) {
                                hostiles.add(p);
                            }
                        }
                    }
                }
            }
        }

        var x = 1;
    }
}
