package pl.lonski.dzibdzikon.effect;

import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;

public interface TileEffect {

    void render(Point coords);

    /**
     * @return true if effect faded and should be removed
     */
    boolean takeTurn(Point pos, World world);
}
