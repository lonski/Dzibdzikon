package pl.lonski.dzibdzikon.entity.features;

import com.badlogic.gdx.graphics.Color;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.animation.TextFlowUpAnimation;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;

public class Regeneration implements EntityFeature {

    private final int hpRegenRateTurns;
    private int hpRegenCooldown;
    private final int mpRegenRateTurns;
    private int mpRegenCooldown;
    private long lastTurnCounter = -1;
    private final Entity entity;

    public Regeneration(int hpRegenRateTurns, Entity entity) {
        this(hpRegenRateTurns, hpRegenRateTurns, entity);
    }

    public Regeneration(int hpRegenRateTurns, int mpRegenRateTurns, Entity entity) {
        this.entity = entity;
        this.hpRegenRateTurns = hpRegenRateTurns;
        this.mpRegenRateTurns = hpRegenRateTurns;
        this.hpRegenCooldown = hpRegenRateTurns;
        this.mpRegenCooldown = mpRegenRateTurns;
    }

    @Override
    public void update(float delta, World world) {
        if (lastTurnCounter < 0) {
            lastTurnCounter = world.getTurn();
            return;
        }

        var fov = entity.<FieldOfView>getFeature(FeatureType.FOV);
        if (fov.getHostiles().isEmpty()) {
            var turnsPassed = world.getTurn() - lastTurnCounter;
            hpRegenCooldown -= Math.max(0, turnsPassed);
            mpRegenCooldown -= Math.max(0, turnsPassed);
        }

        var attackable = entity.<Attackable>getFeature(FeatureType.ATTACKABLE);
        if (attackable != null && hpRegenCooldown <= 0) {
            hpRegenCooldown = hpRegenRateTurns;
            if (attackable.getHp() < attackable.getMaxHp()) {
                attackable.setHp(Math.min(attackable.getHp() + 1, attackable.getMaxHp()));
                entity.addAnimation(
                        new TextFlowUpAnimation("+1", entity.getPosition().getCoords(), Color.CHARTREUSE));
            }
        }

        var manaUser = entity.<MagicUser>getFeature(FeatureType.MAGIC_USER);
        if (manaUser != null && mpRegenCooldown <= 0) {
            mpRegenCooldown = mpRegenRateTurns;
            if (manaUser.getMana() < manaUser.getManaMax()) {
                manaUser.modMana(1);
                entity.addAnimation(
                        new TextFlowUpAnimation("+1", entity.getPosition().getCoords(), Color.BLUE));
            }
        }

        lastTurnCounter = world.getTurn();
    }
}
