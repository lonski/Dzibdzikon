package pl.lonski.dzibdzikon;

import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import java.util.HashSet;
import java.util.Set;

public class DzibdziInput {

    public static boolean isShiftDown = false;

    public static Set<DzibdziInputListener> listeners = new HashSet<>();

    public static class InputHandler extends InputAdapter {

        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Input.Keys.SHIFT_LEFT) {
                isShiftDown = true;
                return true;
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

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            var pos = getGameResources().camera.unproject(new Vector3((float) screenX, (float) screenY, 0f));
            var coords = new Point((int)pos.x/32, (int)pos.y/32);

            var listenersCopy = new HashSet<>(listeners);
            for (DzibdziInputListener listener : listenersCopy) {
                listener.onInput(new DzibdziKey(-1, false, coords));
            }

            return true;
        }
    }

    public interface DzibdziInputListener {
        void onInput(DzibdziKey key);
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
