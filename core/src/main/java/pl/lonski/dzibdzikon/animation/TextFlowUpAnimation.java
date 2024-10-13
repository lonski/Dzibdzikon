package pl.lonski.dzibdzikon.animation;

import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

import com.badlogic.gdx.graphics.Color;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;

public class TextFlowUpAnimation extends BaseAnimation {

    private float speed = 0.01f;
    private float time = 0;
    private final Point targetPos;
    private final String text;
    private final Color color;
    private Point pos;
    private Point startCoord;
    private int updatesCounter = 0;

    public TextFlowUpAnimation(String text, Point startingCoords, Color color) {
        this.text = text;
        this.startCoord = startingCoords;
        this.pos = startingCoords.toPixels();
        this.targetPos = pos.add(new Point(0, 1).toPixels());
        this.color = color;
    }

    @Override
    public void update(float delta, World world) {
        if (!world.visible(startCoord) || updatesCounter > 40) {
            finish();
            return;
        }

        time += delta;
        if (time >= speed && !isDone()) {
            time = 0;
            updatesCounter += 1;
            var dx = targetPos.x() - pos.x();
            var dy = targetPos.y() - pos.y();
            var xi = dx > 0 ? 1 : -1;
            var yi = dy > 0 ? 1 : -1;
            pos = pos.add(new Point(xi, yi));
        }
    }

    @Override
    public void render() {
        getGameResources().fontItalicBold15.setColor(color);
        getGameResources().fontItalicBold15.draw(getGameResources().batch, text, pos.x(), pos.y());
    }

    @Override
    public boolean isDone() {
        return pos.equals(targetPos);
    }

    @Override
    public void finish() {
        pos = targetPos;
    }
}
