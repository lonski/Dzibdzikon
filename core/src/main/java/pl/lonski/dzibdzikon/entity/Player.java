package pl.lonski.dzibdzikon.entity;

import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.ExplosionSimulator;
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
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.entity.features.Regeneration;
import pl.lonski.dzibdzikon.entity.features.SpellBook;
import pl.lonski.dzibdzikon.map.TextureId;
import pl.lonski.dzibdzikon.spell.AcidPuddle;
import pl.lonski.dzibdzikon.spell.Fireball;
import pl.lonski.dzibdzikon.spell.SpikeSpell;

import java.util.List;

import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;

public class Player extends Entity {

    private ExplosionSimulator sim;
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
        addFeature(FeatureType.POSITION, new Position(new Point(0, 0), 0, 100));
        addFeature(FeatureType.FOV, new FieldOfView(this, 8));
        addFeature(FeatureType.ATTACKABLE, new Attackable(20, 20, 5, 0));
        addFeature(FeatureType.REGENERATION, new Regeneration(10, this));
        addFeature(FeatureType.SPELLBOOK, new SpellBook(List.of(new SpikeSpell(), new Fireball(), new AcidPuddle())));
        addFeature(FeatureType.INVENTORY, new Inventory());
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

        Position pos = getFeature(FeatureType.POSITION);
        setCameraPosition(
                new Point(pos.getCoords().x() * TILE_WIDTH, pos.getCoords().y() * TILE_HEIGHT));
    }

    @Override
    public boolean isHostile(Entity entity) {
        return entity.getFeature(FeatureType.ATTACKABLE) != null && (!(entity instanceof Player));
    }

    public static class InputListener implements DzibdziInput.DzibdziInputListener {

        DzibdziInput.DzibdziKey key;

        @Override
        public void onInput(DzibdziInput.DzibdziKey key) {
            if (key.released()) {
                this.key = null;
                return;
            }
            this.key = key;
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
    }
}
