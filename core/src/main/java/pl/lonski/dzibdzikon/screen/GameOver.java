package pl.lonski.dzibdzikon.screen;

import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;
import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import pl.lonski.dzibdzikon.CameraUtils;
import pl.lonski.dzibdzikon.Dzibdzikon;

public class GameOver extends DzibdzikonScreen {

    public GameOver(Dzibdzikon dzibdzikon) {
        super(dzibdzikon);
    }

    @Override
    public void render(float v) {
        // input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            dzibdzikon.gameMenu();
        }

        // update
        var batch = getGameResources().batch;
        var camera = getGameResources().camera;

        batch.setProjectionMatrix(camera.combined);

        var pos = CameraUtils.getTopLeftCorner(camera);
        var marginLeft = camera.viewportWidth / 6;
        var marginTop = camera.viewportHeight / 4;

        var titlePos = new Vector2(pos.x + marginLeft + TILE_WIDTH * 1.2f, pos.y - marginTop + 24);
        var msgPos = new Vector2(titlePos.x, titlePos.y - TILE_WIDTH * 2);

        // render
        ScreenUtils.clear(0, 0, 0, 0);
        batch.begin();
        getGameResources().bigFont.setColor(Color.SCARLET);
        getGameResources().bigFont.draw(batch, "Zostałeś zabity!", titlePos.x, titlePos.y);
        getGameResources().fontItalic15.setColor(Color.LIGHT_GRAY);
        getGameResources().fontItalic15.draw(batch, "Naciśnij <enter> aby wrócić do menu", msgPos.x, msgPos.y);
        batch.end();
    }
}
