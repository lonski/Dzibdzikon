package pl.lonski.dzibdzikon.command;

import com.badlogic.gdx.Input;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.screen.WindowManager;
import pl.lonski.dzibdzikon.ui.window.SpellBookWindow;

public class CastSpellCommand implements Command {
    @Override
    public boolean accepts(DzibdziInput.DzibdziKey key) {
        return (key.keyCode() == Input.Keys.Z && DzibdziInput.isShiftDown);
    }

    @Override
    public void execute(Player player, World world) {
        player.getWindowManager().executeInWindow(WindowManager.WindowType.SPELL_BOOK, window -> {
            ((SpellBookWindow) window).takeSelectedSpell().ifPresent(spell -> {
                System.out.println("Casting spell: " + spell.getName());
            });
        });
    }
}
