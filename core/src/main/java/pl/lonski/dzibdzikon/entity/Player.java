package pl.lonski.dzibdzikon.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.action.MoveAction;
import pl.lonski.dzibdzikon.entity.features.EntityFeature;
import pl.lonski.dzibdzikon.entity.features.FieldOfView;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.map.Glyph;

public class Player extends Entity {

    public Player() {
        super("Dzibdzik", Glyph.PLAYER, 100);
        addFeature(FeatureType.PLAYER, new EntityFeature() {
        });
        addFeature(FeatureType.POSITION, new Position(new Point(0, 0)));
        addFeature(FeatureType.FOV, new FieldOfView(this, 8));
//        addFeature(FeatureType.ATTACKABLE, new Attackable(20, 20, 5, 0));
    }

    @Override
    public void update(float delta, World world) {

        if (getCurrentAction() != null) {
            return;
        }

        Point dpos = new Point(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_4)) {
            dpos = new Point(-1, 0);
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_6)) {
            dpos = new Point(1, 0);
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_8)) {
            dpos = new Point(0, 1);
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_2)) {
            dpos = new Point(0, -1);
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_7)) {
            dpos = new Point(-1, 1);
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_9)) {
            dpos = new Point(1, 1);
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_1)) {
            dpos = new Point(-1, -1);
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_3)) {
            dpos = new Point(1, -1);
        }

        if (!dpos.isZero()) {
            Position pos = getFeature(FeatureType.POSITION);
            Point targetPos = new Point(pos.getCoords().x() + dpos.x(), pos.getCoords().y() + dpos.y());
            if (!world.getCurrentLevel().isObstacle(targetPos)) {
                setCurrentAction(new MoveAction(this, new Point(pos.getCoords().x() + dpos.x(), pos.getCoords().y() + dpos.y())));
            }
        }

        super.update(delta, world);
    }

    //    public PlayerCommandResult hanldeCommand(World world, KeyEvent key) {
//        var player = world.getPlayer();
//        var playerPos = player.<Position>getFeature(FeatureType.POSITION);
//        var level = world.getCurrentLevel();
//
//        // execute scheduled command
//        if (command != null) {
//            var commandResult = command.run(world, key);
//            command = commandResult.nextCommand();
//            return new PlayerCommandResult(commandResult.turns());
//        }
//
//        // handle command key
//        if (key.getKeyChar() == 'c') {
//            Dzibdzikon.getHud().addMessage("Choose direction to close..");
//            command = new CloseCommand(playerPos.getPos());
//            return new PlayerCommandResult(1);
//        }
//
//        if (key.getKeyChar() == '>') {
//            new DescendCommand().run(world, key);
//            return new PlayerCommandResult(0);
//        }
//
//        // wait
//        if (key.getKeyCode() == KeyEvent.VK_NUMPAD5 || key.getKeyCode() == KeyEvent.VK_SPACE) {
//            return new PlayerCommandResult(1);
//
//        }
//
//        // handle position change key
//        int newPos = PositionUtils.getPositionForDirection(playerPos.getPos(), key);
//        if (newPos != playerPos.getPos()) {
//            // move
//            if (!level.isObstacle(newPos)) {
//                playerPos.setPos(newPos);
//            } else {
//                // Check fight possibility
//                level.getEntityAt(newPos, FeatureType.ATTACKABLE)
//                        .ifPresentOrElse(
//                                mob -> {
//                                    var res = new Fight().perform(player, mob);
//                                    Dzibdzikon.getHud().addMessage(res.message());
//                                    world.addAnimation(res.animation());
//                                    if (res.died()) {
//                                        Dzibdzikon.getHud()
//                                                .addMessage("Killed "
//                                                        + mob.getName().toLowerCase());
//                                        level.removeEntity(mob);
//                                    }
//                                },
//                                // check openable
//                                () -> level.getEntityAt(newPos, FeatureType.OPENABLE)
//                                        .ifPresent(openable -> {
//                                            openable.<Openable>getFeature(FeatureType.OPENABLE)
//                                                    .open(world);
//                                        }));
//            }
//
//            // moved/attacked/opened
//            return new PlayerCommandResult(1);
//        }
//
//        // no op
//        return new PlayerCommandResult(0);
//    }
//
//    public record PlayerCommandResult(int turns) {}
}
