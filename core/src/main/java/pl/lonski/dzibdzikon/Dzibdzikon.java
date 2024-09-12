package pl.lonski.dzibdzikon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import pl.lonski.dzibdzikon.map.Glyph;
import pl.lonski.dzibdzikon.screen.GameMenu;
import pl.lonski.dzibdzikon.screen.GameOver;
import pl.lonski.dzibdzikon.screen.GameScreen;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import pl.lonski.dzibdzikon.screen.WindowManager;

// TODO:
// - pathfinding coś chyba nie bangla na 100%
// - spells:
//      * spellbook ui
//      * targeting entities
//      * firing a kolec
public class Dzibdzikon extends Game {

    public static boolean SHOW_WHOLE_LEVEL = false;
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;

    public static SecureRandom RANDOM = new SecureRandom();

    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public BitmapFont fontItalic;
    public BitmapFont fontBoldItalic;
    public BitmapFont bigFont;
    public OrthographicCamera camera;
    public final Map<Glyph, TextureRegion> textures = new HashMap<>();
    public final WindowManager windowManager = new WindowManager();

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        fontItalic = FontUtils.createFont("font/DejaVuSerif-Italic.ttf", 15);
        fontBoldItalic = FontUtils.createFont("font/DejaVuSerif-BoldItalic.ttf", 15);
        bigFont = FontUtils.createFont("font/DejaVuSerif-Italic.ttf", 32);

        for (Glyph glyph : Glyph.values()) {
            textures.put(glyph, new TextureRegion(new Texture(glyph.getFilename())));
        }

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        gameMenu();
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
        fontBoldItalic.dispose();
        bigFont.dispose();
        textures.values().forEach(t -> t.getTexture().dispose());
    }

    public void startNewGame() {
        this.setScreen(new GameScreen(this));
    }

    public void gameMenu() {
        setScreen(new GameMenu(this));
    }

    public void gameOver() {
        setScreen(new GameOver(this));
    }
}
