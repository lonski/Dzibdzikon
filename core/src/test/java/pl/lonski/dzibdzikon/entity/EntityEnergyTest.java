package pl.lonski.dzibdzikon.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pl.lonski.dzibdzikon.action.Action;
import pl.lonski.dzibdzikon.map.TextureId;

/**
 * Tests for the energy-based turn system on Entity.
 *
 * <p>Intent: verify that energy recharges, gates action availability, and that
 * action bookkeeping (take / cancel) is correct.
 */
public class EntityEnergyTest {

    private static Entity makeEntity() {
        return new Entity("test", TextureId.FLOOR);
    }

    /** Minimal stub action — always done, always succeeded. */
    private static Action stubAction() {
        return new Action() {
            @Override
            public void update(float delta, pl.lonski.dzibdzikon.World world) {}

            @Override
            public boolean isDone() {
                return true;
            }
        };
    }

    // --- Initial state ---

    @Test
    void initialEnergy_isZero() {
        assertEquals(0.0, makeEntity().getEnergy(), 1e-9, "New entity should start with 0 energy");
    }

    @Test
    void initialEnergyIsInsufficient() {
        assertFalse(makeEntity().hasEnergyForAction(), "New entity should not have energy for an action");
    }

    // --- Recharge ---

    @Test
    void rechargeEnergy_addsSpeedToEnergy() {
        var e = makeEntity(); // default speed = 1.0
        e.rechargeEnergy();
        assertEquals(1.0, e.getEnergy(), 1e-9);
    }

    @Test
    void rechargeEnergy_twiceReachesTwo() {
        var e = makeEntity();
        e.rechargeEnergy();
        e.rechargeEnergy();
        assertEquals(2.0, e.getEnergy(), 1e-9);
    }

    @Test
    void slowEntity_needsTwoRecharges_toReachThreshold() {
        var e = makeEntity();
        e.setSpeed(0.5);

        e.rechargeEnergy();
        assertFalse(e.hasEnergyForAction(), "After one recharge at 0.5 speed, energy=0.5 — not enough");

        e.rechargeEnergy();
        assertTrue(e.hasEnergyForAction(), "After two recharges at 0.5 speed, energy=1.0 — enough");
    }

    @Test
    void fastEntity_hasEnergyAfterOneRecharge() {
        var e = makeEntity();
        e.setSpeed(2.0);
        e.rechargeEnergy();
        assertTrue(e.hasEnergyForAction(), "Entity with speed=2.0 should have energy after one recharge");
    }

    // --- hasEnergyForAction threshold ---

    @Test
    void hasEnergy_trueAtExactlyOne() {
        var e = makeEntity(); // speed = 1.0
        e.rechargeEnergy(); // energy = 1.0
        assertTrue(e.hasEnergyForAction());
    }

    @Test
    void hasEnergy_falseJustBelowOne() {
        var e = makeEntity();
        e.setSpeed(0.99);
        e.rechargeEnergy(); // energy = 0.99
        assertFalse(e.hasEnergyForAction());
    }

    // --- takeAction ---

    @Test
    void takeAction_deductsOneEnergyUnit() {
        var e = makeEntity();
        e.rechargeEnergy(); // energy = 1.0
        e.takeAction(stubAction());
        // energy = 1.0 - 1.0 = 0.0
        assertEquals(0.0, e.getEnergy(), 1e-9);
    }

    @Test
    void takeAction_setsCurrentAction() {
        var e = makeEntity();
        e.rechargeEnergy();
        Action a = stubAction();
        e.takeAction(a);
        assertSame(a, e.getCurrentAction());
    }

    @Test
    void takeAction_withNull_throwsIllegalStateException() {
        var e = makeEntity();
        assertThrows(IllegalStateException.class, () -> e.takeAction(null));
    }

    // --- clearAction ---

    @Test
    void clearAction_removesCurrentAction() {
        var e = makeEntity();
        e.rechargeEnergy();
        e.takeAction(stubAction());
        e.clearAction();
        assertNull(e.getCurrentAction());
    }

    // --- cancelCurrentAction ---

    @Test
    void cancelCurrentAction_refundsEnergy() {
        var e = makeEntity(); // speed = 1.0
        e.rechargeEnergy(); // energy = 1.0
        e.takeAction(stubAction()); // energy = 0.0
        e.cancelCurrentAction(); // refund → rechargeEnergy() → energy = 1.0
        assertEquals(1.0, e.getEnergy(), 1e-9, "Cancelled action should refund energy");
    }

    @Test
    void cancelCurrentAction_removesAction() {
        var e = makeEntity();
        e.rechargeEnergy();
        e.takeAction(stubAction());
        e.cancelCurrentAction();
        assertNull(e.getCurrentAction());
    }

    @Test
    void cancelCurrentAction_whenNoAction_doesNothing() {
        var e = makeEntity();
        // Should not throw
        assertDoesNotThrow(() -> e.cancelCurrentAction());
    }

    // --- energy floor ---

    @Test
    void useEnergyForAction_doesNotDropBelowZero() {
        var e = makeEntity(); // energy = 0.0
        e.useEnergyForAction(); // would go to -1.0 but should be floored
        assertTrue(e.getEnergy() >= 0.0, "Energy must never go below 0");
    }

    // --- alive helper ---

    @Test
    void alive_falseWithoutAttackableFeature() {
        var e = makeEntity();
        assertFalse(e.alive(), "Entity without ATTACKABLE feature should not be considered alive");
    }

    @Test
    void alive_trueWithPositiveHp() {
        var e = makeEntity();
        var attackable = new pl.lonski.dzibdzikon.entity.features.Attackable(10, 10, 1, 0);
        e.addFeature(FeatureType.ATTACKABLE, attackable);
        assertTrue(e.alive());
    }

    @Test
    void alive_falseAtZeroHp() {
        var e = makeEntity();
        var attackable = new pl.lonski.dzibdzikon.entity.features.Attackable(0, 10, 1, 0);
        e.addFeature(FeatureType.ATTACKABLE, attackable);
        assertFalse(e.alive(), "Entity with 0 HP should not be alive");
    }
}
