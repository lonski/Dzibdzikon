package pl.lonski.dzibdzikon.entity.features;

import com.badlogic.gdx.graphics.Color;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.animation.TextFlowUpAnimation;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;

public class Regeneration implements EntityFeature {

    private final int hpRegenRateTurns;
    private int regenCooldown;
    private long lastTurnCounter = -1;
    private final Entity entity;
    private final Attackable attackable;
    private final FieldOfView fov;

    public Regeneration(int hpRegenRateTurns, Entity entity) {
        this.entity = entity;
        this.attackable = entity.getFeature(FeatureType.ATTACKABLE);
        this.fov = entity.getFeature(FeatureType.FOV);
        this.hpRegenRateTurns = hpRegenRateTurns;
        this.regenCooldown = hpRegenRateTurns;
    }

    @Override
    public void update(float delta, World world) {
        if (lastTurnCounter < 0) {
            lastTurnCounter = world.getTurn();
            return;
        }

        if (fov.getHostiles().isEmpty()) {
            var turnsPassed = world.getTurn() - lastTurnCounter;
            regenCooldown -= Math.max(0, turnsPassed);
        }

        if (regenCooldown <= 0) {
            regenCooldown = hpRegenRateTurns;
            if (attackable.getHp() < attackable.getMaxHp()) {
                attackable.setHp(Math.min(attackable.getHp() + 1, attackable.getMaxHp()));
                entity.addAnimation(new TextFlowUpAnimation(
                        "+1", entity.<Position>getFeature(FeatureType.POSITION).getCoords(), Color.CHARTREUSE));
            }
        }

        lastTurnCounter = world.getTurn();
    }
}
