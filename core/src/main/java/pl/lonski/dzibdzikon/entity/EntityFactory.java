package pl.lonski.dzibdzikon.entity;

import pl.lonski.dzibdzikon.entity.features.FieldOfView;
import pl.lonski.dzibdzikon.entity.features.MonsterAi;
import pl.lonski.dzibdzikon.map.Glyph;

public class EntityFactory {

    public static Entity createZombie() {
        var zombie = new Entity("Zombie", Glyph.ZOMBIE, 10);
//        zombie.addFeature(FeatureType.ATTACKABLE, new Attackable(10, 10, 1, 0));
        zombie.addFeature(FeatureType.AI, new MonsterAi(zombie));
        zombie.addFeature(FeatureType.FOV, new FieldOfView(zombie, 8));
        return zombie;
    }

//    public static Entity createDoor() {
//        var door = new Entity("Door", Tile.DOOR_CLOSED);
//        door.setVisibleInFog(true);
//        door.addFeature(FeatureType.OPENABLE, new DoorOpenable(door));
//        return door;
//    }
//
//    public static Entity createDownstairs() {
//        var stairs = new Entity("Downstairs", Tile.DOWNSTAIRS);
//        stairs.setVisibleInFog(true);
//        stairs.addFeature(FeatureType.DOWNSTAIRS, new Downstairs());
//        return stairs;
//    }
}
