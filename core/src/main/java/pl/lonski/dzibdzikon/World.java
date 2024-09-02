package pl.lonski.dzibdzikon;

import pl.lonski.dzibdzikon.map.RoomMapGeneratorV2;
import pl.lonski.dzibdzikon.map.TileGrid;

public class World {

    private final TileGrid map;

    public World() {
        map = RoomMapGeneratorV2.generate(160, 48);
    }

    public TileGrid getMap() {
        return map;
    }
}
