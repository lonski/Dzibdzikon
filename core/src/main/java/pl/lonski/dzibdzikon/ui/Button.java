package pl.lonski.dzibdzikon.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import pl.lonski.dzibdzikon.Point;

public class Button {

    public Point pos;
    public int width = 32;
    public int height = 32;
    public final Runnable onClick;

    public Button(Runnable onClick) {
        this.onClick = onClick;
    }

    public void render(Vector2 position, ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(position.x, position.y, width, height);
        pos = new Point((int) position.x, (int) position.y);
    }

    public void click() {
        if (onClick != null) {
            onClick.run();
        }
    }
}
