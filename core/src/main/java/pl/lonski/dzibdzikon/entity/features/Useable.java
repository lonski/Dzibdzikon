package pl.lonski.dzibdzikon.entity.features;

import pl.lonski.dzibdzikon.entity.Entity;

public abstract class Useable implements EntityFeature {

    public abstract void use(Entity user, Entity target);
}
