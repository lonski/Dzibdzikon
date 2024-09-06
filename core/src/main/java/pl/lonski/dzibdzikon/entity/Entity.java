package pl.lonski.dzibdzikon.entity;

import java.util.HashMap;
import java.util.Map;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.Action;
import pl.lonski.dzibdzikon.action.NoOpAction;
import pl.lonski.dzibdzikon.entity.features.Attackable;
import pl.lonski.dzibdzikon.entity.features.EntityFeature;
import pl.lonski.dzibdzikon.map.Glyph;

public class Entity {

    private final Map<FeatureType, EntityFeature> features = new HashMap<>();
    private String name;
    private Glyph glyph;
    private int zLevel;
    private boolean visibleInFog = false;
    private Action currentAction;

    public Entity(String name, Glyph glyph, int zLevel) {
        this.name = name;
        this.glyph = glyph;
        this.zLevel = zLevel;
    }

    public void setGlyph(Glyph glyph) {
        this.glyph = glyph;
    }

    public Action getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(Action currentAction) {
        this.currentAction = currentAction;
    }

    public String getName() {
        return name;
    }

    public Glyph getGlyph() {
        return glyph;
    }

    public int getZLevel() {
        return zLevel;
    }

    public boolean isVisibleInFog() {
        return visibleInFog;
    }

    public void setVisibleInFog(boolean visibleInFog) {
        this.visibleInFog = visibleInFog;
    }

    public <T> T getFeature(FeatureType type) {
        if (features.containsKey(type)) {
            return (T) features.get(type);
        }
        return null;
    }

    public void addFeature(FeatureType type, EntityFeature feature) {
        this.features.put(type, feature);
    }

    public void update(float delta, World world) {
        features.values().forEach(f -> f.update(delta, world));
        if (getFeature(FeatureType.PLAYER) == null && currentAction == null) {
            setCurrentAction(new NoOpAction());
        }
    }

    public boolean alive() {
        Attackable attackable = getFeature(FeatureType.ATTACKABLE);
        return attackable != null && attackable.getHp() > 0;
    }
}
