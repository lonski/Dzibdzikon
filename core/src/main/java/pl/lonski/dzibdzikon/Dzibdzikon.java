package pl.lonski.dzibdzikon;

import com.badlogic.gdx.Game;
import java.security.SecureRandom;
import pl.lonski.dzibdzikon.screen.GameMenu;
import pl.lonski.dzibdzikon.screen.GameOver;
import pl.lonski.dzibdzikon.screen.GameScreen;

// TODO:
// - pathfinding coś chyba nie bangla na 100%
// - open doors as action
public class Dzibdzikon extends Game {

    public static boolean SHOW_WHOLE_LEVEL = true;
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;

    public static SecureRandom RANDOM = new SecureRandom();
    private static GameResources gameResources;

    public static GameResources getGameResources() {
        return gameResources;
    }

    @Override
    public void create() {
        gameResources = new GameResources();
        gameMenu();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        gameResources.dispose();
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
