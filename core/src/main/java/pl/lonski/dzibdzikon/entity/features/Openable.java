package pl.lonski.dzibdzikon.entity.features;


import pl.lonski.dzibdzikon.World;

public interface Openable extends EntityFeature {

    void open(World world);

    void close(World world);

    boolean opaque();

    boolean obstacle();

    boolean opened();
}
