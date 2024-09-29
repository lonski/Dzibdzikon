package pl.lonski.dzibdzikon.entity;

import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.entity.features.Attackable;
import pl.lonski.dzibdzikon.entity.features.DoorOpenable;
import pl.lonski.dzibdzikon.entity.features.Downstairs;
import pl.lonski.dzibdzikon.entity.features.FieldOfView;
import pl.lonski.dzibdzikon.entity.features.GlazoludAi;
import pl.lonski.dzibdzikon.entity.features.HealingUseable;
import pl.lonski.dzibdzikon.entity.features.MonsterAi;
import pl.lonski.dzibdzikon.entity.features.Pickable;
import pl.lonski.dzibdzikon.entity.features.PtakodrzewoAi;
import pl.lonski.dzibdzikon.entity.features.RangeAttackable;
import pl.lonski.dzibdzikon.entity.features.Regeneration;
import pl.lonski.dzibdzikon.entity.features.RollingRockAi;
import pl.lonski.dzibdzikon.entity.features.RollingRockAttackable;
import pl.lonski.dzibdzikon.entity.features.RangeAttackerAi;
import pl.lonski.dzibdzikon.map.TextureId;

public class EntityFactory {

    public static Entity createZombie() {
        var zombie = new Entity("Zombie", TextureId.MOB_ZOMBIE);
        zombie.addFeature(FeatureType.ATTACKABLE, new Attackable(8, 10, 1, 0));
        zombie.addFeature(FeatureType.AI, new MonsterAi(zombie));
        zombie.addFeature(FeatureType.FOV, new FieldOfView(zombie, 8));
        zombie.addFeature(FeatureType.REGENERATION, new Regeneration(7, zombie));
        return zombie;
    }

    public static Entity createDoor(boolean opened) {
        var door = new Entity("Drzwi", opened ? TextureId.DOOR_OPEN : TextureId.DOOR_CLOSED);
        door.setVisibleInFog(true);
        door.addFeature(FeatureType.OPENABLE, new DoorOpenable(door));
        return door;
    }

    public static Entity createDownstairs() {
        var stairs = new Entity("Schody w dół", TextureId.DOWNSTAIRS);
        stairs.setVisibleInFog(true);
        stairs.addFeature(FeatureType.DOWNSTAIRS, new Downstairs());
        return stairs;
    }

    public static Entity createGlazolud() {
        var glazolud = new Entity("Głazolud", TextureId.MOB_GLAZOLUD);
        glazolud.addFeature(FeatureType.ATTACKABLE, new Attackable(15, 15, 2, 1));
        glazolud.addFeature(FeatureType.AI, new GlazoludAi(glazolud));
        glazolud.addFeature(FeatureType.FOV, new FieldOfView(glazolud, 8));
        glazolud.addFeature(FeatureType.REGENERATION, new Regeneration(20, glazolud));
        glazolud.setSpeed(0.8f);
        return glazolud;
    }

    public static Entity createRollingRock(Point direction) {
        var rock = new Entity("Głaz", TextureId.MOB_BIG_ROCK);
        rock.addFeature(FeatureType.ATTACKABLE, new RollingRockAttackable(3, 30, 8));
        rock.addFeature(FeatureType.AI, new RollingRockAi(rock, direction));
        rock.setSpeed(2f);
        return rock;
    }

    public static Entity createHealingPotion() {
        var potion = new Entity("Mikstura leczenia", TextureId.POTION_RED);
        potion.addFeature(FeatureType.PICKABLE, new Pickable());
        potion.addFeature(FeatureType.USEABLE, new HealingUseable(4, 8));
        return potion;
    }

    public static Entity createPtakodrzewo() {
        var ptakodrzewo = new Entity("Ptakodrzewo", TextureId.MOB_PTAKODRZEWO);
        ptakodrzewo.addFeature(FeatureType.ATTACKABLE, new Attackable(50, 50, 0, 4));
        ptakodrzewo.addFeature(FeatureType.AI, new PtakodrzewoAi(ptakodrzewo));
        ptakodrzewo.addFeature(FeatureType.FOV, new FieldOfView(ptakodrzewo, 10));
        ptakodrzewo.addFeature(FeatureType.REGENERATION, new Regeneration(3, ptakodrzewo));
        return ptakodrzewo;
    }

    public static Entity createBirdPlanker() {
        var bird = new Entity("Kłodoptak", TextureId.MOB_BIRD_PLANKER);
        bird.addFeature(FeatureType.ATTACKABLE, new Attackable(10, 10, 1, 1));
        bird.addFeature(FeatureType.RANGE_ATTACKABLE, new RangeAttackable(TextureId.PLANK, 2, 2));
        bird.addFeature(FeatureType.AI, new RangeAttackerAi(bird));
        bird.addFeature(FeatureType.FOV, new FieldOfView(bird, 8));
        bird.addFeature(FeatureType.REGENERATION, new Regeneration(8, bird));
        return bird;
    }

    public static Entity createBirdThrower() {
        var bird = new Entity("Rzucoptak", TextureId.MOB_BIRD_THROWER);
        bird.addFeature(FeatureType.ATTACKABLE, new Attackable(10, 10, 1, 1));
        bird.addFeature(FeatureType.AI, new MonsterAi(bird));
        bird.addFeature(FeatureType.FOV, new FieldOfView(bird, 8));
        bird.addFeature(FeatureType.REGENERATION, new Regeneration(8, bird));
        return bird;
    }

    public static Entity createBirdBiter() {
        var bird = new Entity("Dzioboptak", TextureId.MOB_BIRD_BITER);
        bird.addFeature(FeatureType.ATTACKABLE, new Attackable(10, 10, 3, 1));
        bird.addFeature(FeatureType.AI, new MonsterAi(bird));
        bird.addFeature(FeatureType.FOV, new FieldOfView(bird, 8));
        bird.addFeature(FeatureType.REGENERATION, new Regeneration(8, bird));
        return bird;
    }
}
