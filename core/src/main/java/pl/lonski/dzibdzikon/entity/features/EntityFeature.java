package pl.lonski.dzibdzikon.entity.features;


import pl.lonski.dzibdzikon.World;

public interface EntityFeature {
    default void update(float delta, World world) {
    }
}
