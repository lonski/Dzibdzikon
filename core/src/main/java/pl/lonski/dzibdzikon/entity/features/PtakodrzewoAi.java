package pl.lonski.dzibdzikon.entity.features;

import com.badlogic.gdx.graphics.Color;
import java.util.List;
import java.util.stream.Collectors;
import pl.lonski.dzibdzikon.DzibdziRandom;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.MoveAction;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.EntityFactory;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.map.MapUtils;
import pl.lonski.dzibdzikon.screen.Hud;

public class PtakodrzewoAi extends MonsterAi {

    private static final List<String> BIRDS = List.of(
            EntityFactory.createBirdPlanker().getName(),
            EntityFactory.createBirdBiter().getName(),
            EntityFactory.createBirdThrower().getName());
    private static final int MAX_BIRDS_SPAWNED = 10;
    private static final float BIRD_SPAWN_CHANCE = 0.8f;
    private static final int BIRD_SPAWN_COOLDOWN_TURNS = 2;
    private int birdsLeft = 50;
    private int currentBirdSpawnCd = 0;

    public PtakodrzewoAi(Entity entity) {
        super(entity);
    }

    @Override
    public void update(float delta, World world) {
        if (!init(world)) {
            return;
        }

        currentBirdSpawnCd -= 1;

        if (birdsLeft <= 0) {
            return;
        }

        // do nothing if not attacked
        var attackable = entity.<Attackable>getFeature(FeatureType.ATTACKABLE);
        if (attackable.getHp() >= attackable.getMaxHp()) {
            return;
        }

        // do nothing if player not in view
        if (!seesPlayer(world)) {
            return;
        }

        maybeSpawnBird(world);
    }

    private void maybeSpawnBird(World world) {
        if (currentBirdSpawnCd > 0) {
            return;
        }

        var birdsNearby = getNearbyBirds(world);
        if (birdsNearby.size() >= MAX_BIRDS_SPAWNED) {
            return;
        }

        if (DzibdziRandom.nextDouble() < BIRD_SPAWN_CHANCE) {
            return;
        }

        Entity spawnedBird =
                switch (DzibdziRandom.nextInt(3)) {
                    case 0 -> EntityFactory.createBirdBiter();
                    case 1 -> EntityFactory.createBirdPlanker();
                    case 2 -> EntityFactory.createBirdThrower();
                    default -> null;
                };

        var possiblePositions = MapUtils.getNeighbourPositions(myPos.getCoords()).stream()
                .filter(p -> !world.getCurrentLevel().isObstacle(p, true))
                .collect(Collectors.toList());
        var birdPos = possiblePositions.get(DzibdziRandom.nextInt(possiblePositions.size()));
        world.getCurrentLevel().addEntity(spawnedBird, myPos.getCoords());

        Hud.addMessage("Z drzewa wylatuje ptak, aby go bronić!", Color.ORANGE);
        entity.takeAction(new MoveAction(spawnedBird, birdPos));

        birdsLeft -= 1;
        currentBirdSpawnCd = BIRD_SPAWN_COOLDOWN_TURNS;
    }

    private List<Entity> getNearbyBirds(World world) {
        return world.getCurrentLevel().getEntitiesAtCircle(myPos.getCoords(), 10, FeatureType.ATTACKABLE).stream()
                .filter(e -> BIRDS.contains(e.getName()))
                .collect(Collectors.toList());
    }
}
