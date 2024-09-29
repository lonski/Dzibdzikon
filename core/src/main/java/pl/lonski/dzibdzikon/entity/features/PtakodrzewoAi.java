package pl.lonski.dzibdzikon.entity.features;

import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.EntityFactory;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.map.MapUtils;
import pl.lonski.dzibdzikon.map.TextureId;

import java.util.List;

public class PtakodrzewoAi extends MonsterAi {

    private static final List<TextureId> BIRDS =
            List.of(TextureId.MOB_BIRD_PLANKER, TextureId.MOB_BIRD_THROWER, TextureId.MOB_BIRD_BITER);
    private static final int MAX_BIRDS_SPAWNED = 3;
    private static final float BIRD_SPAWN_CHANCE = 0.4f;
    private static final int BIRD_SPAWN_COOLDOWN_TURNS = 5;
    private int birdsLeft = 7;
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

        if (Dzibdzikon.RANDOM.nextFloat() < BIRD_SPAWN_CHANCE) {
            return;
        }

        Entity spawnedBird =
                switch (BIRDS.get(Dzibdzikon.RANDOM.nextInt(BIRDS.size()))) {
                    case MOB_BIRD_BITER -> EntityFactory.createBirdBiter();
                    case MOB_BIRD_PLANKER -> EntityFactory.createBirdPlanker();
                    case MOB_BIRD_THROWER -> EntityFactory.createBirdThrower();
                    default -> null;
                };

        if (spawnedBird == null) {
            return;
        }

        var possiblePositions = MapUtils.getNeighbourPositions(myPos.getCoords()).stream()
                .filter(p -> !world.getCurrentLevel().isObstacle(p, true))
                .toList();
        var position = possiblePositions.get(Dzibdzikon.RANDOM.nextInt(possiblePositions.size()));
        spawnedBird.addFeature(FeatureType.POSITION, new Position(position));

        world.getCurrentLevel().addEntity(spawnedBird);

        birdsLeft -= 1;
        currentBirdSpawnCd = BIRD_SPAWN_COOLDOWN_TURNS;
    }

    private List<Entity> getNearbyBirds(World world) {
        return world.getCurrentLevel().getEntitiesAtCircle(myPos.getCoords(), 10, FeatureType.ATTACKABLE).stream()
                .filter(e -> BIRDS.contains(e.getGlyph()))
                .toList();
    }
}
