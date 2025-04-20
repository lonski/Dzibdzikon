package pl.lonski.dzibdzikon;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class CameraUtils {

    public static Vector2 getBottomLeftCorner(OrthographicCamera camera) {
        float halfWidth = camera.viewportWidth * 0.5f;
        float halfHeight = camera.viewportHeight * 0.5f;
        return new Vector2(camera.position.x - halfWidth, camera.position.y - halfHeight);
    }

    public static Vector2 getBottomRightCorner(OrthographicCamera camera) {
        float halfWidth = camera.viewportWidth * 0.5f;
        float halfHeight = camera.viewportHeight * 0.5f;
        return new Vector2(camera.position.x + halfWidth, camera.position.y - halfHeight);
    }

    public static Vector2 getTopLeftCorner(OrthographicCamera camera) {
        float halfWidth = camera.viewportWidth * 0.5f;
        float halfHeight = camera.viewportHeight * 0.5f;
        return new Vector2(camera.position.x - halfWidth, camera.position.y + halfHeight);
    }

    public static Vector2 getTopRightCorner(OrthographicCamera camera) {
        float halfWidth = camera.viewportWidth * 0.5f;
        float halfHeight = camera.viewportHeight * 0.5f;
        return new Vector2(camera.position.x + halfWidth, camera.position.y + halfHeight);
    }

    public static Vector2 getBottomCenter(OrthographicCamera camera) {
        float halfWidth = camera.viewportWidth * 0.5f;
        float halfHeight = camera.viewportHeight * 0.5f;
        return new Vector2(camera.position.x, camera.position.y - halfHeight);
    }
}
