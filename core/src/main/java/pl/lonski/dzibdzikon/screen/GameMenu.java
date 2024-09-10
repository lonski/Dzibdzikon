package pl.lonski.dzibdzikon.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import pl.lonski.dzibdzikon.CameraUtils;
import pl.lonski.dzibdzikon.Dzibdzikon;
import pl.lonski.dzibdzikon.map.Glyph;

import java.util.List;

import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;

public class GameMenu extends DzibdzikonScreen {

    private float time = 0;
    private float logoBounce = 0;
    private int logoBounceDir = 1;
    private final float logoBounceMax = 5;
    private final List<String> menuEntries = List.of("Rozpocznij grę", "Wyjdź");
    private int menuEntryIdx = 0;

    public GameMenu(Dzibdzikon game) {
        super(game);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // input
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            menuEntryIdx = Math.min(menuEntryIdx + 1, menuEntries.size() - 1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            menuEntryIdx = Math.max(menuEntryIdx - 1, 0);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (menuEntryIdx == 1) {
                Gdx.app.exit();
            } else if (menuEntryIdx == 0) {
                game.startNewGame();
            }
        }

        // update
        game.batch.setProjectionMatrix(game.camera.combined);
        time += delta;

        var pos = CameraUtils.getTopLeftCorner(game.camera);
        var marginLeft = game.camera.viewportWidth / 6;
        var marginTop = game.camera.viewportHeight / 4;

        var titlePos = new Vector2(pos.x + marginLeft + TILE_WIDTH * 1.2f, pos.y - marginTop + 24);
        var msgPos = new Vector2(titlePos.x, titlePos.y - TILE_WIDTH * 2);
        var msgYLineDiff = TILE_HEIGHT;

        var logoPos = new Vector2(msgPos.x - TILE_WIDTH * 1.2f, msgPos.y + logoBounce - 20 - msgYLineDiff * menuEntryIdx);

        if (time >= 0.01f) {
            time = 0;
            logoBounce = logoBounce + (0.2f * logoBounceDir);
            if (logoBounce >= logoBounceMax) {
                logoBounceDir = -1;
            }
            if (logoBounce <= 0) {
                logoBounceDir = 1;
            }
        }

        // render
        ScreenUtils.clear(0, 0, 0, 0);
        game.batch.begin();
        game.batch.draw(game.textures.get(Glyph.PLAYER), logoPos.x, logoPos.y);
        game.bigFont.setColor(Color.ORANGE);
        game.bigFont.draw(game.batch, "Dzibdzikon", titlePos.x, titlePos.y);
        game.fontItalic.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < menuEntries.size(); i++) {
            game.fontItalic.draw(game.batch, menuEntries.get(i), msgPos.x, msgPos.y - msgYLineDiff * i);
        }
        game.batch.end();
    }
}
