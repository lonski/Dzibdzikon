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

    public static class GestureHandler extends GestureDetector.GestureAdapter {

        boolean zooming = false;

        @Override
        public boolean zoom(float initialDistance, float distance) {
            zooming = true;
            float zoomChange = (initialDistance - distance) * 0.00005f;
            getGameResources().camera.zoom =
                    Math.max(0.3f, Math.min(2.5f, getGameResources().camera.zoom + zoomChange));
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

                var pos = getGameResources().camera.unproject(new Vector3(x, y, 0f));
                var coords = new Point(Math.round(pos.x / 32.f), Math.round(pos.y / 32.f));

                var listenersCopy = new HashSet<>(listeners);
                for (DzibdziInputListener listener : listenersCopy) {
                    listener.onInput(new DzibdziKey(-1, false, coords));
                }

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

            var listenersCopy = new HashSet<>(listeners);
            for (DzibdziInputListener listener : listenersCopy) {
                listener.onInput(new DzibdziKey(keycode, false, null));
            }

            return true;
        }

        @Override
        public boolean keyUp(int keycode) {
            if (keycode == Input.Keys.SHIFT_LEFT) {
                isShiftDown = false;
                return true;
            }

            var listenersCopy = new HashSet<>(listeners);
            for (DzibdziInputListener listener : listenersCopy) {
                listener.onInput(new DzibdziKey(keycode, true, null));
            }

            return true;
        }
    }

    public interface DzibdziInputListener {
        boolean onInput(DzibdziKey key);
    }

    public record DzibdziKey(int keyCode, boolean released, Point touchCoords) {

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
