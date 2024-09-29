package pl.lonski.dzibdzikon.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.animation.Animation;
import pl.lonski.dzibdzikon.effect.TileEffect;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Position;

import static pl.lonski.dzibdzikon.Dzibdzikon.SHOW_WHOLE_LEVEL;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;
import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

public class GameScreen extends DzibdzikonScreen {

    private final Hud hud;
    private final World world;

    public GameScreen(Dzibdzikon dzibdzikon) {
        super(dzibdzikon);
        world = new World();
        hud = new Hud();
        getGameResources().windowManager.init(world);
        Gdx.input.setInputProcessor(new DzibdziInput.InputHandler());
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
        hud.update(world);
    }

    @Override
    public void render(float delta) {
        update(delta);

        // render
        var batch = getGameResources().batch;
        var camera = getGameResources().camera;
        var textures = getGameResources().textures;
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

        world.getCurrentLevel().getEntities().stream()
                .filter(e -> e.getFeature(FeatureType.POSITION) != null)
                .filter(world::visible)
                .sorted((e1, e2) -> {
                    var p1 = e1.<Position>getFeature(FeatureType.POSITION);
                    var p2 = e2.<Position>getFeature(FeatureType.POSITION);
                    return Integer.compare(p1.getzLevel(), p2.getzLevel());
                })
                .forEach(entity -> {
                    var pos = entity.<Position>getFeature(FeatureType.POSITION);
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
    }
}
