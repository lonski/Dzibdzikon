package pl.lonski.dzibdzikon.screen;

import static pl.lonski.dzibdzikon.Dzibdzikon.SHOW_WHOLE_LEVEL;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.HashMap;
import java.util.Map;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.map.Glyph;

public class GameScreen implements Screen {

    private final Dzibdzikon game;
    private final Hud hud;

    private Map<Glyph, TextureRegion> textures = new HashMap<>();

    private final World world;

    private BitmapFont font;

    public GameScreen(Dzibdzikon game) {
        this.game = game;

        textures.put(Glyph.PLAYER, new TextureRegion(new Texture("dzibdzik.png")));
        textures.put(Glyph.WALL, new TextureRegion(new Texture("wall_1.png")));
        textures.put(Glyph.FLOOR, new TextureRegion(new Texture("floor_1.png")));
        textures.put(Glyph.ZOMBIE, new TextureRegion(new Texture("zombie.png")));
        textures.put(Glyph.DOOR_OPEN, new TextureRegion(new Texture("door_open.png")));
        textures.put(Glyph.DOOR_CLOSED, new TextureRegion(new Texture("door_closed.png")));
        textures.put(Glyph.DOWNSTAIRS, new TextureRegion(new Texture("downstairs.png")));

        world = new World();

        hud = new Hud(game);

        Gdx.input.setInputProcessor(new DzibdziInput.InputHandler());
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {

        // update
        world.update(delta);

        game.camera.position.set(
                world.getPlayer().getCameraPosition().x(),
                world.getPlayer().getCameraPosition().y(),
                0);
        game.camera.update();
        hud.update(world);

        // render
        ScreenUtils.clear(0, 0, 0, 0);
        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();

        for (int y = 0; y < world.getCurrentLevel().getMap().getHeight(); y++) {
            for (int x = 0; x < world.getCurrentLevel().getMap().getWidth(); x++) {
                var pos = new Point(x, y);
                var renderPos = new Point(x * TILE_WIDTH, y * TILE_HEIGHT);
                var texture = textures.get(world.getCurrentLevel().getMap().getTile(x, y));
                float originX = texture.getRegionWidth() / 2f;
                float originY = texture.getRegionHeight() / 2f;

                if (SHOW_WHOLE_LEVEL || world.getCurrentLevel().getVisible().contains(pos)) {
                    game.batch.draw(
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
                    game.batch.setColor(0.5f, 0.5f, 0.5f, 1);
                    game.batch.draw(
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
                    game.batch.setColor(1, 1, 1, 1);
                }
            }
        }

        world.getCurrentLevel().getEntities().stream()
                .filter(e -> e.getFeature(FeatureType.POSITION) != null)
                .filter(e -> world.visible(e))
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
                    game.batch.draw(
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
                });

        game.batch.end();

        hud.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        game.camera.setToOrtho(false, Gdx.graphics.getWidth() * aspectRatio, Gdx.graphics.getHeight());
        game.camera.viewportWidth = Gdx.graphics.getWidth();
        game.camera.viewportHeight = Gdx.graphics.getHeight();
        game.camera.update();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        textures.values().forEach(t -> t.getTexture().dispose());
    }
}
