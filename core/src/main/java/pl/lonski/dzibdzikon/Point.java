package pl.lonski.dzibdzikon;

public record Point(int x, int y) {

    public boolean isZero() {
        return x == 0 && y == 0;
    }
}
