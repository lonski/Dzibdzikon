package pl.lonski.dzibdzikon.entity.features;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests for the melee combat formula in Attackable.
 *
 * <p>Intent: verify that combat works as designed — hit requires beating the defence roll,
 * damage equals the difference, and stats influence outcomes as expected.
 */
public class AttackableTest {

    // --- Hit / miss consistency ---

    @Test
    void hitResult_alwaysHasPositiveDamage() {
        // When the result says "hit", damage must be > 0 (attackRoll > defenceRoll means damage > 0)
        var attacker = new Attackable(10, 10, 10, 0);
        var target = new Attackable(10, 10, 0, 0);

        for (int i = 0; i < 200; i++) {
            var result = attacker.attack(target);
            if (result.hit()) {
                assertTrue(result.damage() > 0, "A hit must deal positive damage, got: " + result.damage());
            }
        }
    }

    @Test
    void missResult_alwaysHasZeroDamage() {
        // When the result says "miss", damage must be 0
        var attacker = new Attackable(10, 10, 10, 0);
        var target = new Attackable(10, 10, 0, 0);

        for (int i = 0; i < 200; i++) {
            var result = attacker.attack(target);
            if (!result.hit()) {
                assertEquals(0, result.damage(), "A miss must deal zero damage");
            }
        }
    }

    // --- Zero-attack attacker ---

    @Test
    void zeroAttack_neverHits() {
        // attackRoll = random(0, 1) = always 0; defenceRoll >= 0; 0 > 0 is false → never hits
        var attacker = new Attackable(10, 10, 0, 0);
        var target = new Attackable(10, 10, 0, 5);

        for (int i = 0; i < 500; i++) {
            var result = attacker.attack(target);
            assertFalse(result.hit(), "Attacker with 0 attack should never hit");
            assertEquals(0, result.damage(), "Attacker with 0 attack should deal 0 damage");
        }
    }

    @Test
    void zeroAttack_neverHitsEvenAgainstZeroDefence() {
        // attackRoll = 0, defenceRoll = 0; 0 > 0 is false → never hits
        var attacker = new Attackable(10, 10, 0, 0);
        var target = new Attackable(10, 10, 0, 0);

        for (int i = 0; i < 500; i++) {
            var result = attacker.attack(target);
            assertFalse(result.hit(), "0 attack vs 0 defence: attack roll 0 never beats defence roll 0");
        }
    }

    // --- Defence effectiveness ---

    @Test
    void highDefence_reducesHitRate() {
        // With same attacker, higher defence should result in fewer hits
        var attacker = new Attackable(10, 10, 6, 0);
        var targetLowDef = new Attackable(10, 10, 0, 0);
        var targetHighDef = new Attackable(10, 10, 0, 10);

        int hitsVsLow = 0;
        int hitsVsHigh = 0;
        int iterations = 2000;

        for (int i = 0; i < iterations; i++) {
            if (attacker.attack(targetLowDef).hit()) hitsVsLow++;
            if (attacker.attack(targetHighDef).hit()) hitsVsHigh++;
        }

        assertTrue(hitsVsLow > hitsVsHigh,
                "Higher defence should reduce hit rate; hitsVsLow=" + hitsVsLow + " hitsVsHigh=" + hitsVsHigh);
    }

    @Test
    void highAttackVsNoDefence_hitsFrequently() {
        // attack=10 vs defence=0: attacker rolls 0..10, defender rolls 0..0=0
        // hit when attackRoll > 0, i.e. ~90% of the time (10 out of 11 rolls)
        var attacker = new Attackable(10, 10, 10, 0);
        var target = new Attackable(10, 10, 0, 0);

        int hits = 0;
        for (int i = 0; i < 1000; i++) {
            if (attacker.attack(target).hit()) hits++;
        }

        assertTrue(hits > 700, "High attack vs no defence should hit >70% of the time; got: " + hits + "/1000");
    }

    // --- Damage value ---

    @Test
    void damage_isAtMostAttackValue() {
        // Max damage = max attackRoll - min defenceRoll = attack - 0 = attack
        var attacker = new Attackable(10, 10, 5, 0);
        var target = new Attackable(10, 10, 0, 0);

        for (int i = 0; i < 500; i++) {
            var result = attacker.attack(target);
            assertTrue(result.damage() <= 5, "Damage cannot exceed attacker's attack value; got: " + result.damage());
        }
    }

    @Test
    void fightResult_record_accessors_work() {
        var hit = new Attackable.FightResult(true, 3);
        assertTrue(hit.hit());
        assertEquals(3, hit.damage());

        var miss = new Attackable.FightResult(false, 0);
        assertFalse(miss.hit());
        assertEquals(0, miss.damage());
    }
}
