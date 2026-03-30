package pl.lonski.dzibdzikon.command;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.Input;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.lonski.dzibdzikon.DzibdziInput.DzibdziKey;
import pl.lonski.dzibdzikon.Point;

class CloseCommandTest {

    private CloseCommand cmd;

    @BeforeEach
    void setUp() {
        cmd = new CloseCommand();
    }

    // --- keyboard path ---

    @Test
    void accept_keyC_returnsTrue() {
        var key = new DzibdziKey(Input.Keys.C, false, null, null, false);
        assertTrue(cmd.accept(key));
    }

    @Test
    void accept_otherKey_returnsFalse() {
        var key = new DzibdziKey(Input.Keys.X, false, null, null, false);
        assertFalse(cmd.accept(key));
    }

    // --- long-press path ---

    @Test
    void accept_longPressWithCoords_returnsTrue() {
        var coords = new Point(3, 4);
        var key = new DzibdziKey(Input.Keys.UNKNOWN, false, coords, null, true);
        assertTrue(cmd.accept(key));
    }

    @Test
    void accept_longPressWithoutCoords_returnsFalse() {
        var key = new DzibdziKey(Input.Keys.UNKNOWN, false, null, null, true);
        assertFalse(cmd.accept(key));
    }

    @Test
    void accept_longPressWithCoords_storesTouchTarget() {
        var coords = new Point(5, 7);
        var key = new DzibdziKey(Input.Keys.UNKNOWN, false, coords, null, true);
        cmd.accept(key);
        assertEquals(coords, cmd.touchTarget);
    }

    @Test
    void accept_keyCAfterLongPress_clearsTouchTarget() {
        var coords = new Point(5, 7);
        cmd.accept(new DzibdziKey(Input.Keys.UNKNOWN, false, coords, null, true));
        cmd.accept(new DzibdziKey(Input.Keys.C, false, null, null, false));
        assertNull(cmd.touchTarget);
    }
}
