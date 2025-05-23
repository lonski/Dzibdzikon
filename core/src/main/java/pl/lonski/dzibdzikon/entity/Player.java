package pl.lonski.dzibdzikon.entity;

import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;

import java.util.List;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.command.CastSpellCommand;
import pl.lonski.dzibdzikon.command.CloseCommand;
import pl.lonski.dzibdzikon.command.Command;
import pl.lonski.dzibdzikon.command.GoDownCommand;
import pl.lonski.dzibdzikon.command.OpenInventoryCommand;
import pl.lonski.dzibdzikon.command.PickupCommand;
import pl.lonski.dzibdzikon.command.PositionChangeCommand;
import pl.lonski.dzibdzikon.command.UseQuickbarCommand;
import pl.lonski.dzibdzikon.command.WaitCommand;
import pl.lonski.dzibdzikon.entity.features.Attackable;
import pl.lonski.dzibdzikon.entity.features.EntityFeature;
import pl.lonski.dzibdzikon.entity.features.FieldOfView;
import pl.lonski.dzibdzikon.entity.features.Inventory;
import pl.lonski.dzibdzikon.entity.features.MagicUser;
import pl.lonski.dzibdzikon.entity.features.Regeneration;
import pl.lonski.dzibdzikon.map.TextureId;
import pl.lonski.dzibdzikon.spell.Burn;
import pl.lonski.dzibdzikon.spell.SpikeSpell;

public class Player extends Entity {

    private final InputListener input = new InputListener();
    private Point cameraPosition;
    private final List<Command> commands = List.of(
            new PositionChangeCommand(this),
            new WaitCommand(),
            new CloseCommand(),
            new GoDownCommand(),
            new CastSpellCommand(),
            new UseQuickbarCommand(),
            new PickupCommand(),
            new OpenInventoryCommand());
    private final Quickbar quickbar;

    public Player() {
        super("Dzibdzik", TextureId.PLAYER);
        this.quickbar = new Quickbar();
        setSpeed(1f);
        addFeature(FeatureType.PLAYER, new EntityFeature() {});
        addFeature(FeatureType.FOV, new FieldOfView(this, 8));
        addFeature(FeatureType.ATTACKABLE, new Attackable(20, 20, 5, 0));
        addFeature(FeatureType.REGENERATION, new Regeneration(10, 3, this));
        addFeature(FeatureType.MAGIC_USER, new MagicUser(List.of(new SpikeSpell(), new Burn()), 100, 100));
        addFeature(FeatureType.INVENTORY, new Inventory());
        getPosition().setzLevel(100);
    }

    public Quickbar getQuickbar() {
        return quickbar;
    }

    public Point getCameraPosition() {
        return cameraPosition;
    }

    public void setCameraPosition(Point cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

    public InputListener getInputListener() {
        return input;
    }

    @Override
    public void update(float delta, World world) {
        if (getCurrentAction() != null || !alive()) {
            return;
        }

        commands.forEach(c -> c.update(delta));

        if (!input.empty()) {
            //            if (input.key.click() != null) {
            //                var click = input.key.click();
            //                var center = click.toCoords();
            //                if (sim == null || !sim.hasMoreSteps()) {
            //                    sim = new ExplosionSimulator(center, 3, world);
            //                }
            //
            //                Hud.debugHighlight.clear();
            //                Hud.debugHighlight.addAll(sim.step());
            //                input.reset();
            //                return;
            //            }

            commands.stream()
                    .filter(c -> c.accept(input.key))
                    .findFirst()
                    .ifPresent(command -> command.execute(this, world));
        }

        super.update(delta, world);

        setCameraPosition(new Point(
                getPosition().getCoords().x() * TILE_WIDTH,
                getPosition().getCoords().y() * TILE_HEIGHT));
    }

    @Override
    public boolean isHostile(Entity entity) {
        return entity.getFeature(FeatureType.ATTACKABLE) != null && (!(entity instanceof Player));
    }

    public static class InputListener implements DzibdziInput.DzibdziInputListener {

        DzibdziInput.DzibdziKey key;

        @Override
        public boolean onInput(DzibdziInput.DzibdziKey key) {
            if (key.released()) {
                this.key = null;
                return true;
            }
            this.key = key;
            return true;
        }

        public void reset() {
            key = null;
        }

        public boolean empty() {
            return key == null;
        }

        public DzibdziInput.DzibdziKey getKey() {
            return key;
        }

        public void resetClick() {
            if (this.key.touchCoords() != null) {
                reset();
            }
        }
    }
}
