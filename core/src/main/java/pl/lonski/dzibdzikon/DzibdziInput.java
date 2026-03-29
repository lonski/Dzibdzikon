package pl.lonski.dzibdzikon;

import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import java.util.HashSet;
import java.util.Set;

public class DzibdziInput {

    public static boolean isShiftDown = false;

    public static Set<DzibdziInputListener> listeners = new HashSet<>();

    public static void broadcast(DzibdziKey key) {
        var listenersCopy = new HashSet<>(listeners);
        for (DzibdziInputListener listener : listenersCopy) {
            listener.onInput(key);
        }
    }

    public static class GestureHandler extends GestureDetector.GestureAdapter {

        boolean zooming = false;

        @Override
        public boolean zoom(float initialDistance, float distance) {
            zooming = true;
            float zoomChange = (initialDistance - distance) * 0.00005f;
            // Min zoom: show ~8 tiles across screen (very zoomed in)
            // Max zoom: show ~50 tiles across screen (very zoomed out)
            float minZoom = (8 * 32f) / getGameResources().camera.viewportWidth;
            float maxZoom = (50 * 32f) / getGameResources().camera.viewportWidth;
            getGameResources().camera.zoom =
                    Math.max(minZoom, Math.min(maxZoom, getGameResources().camera.zoom + zoomChange));
            getGameResources().camera.update();
            return true;
        }

        @Override
        public boolean panStop(float x, float y, int pointer, int button) {
            zooming = false;
            return super.panStop(x, y, pointer, button);
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {
            if (zooming) {
                return false;
            }
            var screenCoords = new Point(Math.round(x), Math.round(y));
            var pos = getGameResources().camera.unproject(new Vector3(x, y, 0f));
            var coords = new Point(Math.round(pos.x / 32.f), Math.round(pos.y / 32.f));

            broadcast(new DzibdziKey(-1, false, coords, screenCoords, false));
            return true;
        }

        @Override
        public boolean longPress(float x, float y) {
            var screenCoords = new Point(Math.round(x), Math.round(y));
            broadcast(new DzibdziKey(Input.Keys.UNKNOWN, false, null, screenCoords, true));
            return true;
        }
    }

    public static class InputHandler extends InputAdapter {

        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Input.Keys.SHIFT_LEFT) {
                isShiftDown = true;
                return true;
            }

            if (keycode == Input.Keys.NUM_9) {
                getGameResources().camera.zoom += 0.1f;
            } else if (keycode == Input.Keys.NUM_0) {
                getGameResources().camera.zoom -= 0.1f;
            }

            broadcast(new DzibdziKey(keycode, false, null, null, false));

            return true;
        }

        @Override
        public boolean keyUp(int keycode) {
            if (keycode == Input.Keys.SHIFT_LEFT) {
                isShiftDown = false;
                return true;
            }

            broadcast(new DzibdziKey(keycode, true, null, null, false));

            return true;
        }
    }

    public interface DzibdziInputListener {
        boolean onInput(DzibdziKey key);
    }

    public record DzibdziKey(int keyCode, boolean released, Point touchCoords, Point screenTouchCoords, boolean longPress) {

        public boolean isUpKey() {
            return keyCode == Input.Keys.UP || keyCode == Input.Keys.NUMPAD_8 || keyCode == Input.Keys.K;
        }

        public boolean isDownKey() {
            return keyCode == Input.Keys.DOWN || keyCode == Input.Keys.NUMPAD_2 || keyCode == Input.Keys.J;
        }

        public boolean isEnterKey() {
            return keyCode == Input.Keys.ENTER || keyCode == Input.Keys.NUMPAD_ENTER;
        }
    }
}
