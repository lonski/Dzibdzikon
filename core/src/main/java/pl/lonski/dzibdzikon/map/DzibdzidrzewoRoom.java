package pl.lonski.dzibdzikon.map;

import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;

public class DzibdzidrzewoRoom extends CircleRoom {

    public DzibdzidrzewoRoom(int x, int y, int w, int h) {
        super(x, y, Math.max(w, 5), Math.max(h, 5));
    }

    @Override
    public RoomType getRoomType() {
        return RoomType.DZIBDZIDRZEWO;
    }

    @Override
    public void afterMapGenerated(TileGrid map) {

        for (Point point : perimeterPoints) {
            if (map.getTile(point).isWall()) {
                map.setTile(point, TextureId.WALL_GREEN);
            } else if (map.getTile(point).isFloor()) {
                map.setTile(point, TextureId.FLOOR_GREEN);
            }
        }

        for (Point point : points) {
            map.setTile(point, TextureId.FLOOR_GREEN);
        }
    }

    public boolean acceptsEntity(Entity entity) {
        if (entity.getFeature(FeatureType.ATTACKABLE) != null) {
            return entity.getGlyph() == TextureId.MOB_PTAKODRZEWO;
        }

        return true;
    }
}
