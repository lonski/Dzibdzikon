package pl.lonski.dzibdzikon.effect;

import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;

public interface Effect {

    default boolean stackable() {
        return true;
    }

    default String getId() {
        return getClass().getSimpleName();
    }

    void apply(Entity target);

    boolean isActive();

    default void takeTurn(World world, Entity target) {}

    default void remove(Entity target) {}

    default boolean blockEntityActingPossibility() {
        return false;
    }
}
