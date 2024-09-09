package pl.lonski.dzibdzikon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.security.SecureRandom;
import pl.lonski.dzibdzikon.screen.GameScreen;

// TODO:
// - pathfinding coś chyba nie bangla na 100%
// - heal over time
public class Dzibdzikon extends Game {

    public static boolean SHOW_WHOLE_LEVEL = false;
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;

    public static SecureRandom RANDOM = new SecureRandom();

    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public BitmapFont fontItalic;
    public BitmapFont fontBoldItalic;
    public OrthographicCamera camera;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        fontItalic = FontUtils.createFont("font/DejaVuSerif-Italic.ttf", 15);
        fontBoldItalic = FontUtils.createFont("font/DejaVuSerif-BoldItalic.ttf", 15);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        this.setScreen(new GameScreen(this));
    }

    @Override
    public void render() {
        super.render();

    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        fontItalic.dispose();
    }
}
