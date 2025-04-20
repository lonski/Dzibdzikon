package pl.lonski.dzibdzikon.command;

import com.badlogic.gdx.Input;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.Quickbar;

public class UseQuickbarCommand implements Command {

    private Quickbar.SlotType slotType;
    private boolean clearSlotCommand = false;

    @Override
    public boolean accept(DzibdziInput.DzibdziKey key) {
        slotType = switch (key.keyCode()) {
            case Input.Keys.NUM_1 -> Quickbar.SlotType.NUM_1;
            case Input.Keys.NUM_2 -> Quickbar.SlotType.NUM_2;
            case Input.Keys.NUM_3 -> Quickbar.SlotType.NUM_3;
            case Input.Keys.NUM_4 -> Quickbar.SlotType.NUM_4;
            case Input.Keys.NUM_5 -> Quickbar.SlotType.NUM_5;
            default -> null;};

        clearSlotCommand = DzibdziInput.isShiftDown;

        return slotType != null;
    }

    @Override
    public void execute(Player player, World world) {
        if (!clearSlotCommand) {
            player.getQuickbar().useSlot(slotType).ifPresent(player::takeAction);
        } else {
            player.getQuickbar().clearSlot(slotType);
        }
    }
}
