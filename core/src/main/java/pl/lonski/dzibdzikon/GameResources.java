package pl.lonski.dzibdzikon;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.HashMap;
import java.util.Map;
import pl.lonski.dzibdzikon.map.TextureId;
import pl.lonski.dzibdzikon.screen.WindowManager;

public class GameResources {

    public OrthographicCamera camera;
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public BitmapFont fontItalic12;
    public BitmapFont fontItalic15;
    public BitmapFont fontItalic20;
    public BitmapFont fontItalicBold15;
    public BitmapFont bigFont;
    public final Map<TextureId, TextureRegion> textures = new HashMap<>();
    public final WindowManager windowManager = new WindowManager();

    public GameResources() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        fontItalic12 = FontUtils.createFont("font/DejaVuSerif-Italic.ttf", 12);
        fontItalic15 = FontUtils.createFont("font/DejaVuSerif-Italic.ttf", 15);
        fontItalic20 = FontUtils.createFont("font/DejaVuSerif-Italic.ttf", 20);
        fontItalicBold15 = FontUtils.createFont("font/DejaVuSerif-BoldItalic.ttf", 15);
        bigFont = FontUtils.createFont("font/DejaVuSerif-Italic.ttf", 32);

        for (TextureId glyph : TextureId.values()) {
            textures.put(glyph, new TextureRegion(new Texture(glyph.getFilename())));
        }

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
    }

    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        fontItalic15.dispose();
        fontItalicBold15.dispose();
        bigFont.dispose();
        textures.values().forEach(t -> t.getTexture().dispose());
    }
}
