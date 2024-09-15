package pl.lonski.dzibdzikon;

import com.badlogic.gdx.Input;

import java.util.function.Consumer;

public class PositionUtils {

    public static Point getPositionChange(DzibdziInput.DzibdziKey key) {
        Point dpos = new Point(0, 0);

        if (key == null) {
            return dpos;
        }

        if (key.keyCode() == Input.Keys.NUMPAD_4 || key.keyCode() == Input.Keys.LEFT || key.keyCode() == Input.Keys.H) {
            dpos = new Point(-1, 0);
        } else if (key.keyCode() == Input.Keys.NUMPAD_6
                || key.keyCode() == Input.Keys.RIGHT
                || key.keyCode() == Input.Keys.L) {
            dpos = new Point(1, 0);
        } else if (key.keyCode() == Input.Keys.NUMPAD_8
                || key.keyCode() == Input.Keys.UP
                || key.keyCode() == Input.Keys.K) {
            dpos = new Point(0, 1);
        } else if (key.keyCode() == Input.Keys.NUMPAD_2
                || key.keyCode() == Input.Keys.DOWN
                || key.keyCode() == Input.Keys.J) {
            dpos = new Point(0, -1);
        } else if (key.keyCode() == Input.Keys.NUMPAD_7 || key.keyCode() == Input.Keys.Y) {
            dpos = new Point(-1, 1);
        } else if (key.keyCode() == Input.Keys.NUMPAD_9 || key.keyCode() == Input.Keys.U) {
            dpos = new Point(1, 1);
        } else if (key.keyCode() == Input.Keys.NUMPAD_1 || key.keyCode() == Input.Keys.B) {
            dpos = new Point(-1, -1);
        } else if (key.keyCode() == Input.Keys.NUMPAD_3 || key.keyCode() == Input.Keys.N) {
            dpos = new Point(1, -1);
        }

        return dpos;
    }

    public static void inFilledCircleOf(int radius, Consumer<Point> consumer) {
        for (int ox = -radius; ox <= radius; ox++) {
            for (int oy = -radius; oy <= radius; oy++) {
                if (ox * ox + oy * oy <= radius * radius) {
                    consumer.accept(new Point(ox, oy));
                }
            }
        }
    }

    public static void inCircleOf(int radius, Consumer<Point> consumer) {

        int x = radius;
        int y = 0;
        int error = 1 - radius;

        while (x >= y) {
            consumer.accept(new Point(x, y));
            consumer.accept(new Point(-x, y));
            consumer.accept(new Point(x, -y));
            consumer.accept(new Point(-x, -y));
            consumer.accept(new Point(y, x));
            consumer.accept(new Point(-y, x));
            consumer.accept(new Point(y, -x));
            consumer.accept(new Point(-y, -x));

            if (error <= 0) {
                y++;
                error += 2 * y + 1;
            }

            if (error > 0) {
                x--;
                error -= 2 * x + 1;
            }
        }
    }
}
