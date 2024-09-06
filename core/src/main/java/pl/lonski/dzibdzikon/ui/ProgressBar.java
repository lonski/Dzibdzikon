package pl.lonski.dzibdzikon.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class ProgressBar {

    private final float width, height;
    private final Color backgroundColor, foregroundColor;
    private float progress;

    public ProgressBar(float width, float height, Color backgroundColor, Color foregroundColor) {
        this.width = width;
        this.height = height;
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        this.progress = 1.0f; // Default to full progress
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setProgress(float progress) {
        this.progress = Math.max(0, Math.min(1, progress)); // Clamp between 0 and 1
    }

    public void render(Vector2 position, ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(backgroundColor);
        shapeRenderer.rect(position.x, position.y, width, height);
        shapeRenderer.setColor(foregroundColor);
        shapeRenderer.rect(position.x, position.y, width * progress, height);
    }
}
