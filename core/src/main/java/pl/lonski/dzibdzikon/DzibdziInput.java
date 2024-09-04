package pl.lonski.dzibdzikon;

import com.badlogic.gdx.InputAdapter;
import java.util.HashSet;
import java.util.Set;

public class DzibdziInput {

    public static Set<DzibdziInputListener> listeners = new HashSet<>();

    public static class InputHandler extends InputAdapter {

        @Override
        public boolean keyDown(int keycode) {
            listeners.forEach(l -> l.onInput(new DzibdziKey(keycode, false)));
            return true;
        }

        @Override
        public boolean keyUp(int keycode) {
            listeners.forEach(l -> l.onInput(new DzibdziKey(keycode, true)));
            return true;
        }
    }

    public interface DzibdziInputListener {
        void onInput(DzibdziKey key);
    }

    public record DzibdziKey(int keyCode, boolean released) {
    }
}
