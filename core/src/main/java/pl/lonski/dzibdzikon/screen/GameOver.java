package pl.lonski.dzibdzikon.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import pl.lonski.dzibdzikon.CameraUtils;
import pl.lonski.dzibdzikon.Dzibdzikon;

import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;

public class GameOver extends DzibdzikonScreen {
    public GameOver(Dzibdzikon game) {
        super(game);
    }

    @Override
    public void render(float v) {
        // input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.gameMenu();
        }

        // update
        game.batch.setProjectionMatrix(game.camera.combined);

        var pos = CameraUtils.getTopLeftCorner(game.camera);
        var marginLeft = game.camera.viewportWidth / 6;
        var marginTop = game.camera.viewportHeight / 4;

        var titlePos = new Vector2(pos.x + marginLeft + TILE_WIDTH * 1.2f, pos.y - marginTop + 24);
        var msgPos = new Vector2(titlePos.x, titlePos.y - TILE_WIDTH * 2);

        // render
        ScreenUtils.clear(0, 0, 0, 0);
        game.batch.begin();
        game.bigFont.setColor(Color.SCARLET);
        game.bigFont.draw(game.batch, "Zostałeś zabity!", titlePos.x, titlePos.y);
        game.fontItalic.setColor(Color.LIGHT_GRAY);
        game.fontItalic.draw(game.batch, "Naciśnij <enter> aby wrócić do menu", msgPos.x, msgPos.y);
        game.batch.end();
    }
}
