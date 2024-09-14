package pl.lonski.dzibdzikon.entity;

import pl.lonski.dzibdzikon.action.Action;
import pl.lonski.dzibdzikon.map.TextureId;

import java.util.Optional;
import java.util.function.Supplier;

public class Quickbar {

    private Slot slot;

    public Optional<Action> useSlot() {
        return Optional.ofNullable(slot).map(s -> s.actionSupplier.get());
    }

    public Optional<TextureId> getSlotIcon() {
        return Optional.ofNullable(slot).map(Slot::icon);
    }

    public void setSlot(TextureId textureId, Supplier<Action> actionSupplier) {
        this.slot = new Slot(actionSupplier, textureId);
    }

    public record Slot(Supplier<Action> actionSupplier, TextureId icon) {
    }
}
