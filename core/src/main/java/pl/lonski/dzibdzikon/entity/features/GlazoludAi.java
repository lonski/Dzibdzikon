package pl.lonski.dzibdzikon.entity.features;

import com.badlogic.gdx.graphics.Color;
import pl.lonski.dzibdzikon.DzibdziRandom;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.MoveAction;
import pl.lonski.dzibdzikon.animation.RollingAnimation;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.EntityFactory;
import pl.lonski.dzibdzikon.map.Line;
import pl.lonski.dzibdzikon.screen.Hud;

public class GlazoludAi extends MonsterAi {

    private int rockRollCooldown = 0;

    public GlazoludAi(Entity entity) {
        super(entity);
    }

    @Override
    public void update(float delta, World world) {
        if (!init(world)) {
            return;
        }

        if (attackEnemyAtNeighbourTile(world)) {
            return;
        }

        if (rollRock(world)) {
            return;
        }

        if (chasePlayer(world)) {
            return;
        }

        mindlessWander(world);
    }

    private boolean rollRock(World world) {
        if (rockRollCooldown > 0) {
            rockRollCooldown--;
            return false;
        }

        if (!seesPlayer(world)) {
            return false;
        }

        var line = Line.calculateStrightOrDiagonal(myPos.getCoords(), playerPos.getCoords(), false);
        if (line.size() <= 1) {
            return false;
        }

        var rockPos = line.get(0);
        if (world.getCurrentLevel().isObstacle(rockPos)) {
            return false;
        }

        var direction = new Point(line.get(1).sub(line.get(0)));
        var rock = EntityFactory.createRollingRock(direction);
        rock.addAnimation(new RollingAnimation(direction.x() < 0 ? 10 : -10, rock));
        world.getCurrentLevel().addEntity(rock, myPos.getCoords());
        rock.getPosition().setzLevel(200);

        Hud.addMessage("Głazolud rzuca ogromny głaz!", Color.ORANGE);

        entity.takeAction(new MoveAction(rock, rockPos));
        rockRollCooldown = DzibdziRandom.nextInt(8, 16);

        return true;
    }
}
