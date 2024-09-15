package pl.lonski.dzibdzikon.entity;

import pl.lonski.dzibdzikon.action.Action;
import pl.lonski.dzibdzikon.action.ChainAction;
import pl.lonski.dzibdzikon.action.CustomAction;
import pl.lonski.dzibdzikon.map.TextureId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class Quickbar {

    private final Map<SlotType, Slot> slots = new HashMap<>();

    public Optional<Action> useSlot(SlotType slotType) {
        return Optional.ofNullable(slots.get(slotType)).map(s -> {
            s.icon().highlight = true;
            return new ChainAction(List.of(s.actionSupplier.get(), new CustomAction(() -> s.icon().highlight = false)));
        });
    }

    public List<SlotIcon> getSlotIcons() {
        return slots.values().stream().map(Slot::icon).toList();
    }

    public void setSlot(SlotType slotType, TextureId textureId, Supplier<Action> actionSupplier) {
        this.slots.put(
                slotType,
                new Slot(
                        actionSupplier,
                        new SlotIcon(textureId, List.of(SlotType.values()).indexOf(slotType) + 1, false)));
    }

    public void clearSlot(SlotType slotType) {
        this.slots.remove(slotType);
    }

    public record Slot(Supplier<Action> actionSupplier, SlotIcon icon) {}

    public static class SlotIcon {
        TextureId icon;
        int num;
        boolean highlight;

        public SlotIcon(TextureId icon, int num, boolean highlight) {
            this.icon = icon;
            this.num = num;
            this.highlight = highlight;
        }

        public TextureId getIcon() {
            return icon;
        }

        public int getNum() {
            return num;
        }

        public boolean isHighlight() {
            return highlight;
        }
    }

    public enum SlotType {
        NUM_1,
        NUM_2,
        NUM_3,
        NUM_4,
        NUM_5
    }
}
