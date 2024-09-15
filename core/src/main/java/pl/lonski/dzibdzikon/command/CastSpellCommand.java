package pl.lonski.dzibdzikon.command;

import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

import com.badlogic.gdx.Input;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.CastSpellAction;
import pl.lonski.dzibdzikon.action.targeting.TargetConsumer;
import pl.lonski.dzibdzikon.action.targeting.TargeterFactory;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.screen.WindowManager;
import pl.lonski.dzibdzikon.spell.Spell;
import pl.lonski.dzibdzikon.ui.window.SpellBookWindow;

public class CastSpellCommand implements Command {
    @Override
    public boolean accept(DzibdziInput.DzibdziKey key) {
        return (key.keyCode() == Input.Keys.Z && DzibdziInput.isShiftDown);
    }

    @Override
    public void execute(Player player, World world) {
        getGameResources().windowManager.executeInWindow(WindowManager.WindowType.SPELL_BOOK, window -> {
            ((SpellBookWindow) window).takeSelectedSpell().ifPresent(spell -> castSpell(player, spell));
        });
    }

    private void castSpell(Player player, Spell spell) {
        TargetConsumer onTargetSelected = target -> new CastSpellAction(player, target, spell);
        player.takeAction(TargeterFactory.create(player, spell.getTargetingMode(), onTargetSelected));
    }
}
