package pl.lonski.dzibdzikon;

import com.badlogic.gdx.Input;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class PositionUtils {

    public static Point getPositionChange(Point myCoords, DzibdziInput.DzibdziKey key) {
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

        if (dpos.isZero() && key.touchCoords() != null) {
            Point dd = key.touchCoords().sub(myCoords);
            if (!dd.isZero()) {
                dpos = new Point(Math.min(1, Math.max(-1, dd.x())), Math.min(1, Math.max(-1, dd.y())));
            }
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

    public static CirclePoints inFilledCircleWithPerimeterOf(int radius) {
        var points = new HashSet<Point>();
        var perimeterPoints = new HashSet<Point>();

        for (int ox = -radius; ox <= radius; ox++) {
            for (int oy = -radius; oy <= radius; oy++) {
                if (ox * ox + oy * oy < (radius - 1) * (radius - 1)) {
                    points.add(new Point(ox, oy));
                } else if (ox * ox + oy * oy <= radius * radius) {
                    perimeterPoints.add(new Point(ox, oy));
                }
            }
        }

        for (Point p : new HashSet<>(perimeterPoints)) {

            //      *
            //     ###
            //    #   #
            //   ##    ##
            //    #   #
            //     ###
            //      #
            if (perimeterPoints.contains(new Point(p.x(), p.y() + 1))
                    && perimeterPoints.contains(new Point(p.x() - 1, p.y()))
                    && perimeterPoints.contains(new Point(p.x() + 1, p.y()))
                    && !perimeterPoints.contains(new Point(p.x() - 1, p.y() + 1))
                    && !perimeterPoints.contains(new Point(p.x() + 1, p.y() + 1))) {
                perimeterPoints.remove(new Point(p.x(), p.y() + 1));
            }

            //      #
            //     ###
            //    #   #
            //   ##    ##
            //    #   #
            //     ###
            //      *
            if (perimeterPoints.contains(new Point(p.x(), p.y() - 1))
                    && perimeterPoints.contains(new Point(p.x() - 1, p.y()))
                    && perimeterPoints.contains(new Point(p.x() + 1, p.y()))
                    && !perimeterPoints.contains(new Point(p.x() - 1, p.y() - 1))
                    && !perimeterPoints.contains(new Point(p.x() + 1, p.y() - 1))) {
                perimeterPoints.remove(new Point(p.x(), p.y() - 1));
            }

            //      #
            //     ###
            //    #   #
            //   *#    ##
            //    #   #
            //     ###
            //      #
            if (perimeterPoints.contains(new Point(p.x() - 1, p.y()))
                    && perimeterPoints.contains(new Point(p.x(), p.y() + 1))
                    && perimeterPoints.contains(new Point(p.x(), p.y() - 1))
                    && !perimeterPoints.contains(new Point(p.x() - 1, p.y() - 1))
                    && !perimeterPoints.contains(new Point(p.x() - 1, p.y() + 1))) {
                perimeterPoints.remove(new Point(p.x() - 1, p.y()));
            }

            //      #
            //     ###
            //    #   #
            //   ##    #*
            //    #   #
            //     ###
            //      #
            if (perimeterPoints.contains(new Point(p.x() + 1, p.y()))
                    && perimeterPoints.contains(new Point(p.x(), p.y() + 1))
                    && perimeterPoints.contains(new Point(p.x(), p.y() - 1))
                    && !perimeterPoints.contains(new Point(p.x() + 1, p.y() - 1))
                    && !perimeterPoints.contains(new Point(p.x() + 1, p.y() + 1))) {
                perimeterPoints.remove(new Point(p.x() + 1, p.y()));
            }
        }

        return new CirclePoints(points, perimeterPoints);
    }

    public static void inFilledEllipseOf(int a, int b, Consumer<Point> consumer) {
        int x = a;
        int y = 0;
        int dx = 1 - 2 * a;
        int dy = 2 * b;
        int error = dx + dy;

        while (x >= y) {
            // Add points for the current ellipse section
            for (int i = -x; i <= x; i++) {
                consumer.accept(new Point(i, y));
                consumer.accept(new Point(i, -y));
            }

            if (error >= 0) {
                x -= 1;
                dx += 2 * (1 - x);
                error += dx;
            } else {
                y += 1;
                dy -= 2 * (1 - y);
                error += dy;
            }
        }
    }

    public static void inEllipseOf(int a, int b, Consumer<Point> consumer) {
        int x = a;
        int y = 0;
        int dx = 1 - 2 * a;
        int dy = 2 * b;
        int error = dx + dy;

        while (x >= y) {
            // Add points for the current ellipse section
            consumer.accept(new Point(x, y));
            consumer.accept(new Point(-x, y));
            consumer.accept(new Point(x, -y));
            consumer.accept(new Point(-x, -y));

            // Add top and bottom points
            if (y == x) {
                for (int i = -x + 1; i < x; i++) {
                    consumer.accept(new Point(i, y));
                    consumer.accept(new Point(i, -y));
                }
            }

            if (error >= 0) {
                x -= 1;
                dx += 2 * (1 - x);
                error += dx;
            } else {
                y += 1;
                dy -= 2 * (1 - y);
                error += dy;
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

    public record CirclePoints(Set<Point> points, Set<Point> perimeter) {}
}
