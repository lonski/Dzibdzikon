package pl.lonski.dzibdzikon.screen;

import static com.badlogic.gdx.graphics.glutils.HdpiUtils.glViewport;
import static pl.lonski.dzibdzikon.Dzibdzikon.SHOW_WHOLE_LEVEL;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;
import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.utils.ScreenUtils;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.animation.Animation;

public class GameScreen extends DzibdzikonScreen {

    private final Hud hud;
    private final World world;

    public GameScreen(Dzibdzikon dzibdzikon) {
        super(dzibdzikon);
        world = new World();
        hud = new Hud(world);
        getGameResources().windowManager.init(world);
        Gdx.input.setInputProcessor(new InputMultiplexer(
            hud,
                new GestureDetector(new DzibdziInput.GestureHandler()), new DzibdziInput.InputHandler()));
    }

    private void update(float delta) {
        if (!getGameResources().windowManager.isAnyWindowVisible()) {
            world.update(delta);
        }

        getGameResources().windowManager.update(delta);

        if (!world.getPlayer().alive()) {
            dzibdzikon.gameOver();
        }

        getGameResources()
                .camera
                .position
                .set(
                        world.getPlayer().getCameraPosition().x(),
                        world.getPlayer().getCameraPosition().y(),
                        0);
        getGameResources().camera.update();
        getGameResources().uiCamera.update();
//        hud.update(world);
        hud.update(delta, world);
    }

    @Override
    public void render(float delta) {
        update(delta);

        // render
        var batch = getGameResources().batch;
        var camera = getGameResources().camera;
        var textures = getGameResources().textures;

        glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ScreenUtils.clear(0, 0, 0, 0);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (int y = 0; y < world.getCurrentLevel().getMap().getHeight(); y++) {
            for (int x = 0; x < world.getCurrentLevel().getMap().getWidth(); x++) {
                var pos = new Point(x, y);
                var renderPos = new Point(x * TILE_WIDTH, y * TILE_HEIGHT);
                var texture = textures.get(world.getCurrentLevel().getMap().getTile(x, y));
                float originX = texture.getRegionWidth() / 2f;
                float originY = texture.getRegionHeight() / 2f;

                if (SHOW_WHOLE_LEVEL || world.getCurrentLevel().getVisible().contains(pos)) {
                    batch.draw(
                            texture,
                            renderPos.x() - originX,
                            renderPos.y() - originY,
                            originX,
                            originY,
                            texture.getRegionWidth(),
                            texture.getRegionHeight(),
                            1.0f,
                            1.0f,
                            0);
                } else if (world.getCurrentLevel().getVisited().contains(pos)) {
                    batch.setColor(0.5f, 0.5f, 0.5f, 1);
                    batch.draw(
                            texture,
                            renderPos.x() - originX,
                            renderPos.y() - originY,
                            originX,
                            originY,
                            texture.getRegionWidth(),
                            texture.getRegionHeight(),
                            1.0f,
                            1.0f,
                            0);
                    batch.setColor(1, 1, 1, 1);
                }
            }
        }

        world.getCurrentLevel().getTileEffects().forEach(((point, tileEffects) -> {
            if (world.visible(point)) {
                tileEffects.forEach(e -> e.render(point));
            }
        }));

        world.getCurrentLevel().getEntitiesWithPosition().stream()
                .filter(e -> world.visible(e.getKey()))
                .sorted((e1, e2) -> {
                    var p1 = e1.getValue();
                    var p2 = e2.getValue();
                    return Integer.compare(p1.getzLevel(), p2.getzLevel());
                })
                .forEach(e -> {
                    var pos = e.getValue();
                    var entity = e.getKey();
                    var tex = textures.get(entity.getGlyph());
                    float originX = tex.getRegionWidth() / 2f;
                    float originY = tex.getRegionHeight() / 2f;
                    batch.draw(
                            tex,
                            pos.getRenderPosition().x() - originX,
                            pos.getRenderPosition().y() - originY,
                            originX,
                            originY,
                            tex.getRegionWidth(),
                            tex.getRegionHeight(),
                            1.0f,
                            1.0f,
                            pos.getRotation() // Rotation angle in degrees
                            );
                    entity.getAnimations().forEach(Animation::render);
                });

        batch.end();

        getGameResources().windowManager.render(delta);

        hud.render(delta);
//        hud.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        hud.getViewport().update(width, height, true);
    }
}
