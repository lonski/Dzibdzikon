package pl.lonski.dzibdzikon;

import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.HashMap;
import java.util.Map;
import pl.lonski.dzibdzikon.action.Action;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Player;
import pl.lonski.dzibdzikon.entity.features.Position;
import pl.lonski.dzibdzikon.map.Glyph;

public class GameScreen implements Screen {

    private final Dzibdzikon game;

    private Map<Glyph, Texture> textures = new HashMap<>();

    private OrthographicCamera camera;
    private Player player;
    private World world;
    private Action currentAction;

    public GameScreen(Dzibdzikon game) {
        this.game = game;

        textures.put(Glyph.PLAYER, new Texture("dzibdzik.png"));
        textures.put(Glyph.WALL, new Texture("wall_1.png"));
        textures.put(Glyph.FLOOR, new Texture("floor_1.png"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        world = new World();
        player = new Player();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        //update
        if (currentAction == null) {
            player.update(delta, world);
            currentAction = player.getCurrentAction();
        } else {
            currentAction.update(delta);
            if (currentAction.isDone()) {
                currentAction = null;
                player.setCurrentAction(null);
            }
        }

        var playerPos = player.<Position>getFeature(FeatureType.POSITION);
        camera.position.set(playerPos.getRenderPosition().x(), playerPos.getRenderPosition().y(), 0);
        camera.update();

        //render
        ScreenUtils.clear(0, 0, 0, 0);
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        for (int y = 0; y < world.getMap().getHeight(); y++) {
            for (int x = 0; x < world.getMap().getWidth(); x++) {
                Texture texture = textures.get(world.getMap().getTile(x, y));
                game.batch.draw(texture, x * TILE_WIDTH, y * TILE_HEIGHT);
            }
        }

        game.batch.draw(textures.get(player.getGlyph()), playerPos.getRenderPosition().x(), playerPos.getRenderPosition().y());

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        camera.setToOrtho(false, Gdx.graphics.getWidth() * aspectRatio, Gdx.graphics.getHeight());

        camera.viewportWidth = Gdx.graphics.getWidth();
        camera.viewportHeight = Gdx.graphics.getHeight();
        camera.update();
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
