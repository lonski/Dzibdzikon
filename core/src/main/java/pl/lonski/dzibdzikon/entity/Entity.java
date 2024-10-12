package pl.lonski.dzibdzikon.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.Action;
import pl.lonski.dzibdzikon.action.NoOpAction;
import pl.lonski.dzibdzikon.animation.Animation;
import pl.lonski.dzibdzikon.effect.Effect;
import pl.lonski.dzibdzikon.entity.features.Attackable;
import pl.lonski.dzibdzikon.entity.features.EntityFeature;
import pl.lonski.dzibdzikon.map.TextureId;

public class Entity {

    private final Map<FeatureType, EntityFeature> features = new HashMap<>();
    private final String name;
    private TextureId glyph;
    private boolean visibleInFog = false;
    private Action currentAction;
    private final List<Animation> animations = new ArrayList<>();
    private List<Effect> activeEffects = new ArrayList<>();
    private double speed = 1.0;
    private double energy = 0.0;
    private boolean flying = false;
    private BiConsumer<Entity, World> onAfterDeath;

    public Entity(String name, TextureId glyph) {
        this.name = name;
        this.glyph = glyph;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void applyEffect(Effect effect) {
        if (!effect.stackable()) {
            boolean effectExistsAlready = activeEffects.stream().anyMatch(e -> e.getClass()
                    .getSimpleName()
                    .equals(effect.getClass().getSimpleName()));
            if (effectExistsAlready) {
                return;
            }
        }

        effect.apply(this);
        if (effect.isActive()) {
            activeEffects.add(effect);
        } else {
            effect.remove(this);
        }
    }

    public void onTurnStarted(World world) {
        var remainedEffects = new ArrayList<Effect>();
        for (Effect effect : activeEffects) {
            effect.takeTurn(world, this);
            if (!effect.isActive()) {
                effect.remove(this);
            } else {
                remainedEffects.add(effect);
            }
        }
        this.activeEffects = remainedEffects;
    }

    public void addAnimation(Animation animation) {
        if (animation != null) {
            this.animations.add(animation);
        }
    }

    public void setGlyph(TextureId glyph) {
        this.glyph = glyph;
    }

    public Action getCurrentAction() {
        return currentAction;
    }

    public void takeAction(Action currentAction) {
        if (currentAction == null) {
            throw new IllegalStateException("Cant clear action here");
        }
        this.currentAction = currentAction;
        useEnergyForAction();
    }

    public void clearAction() {
        this.currentAction = null;
    }

    public String getName() {
        return name;
    }

    public TextureId getGlyph() {
        return glyph;
    }

    public boolean isVisibleInFog() {
        return visibleInFog;
    }

    public void setVisibleInFog(boolean visibleInFog) {
        this.visibleInFog = visibleInFog;
    }

    public <T> T getFeature(FeatureType type) {
        if (features.containsKey(type)) {
            return (T) features.get(type);
        }
        return null;
    }

    public void addFeature(FeatureType type, EntityFeature feature) {
        this.features.put(type, feature);
    }

    public boolean anyActionBlockingEffectsActive() {
        return activeEffects.stream().anyMatch(Effect::blockEntityActingPossibility);
    }

    public void update(float delta, World world) {
        if (anyActionBlockingEffectsActive()) {
            takeAction(new NoOpAction());
            return;
        }

        features.values().forEach(f -> f.update(delta, world));
        // make sure non-player entity always take a turn
        if (getFeature(FeatureType.PLAYER) == null && currentAction == null) {
            takeAction(new NoOpAction());
        }
    }

    public void updateAnimation(float delta, World world) {
        animations.forEach(a -> a.update(delta, world));
        animations.removeIf(Animation::isDone);
    }

    public List<Animation> getAnimations() {
        return animations;
    }

    public boolean alive() {
        Attackable attackable = getFeature(FeatureType.ATTACKABLE);
        return attackable != null && attackable.getHp() > 0;
    }

    public void rechargeEnergy() {
        this.energy += speed;
    }

    public boolean hasEnergyForAction() {
        return energy >= 1.0;
    }

    public void cancelCurrentAction() {
        if (currentAction != null) {
            currentAction = null;
            rechargeEnergy();
        }
    }

    public void useEnergyForAction() {
        energy -= 1.0;
        if (energy < 0.0) {
            System.out.println("Energy dropped below 0.0: " + energy + " for " + name);
            energy = 0.0;
        }
    }

    public boolean isHostile(Entity entity) {
        return entity instanceof Player;
    }

    public double getEnergy() {
        return energy;
    }

    public boolean isFlying() {
        return flying;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public void finishAllAnimation() {
        this.animations.forEach(Animation::finish);
        this.animations.clear();
    }

    public void setOnAfterDeath(BiConsumer<Entity, World> onAfterDeath) {
        this.onAfterDeath = onAfterDeath;
    }

    public void onAfterDeath(World world) {
        if (onAfterDeath != null) {
            onAfterDeath.accept(this, world);
        }
    }
}
