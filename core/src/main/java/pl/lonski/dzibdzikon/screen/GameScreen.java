package pl.lonski.dzibdzikon.screen;

import static pl.lonski.dzibdzikon.Dzibdzikon.SHOW_WHOLE_LEVEL;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.HashMap;
import java.util.Map;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.entity.Entity;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.map.Glyph;

public class GameScreen implements Screen {

    private final Dzibdzikon game;
    private final Hud hud;

    private Map<Glyph, Texture> textures = new HashMap<>();

    private World world;

    private BitmapFont font;

    public GameScreen(Dzibdzikon game) {
        this.game = game;

        textures.put(Glyph.PLAYER, new Texture("dzibdzik.png"));
        textures.put(Glyph.WALL, new Texture("wall_1.png"));
        textures.put(Glyph.FLOOR, new Texture("floor_1.png"));
        textures.put(Glyph.ZOMBIE, new Texture("zombie.png"));

        world = new World();

        hud = new Hud(game);

        Gdx.input.setInputProcessor(new DzibdziInput.InputHandler());
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        //update
        world.update(delta);

        game.camera.position.set(world.getPlayer().getCameraPosition().x(), world.getPlayer().getCameraPosition().y(), 0);
        game.camera.update();
        hud.update(world);

        //render
        ScreenUtils.clear(0, 0, 0, 0);
        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();

        for (int y = 0; y < world.getCurrentLevel().getMap().getHeight(); y++) {
            for (int x = 0; x < world.getCurrentLevel().getMap().getWidth(); x++) {
                var pos = new Point(x, y);
                Texture texture = textures.get(world.getCurrentLevel().getMap().getTile(x, y));
                if (SHOW_WHOLE_LEVEL || world.getCurrentLevel().getVisible().contains(pos)) {
                    game.batch.draw(texture, x * TILE_WIDTH, y * TILE_HEIGHT);
                } else if (world.getCurrentLevel().getVisited().contains(pos)) {
                    game.batch.setColor(0.5f, 0.5f, 0.5f, 1);
                    game.batch.draw(texture, x * TILE_WIDTH, y * TILE_HEIGHT);
                    game.batch.setColor(1, 1, 1, 1);
                }
            }
        }

        for (Entity entity : world.getCurrentLevel().getEntities()) {
            var pos = entity.<Position>getFeature(FeatureType.POSITION);
            if (pos == null || !world.visible(entity)) {
                continue;
            }
            game.batch.draw(textures.get(entity.getGlyph()), pos.getRenderPosition().x(), pos.getRenderPosition().y());
        }

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
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        textures.values().forEach(Texture::dispose);
    }
}
