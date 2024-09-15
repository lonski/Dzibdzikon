package pl.lonski.dzibdzikon.entity.features;

import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;

import java.util.ArrayList;
import java.util.List;

public class Inventory implements EntityFeature {

    private final List<Entity> items = new ArrayList<>();

    public boolean addItem(Entity item) {
        if (item.getFeature(FeatureType.PICKABLE) != null) {
            items.add(item);
            return true;
        }
        return false;
    }

    public List<Entity> getItems() {
        return items;
    }
}
