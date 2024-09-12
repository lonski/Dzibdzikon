package pl.lonski.dzibdzikon;

public class Debouncer {

    private final float delay;
    private float counter = 0;

    public Debouncer(float delay) {
        this.delay = delay;
    }

    public boolean debounce(float delta) {
        counter += delta;
        if (counter >= delay) {
            counter = 0;
            return true;
        }
        return false;
    }
}
