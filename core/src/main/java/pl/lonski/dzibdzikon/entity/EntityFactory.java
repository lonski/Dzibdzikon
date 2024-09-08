package pl.lonski.dzibdzikon.entity;

import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.animation.RollingAnimation;
import pl.lonski.dzibdzikon.entity.features.Attackable;
import pl.lonski.dzibdzikon.entity.features.DoorOpenable;
import pl.lonski.dzibdzikon.entity.features.Downstairs;
import pl.lonski.dzibdzikon.entity.features.FieldOfView;
import pl.lonski.dzibdzikon.entity.features.GlazoludAi;
import pl.lonski.dzibdzikon.entity.features.MonsterAi;
import pl.lonski.dzibdzikon.entity.features.RollingRockAi;
import pl.lonski.dzibdzikon.entity.features.RollingRockAttackable;
import pl.lonski.dzibdzikon.map.Glyph;

public class EntityFactory {

    public static Entity createZombie() {
        var zombie = new Entity("Zombie", Glyph.ZOMBIE);
        zombie.addFeature(FeatureType.ATTACKABLE, new Attackable(10, 10, 1, 0));
        zombie.addFeature(FeatureType.AI, new MonsterAi(zombie));
        zombie.addFeature(FeatureType.FOV, new FieldOfView(zombie, 8));
        return zombie;
    }

    public static Entity createDoor() {
        var door = new Entity("Drzwi", Glyph.DOOR_CLOSED);
        door.setVisibleInFog(true);
        door.addFeature(FeatureType.OPENABLE, new DoorOpenable(door));
        return door;
    }

    public static Entity createDownstairs() {
        var stairs = new Entity("Schody w dół", Glyph.DOWNSTAIRS);
        stairs.setVisibleInFog(true);
        stairs.addFeature(FeatureType.DOWNSTAIRS, new Downstairs());
        return stairs;
    }

    public static Entity createGlazolud() {
        var glazolud = new Entity("Głazolud", Glyph.GLAZOLUD);
        glazolud.addFeature(FeatureType.ATTACKABLE, new Attackable(20, 20, 2, 1));
        glazolud.addFeature(FeatureType.AI, new GlazoludAi(glazolud));
        glazolud.addFeature(FeatureType.FOV, new FieldOfView(glazolud, 8));
        return glazolud;
    }

    public static Entity createRollingRock(Point direction) {
        var rock = new Entity("Głaz", Glyph.BIG_ROCK);
        rock.addFeature(FeatureType.ATTACKABLE, new RollingRockAttackable(30, 30, 10));
        rock.addFeature(FeatureType.AI, new RollingRockAi(rock, direction));
        return rock;
    }
}
