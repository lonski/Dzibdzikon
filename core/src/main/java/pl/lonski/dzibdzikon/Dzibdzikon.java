package pl.lonski.dzibdzikon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.security.SecureRandom;
import pl.lonski.dzibdzikon.screen.GameScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Dzibdzikon extends Game {

    public static boolean SHOW_WHOLE_LEVEL = false;
    public static final int TILE_WIDTH = 29;
    public static final int TILE_HEIGHT = 29;

    public static SecureRandom RANDOM = new SecureRandom();

    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();

        this.setScreen(new GameScreen(this));


    }

    @Override
    public void render() {
        super.render();

    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
