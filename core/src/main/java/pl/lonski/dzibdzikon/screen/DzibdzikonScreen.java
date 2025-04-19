package pl.lonski.dzibdzikon.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import pl.lonski.dzibdzikon.Dzibdzikon;

import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

public class DzibdzikonScreen implements Screen {

    protected Dzibdzikon dzibdzikon;

    public DzibdzikonScreen(Dzibdzikon dzibdzikon) {
        this.dzibdzikon = dzibdzikon;
    }

    @Override
    public void show() {}

    @Override
    public void render(float v) {}

    @Override
    public void resize(int width, int height) {
//        float aspectRatio = (float) width / (float) height;
//        getGameResources().camera.setToOrtho(false, Gdx.graphics.getWidth() * aspectRatio, Gdx.graphics.getHeight());
//        getGameResources().camera.viewportWidth = Gdx.graphics.getWidth();
//        getGameResources().camera.viewportHeight = Gdx.graphics.getHeight();
//        getGameResources().camera.update();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
}
