package pl.lonski.dzibdzikon.effect;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.map.TextureId;

/**
 * Tests for effect lifecycle, stackability and blocking behaviour on Entity.
 *
 * <p>All effects here are test doubles — they do not touch LibGDX.
 */
public class EntityEffectTest {

    private static Entity makeEntity() {
        return new Entity("test", TextureId.FLOOR);
    }

    // ----- Test-double helpers -----

    /** Simple persistent stackable effect that counts apply() and takeTurn() calls. */
    private static class TrackingEffect implements Effect {
        final AtomicInteger applied = new AtomicInteger();
        final AtomicInteger ticks = new AtomicInteger();
        final AtomicInteger removed = new AtomicInteger();
        private int remainingTurns;
        private final boolean stackable;
        private final boolean blocking;

        TrackingEffect(int turns, boolean stackable, boolean blocking) {
            this.remainingTurns = turns;
            this.stackable = stackable;
            this.blocking = blocking;
        }

        TrackingEffect(int turns) {
            this(turns, true, false);
        }

        @Override
        public boolean stackable() {
            return stackable;
        }

        @Override
        public void apply(Entity target) {
            applied.incrementAndGet();
        }

        @Override
        public boolean isActive() {
            return remainingTurns > 0;
        }

        @Override
        public void takeTurn(pl.lonski.dzibdzikon.World world, Entity target) {
            ticks.incrementAndGet();
            remainingTurns--;
        }

        @Override
        public void remove(Entity target) {
            removed.incrementAndGet();
        }

        @Override
        public boolean blockEntityActingPossibility() {
            return blocking;
        }
    }

    /** Instant effect: isActive() = false immediately after apply. */
    private static class InstantEffect implements Effect {
        final AtomicInteger applied = new AtomicInteger();
        final AtomicInteger removed = new AtomicInteger();

        @Override
        public void apply(Entity target) {
            applied.incrementAndGet();
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public void remove(Entity target) {
            removed.incrementAndGet();
        }
    }

    // ----- Stackable effects -----

    @Test
    void stackableEffects_bothPersistAfterApplyingTwice() {
        var entity = makeEntity();
        var e1 = new TrackingEffect(2);
        var e2 = new TrackingEffect(2);

        entity.applyEffect(e1);
        entity.applyEffect(e2);

        // Both should be ticked on next turn
        entity.onTurnStarted(null);

        assertEquals(1, e1.ticks.get(), "First stackable effect should have been ticked");
        assertEquals(1, e2.ticks.get(), "Second stackable effect should have been ticked");
    }

    // ----- Non-stackable effects -----

    @Test
    void nonStackableEffect_replacesExistingEffectOfSameClass() {
        var entity = makeEntity();
        var first = new TrackingEffect(5, false, false);
        var second = new TrackingEffect(5, false, false);

        entity.applyEffect(first);
        entity.applyEffect(second); // should evict first

        // Only one effect should tick
        entity.onTurnStarted(null);
        assertEquals(0, first.ticks.get(), "First non-stackable effect should have been evicted");
        assertEquals(1, second.ticks.get(), "Second non-stackable effect should be active");
    }

    @Test
    void nonStackableEffect_callsRemoveOnEvictedEffect() {
        var entity = makeEntity();
        var first = new TrackingEffect(5, false, false);
        var second = new TrackingEffect(5, false, false);

        entity.applyEffect(first);
        entity.applyEffect(second);

        assertEquals(1, first.removed.get(), "remove() must be called on the evicted non-stackable effect");
    }

    // ----- Instant effects -----

    @Test
    void instantEffect_doesNotPersistInActiveEffects() {
        var entity = makeEntity();
        var instant = new InstantEffect();

        entity.applyEffect(instant);
        assertEquals(1, instant.applied.get(), "Instant effect should be applied");

        // Should not be ticked since it's not in active effects
        entity.onTurnStarted(null);
        // If it had been ticked, applied would not count but we check no exception occurs
        // and remove() was called
        assertEquals(1, instant.removed.get(), "Instant effect should have remove() called");
    }

    // ----- onTurnStarted lifecycle -----

    @Test
    void onTurnStarted_ticksAllActiveEffects() {
        var entity = makeEntity();
        var e1 = new TrackingEffect(3);
        var e2 = new TrackingEffect(3);

        entity.applyEffect(e1);
        entity.applyEffect(e2);
        entity.onTurnStarted(null);

        assertEquals(1, e1.ticks.get());
        assertEquals(1, e2.ticks.get());
    }

    @Test
    void onTurnStarted_removesExpiredEffects() {
        var entity = makeEntity();
        var shortLived = new TrackingEffect(1); // expires after 1 tick
        var longLived = new TrackingEffect(3);

        entity.applyEffect(shortLived);
        entity.applyEffect(longLived);

        entity.onTurnStarted(null); // shortLived becomes inactive
        entity.onTurnStarted(null); // should only tick longLived

        assertEquals(1, shortLived.ticks.get(), "Short-lived effect should only tick once");
        assertEquals(2, longLived.ticks.get(), "Long-lived effect should tick twice");
        assertEquals(1, shortLived.removed.get(), "Expired effect should have remove() called");
    }

    // ----- Action-blocking effects -----

    @Test
    void blockingEffect_preventsEntityFromActing() {
        var entity = makeEntity();
        var blocker = new TrackingEffect(2, true, true);

        entity.applyEffect(blocker);

        assertTrue(entity.anyActionBlockingEffectsActive(), "Entity with blocking effect should be blocked");
    }

    @Test
    void nonBlockingEffect_doesNotPreventActing() {
        var entity = makeEntity();
        var effect = new TrackingEffect(2, true, false);

        entity.applyEffect(effect);

        assertFalse(entity.anyActionBlockingEffectsActive(), "Entity with non-blocking effect should not be blocked");
    }

    @Test
    void noEffects_doesNotBlock() {
        var entity = makeEntity();
        assertFalse(entity.anyActionBlockingEffectsActive());
    }
}
