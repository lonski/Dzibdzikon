package pl.lonski.dzibdzikon.entity;

import pl.lonski.dzibdzikon.action.Action;
import pl.lonski.dzibdzikon.map.TextureId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class Quickbar {

    private final Map<SlotType, Slot> slots = new HashMap<>();

    public Optional<Action> useSlot(SlotType slotType) {
        return Optional.ofNullable(slots.get(slotType)).map(s -> s.actionSupplier.get());
    }

    public List<SlotIcon> getSlotIcons() {
        return slots.entrySet().stream()
                .map(e -> new SlotIcon(
                        e.getValue().icon(), List.of(SlotType.values()).indexOf(e.getKey()) + 1))
                .toList();
    }

    public void setSlot(SlotType slotType, TextureId textureId, Supplier<Action> actionSupplier) {
        this.slots.put(slotType, new Slot(actionSupplier, textureId));
    }

    public void clearSlot(SlotType slotType) {
        this.slots.remove(slotType);
    }

    public record SlotIcon(TextureId icon, int num) {}

    public record Slot(Supplier<Action> actionSupplier, TextureId icon) {}

    public enum SlotType {
        NUM_1,
        NUM_2,
        NUM_3,
        NUM_4,
        NUM_5
    }
}
