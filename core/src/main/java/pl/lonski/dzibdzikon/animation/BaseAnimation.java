package pl.lonski.dzibdzikon.animation;

public abstract class BaseAnimation implements Animation {

    private Object owner;

    public BaseAnimation(Object owner) {
        this.owner = owner;
    }

    public BaseAnimation() {
        this(null);
    }

    @Override
    public Object getOwner() {
        return owner;
    }

    public void setOwner(Object owner) {
        this.owner = owner;
    }
}
