package pl.lonski.dzibdzikon.entity.features;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.map.TextureId;

public class DoorOpenable implements Openable {

    private final Entity entity;

    public DoorOpenable(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void open(World world) {
        if (!opened()) {
            entity.setGlyph(TextureId.DOOR_OPEN);
        }
    }

    @Override
    public void close(World world) {
        if (opened()) {
            entity.setGlyph(TextureId.DOOR_CLOSED);
        }
    }

    @Override
    public boolean opaque() {
        return entity.getGlyph() == TextureId.DOOR_CLOSED;
    }

    @Override
    public boolean obstacle() {
        return entity.getGlyph() == TextureId.DOOR_CLOSED;
    }

    @Override
    public boolean opened() {
        return entity.getGlyph() == TextureId.DOOR_OPEN;
    }
}
