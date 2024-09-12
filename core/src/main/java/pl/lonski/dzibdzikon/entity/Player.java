package pl.lonski.dzibdzikon.entity;

import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;

import com.badlogic.gdx.Input;
import java.util.List;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.AttackAction;
import pl.lonski.dzibdzikon.action.MoveAction;
import pl.lonski.dzibdzikon.command.CastSpellCommand;
import pl.lonski.dzibdzikon.command.CloseCommand;
import pl.lonski.dzibdzikon.command.Command;
import pl.lonski.dzibdzikon.command.GoDownCommand;
import pl.lonski.dzibdzikon.command.WaitCommand;
import pl.lonski.dzibdzikon.entity.features.Attackable;
import pl.lonski.dzibdzikon.entity.features.EntityFeature;
import pl.lonski.dzibdzikon.entity.features.FieldOfView;
import pl.lonski.dzibdzikon.entity.features.Openable;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.entity.features.Regeneration;
import pl.lonski.dzibdzikon.entity.features.SpellBook;
import pl.lonski.dzibdzikon.map.Glyph;
import pl.lonski.dzibdzikon.screen.WindowManager;

public class Player extends Entity {

    private final InputListener input = new InputListener();
    private Point cameraPosition;
    private final Dzibdzikon game;
    private final List<Command> commands =
            List.of(new WaitCommand(), new CloseCommand(), new GoDownCommand(), new CastSpellCommand());

    public Player(Dzibdzikon game) {
        super("Dzibdzik", Glyph.PLAYER);
        this.game = game;
        setSpeed(1f);
        addFeature(FeatureType.PLAYER, new EntityFeature() {});
        addFeature(FeatureType.POSITION, new Position(new Point(0, 0), 0, 100));
        addFeature(FeatureType.FOV, new FieldOfView(this, 8));
        addFeature(FeatureType.ATTACKABLE, new Attackable(20, 20, 5, 0));
        addFeature(FeatureType.REGENERATION, new Regeneration(10, this));
        addFeature(FeatureType.SPELLBOOK, new SpellBook());
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
            Point dPos = getPositionChangeInput();

            if (dPos.isZero()) {
                commands.stream()
                        .filter(c -> c.accepts(input.key))
                        .findFirst()
                        .ifPresent(command -> command.execute(this, world));
            } else {
                // handle position change
                Position pos = getFeature(FeatureType.POSITION);
                Point targetPos = new Point(
                        pos.getCoords().x() + dPos.x(), pos.getCoords().y() + dPos.y());

                // move
                if (!world.getCurrentLevel().isObstacle(targetPos)) {
                    setCurrentAction(new MoveAction(
                            this,
                            new Point(
                                    pos.getCoords().x() + dPos.x(),
                                    pos.getCoords().y() + dPos.y())));
                } else {

                    // Check fight possibility
                    world.getCurrentLevel()
                            .getEntityAt(targetPos, FeatureType.ATTACKABLE)
                            .ifPresentOrElse(
                                    mob -> setCurrentAction(new AttackAction(this, mob)),
                                    // check openable
                                    // TODO: change to action
                                    () -> world.getCurrentLevel()
                                            .getEntityAt(targetPos, FeatureType.OPENABLE)
                                            .ifPresent(openable -> {
                                                openable.<Openable>getFeature(FeatureType.OPENABLE)
                                                        .open(world);
                                                input.reset();
                                            }));
                    //                    input.reset(); // do not process the same key again
                }
            }
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

    public Point getPositionChangeInput() {
        Point dpos = new Point(0, 0);

        if (input.empty()) {
            return dpos;
        }

        if (input.key.keyCode() == Input.Keys.NUMPAD_4
                || input.key.keyCode() == Input.Keys.LEFT
                || input.key.keyCode() == Input.Keys.H) {
            dpos = new Point(-1, 0);
        } else if (input.key.keyCode() == Input.Keys.NUMPAD_6
                || input.key.keyCode() == Input.Keys.RIGHT
                || input.key.keyCode() == Input.Keys.L) {
            dpos = new Point(1, 0);
        } else if (input.key.keyCode() == Input.Keys.NUMPAD_8
                || input.key.keyCode() == Input.Keys.UP
                || input.key.keyCode() == Input.Keys.K) {
            dpos = new Point(0, 1);
        } else if (input.key.keyCode() == Input.Keys.NUMPAD_2
                || input.key.keyCode() == Input.Keys.DOWN
                || input.key.keyCode() == Input.Keys.J) {
            dpos = new Point(0, -1);
        } else if (input.key.keyCode() == Input.Keys.NUMPAD_7 || input.key.keyCode() == Input.Keys.Y) {
            dpos = new Point(-1, 1);
        } else if (input.key.keyCode() == Input.Keys.NUMPAD_9 || input.key.keyCode() == Input.Keys.U) {
            dpos = new Point(1, 1);
        } else if (input.key.keyCode() == Input.Keys.NUMPAD_1 || input.key.keyCode() == Input.Keys.B) {
            dpos = new Point(-1, -1);
        } else if (input.key.keyCode() == Input.Keys.NUMPAD_3 || input.key.keyCode() == Input.Keys.N) {
            dpos = new Point(1, -1);
        }

        return dpos;
    }

    public WindowManager getWindowManager() {
        return game.windowManager;
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
    }
}
