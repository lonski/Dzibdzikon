package pl.lonski.dzibdzikon.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import pl.lonski.dzibdzikon.Dzibdzikon;

public class DzibdzikonScreen implements Screen {

    protected Dzibdzikon game;

    public DzibdzikonScreen(Dzibdzikon game) {
        this.game = game;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {

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

    }
}
