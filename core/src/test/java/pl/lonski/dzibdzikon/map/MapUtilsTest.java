package pl.lonski.dzibdzikon.map;

import org.junit.jupiter.api.Test;
import pl.lonski.dzibdzikon.Point;

class MapUtilsTest {

    @Test
    public void testEuclideanDistance() {
        System.out.println(MapUtils.euclideanDistance(new Point(0, 0), new Point(1, 1)));
        System.out.println(MapUtils.euclideanDistance(new Point(0, 0), new Point(0, 1)));
        System.out.println(MapUtils.euclideanDistance(new Point(0, 0), new Point(0, 2)));
        System.out.println(MapUtils.euclideanDistance(new Point(0, 0), new Point(1, 2)));
    }
}
