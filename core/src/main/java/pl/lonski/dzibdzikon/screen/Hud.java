package pl.lonski.dzibdzikon.screen;

import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;
import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import pl.lonski.dzibdzikon.CameraUtils;
import pl.lonski.dzibdzikon.FontUtils;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.command.CastSpellCommand;
import pl.lonski.dzibdzikon.entity.FeatureType;
import pl.lonski.dzibdzikon.entity.Quickbar;
import pl.lonski.dzibdzikon.entity.features.Attackable;
import pl.lonski.dzibdzikon.entity.features.MagicUser;
import pl.lonski.dzibdzikon.map.TextureId;

public class Hud extends Stage {

    private static final int MAX_MESSAGES = 5;
    private static final List<Message> messages = new ArrayList<>();
    private static String actionMessage = "";
    private static final List<Point> targets = new ArrayList<>();
    public static final List<Point> debugHighlight = new ArrayList<>();
    private final List<Quickbar.SlotIcon> quickBarIcons = new ArrayList<>();

    private final ProgressBar mpBar;
    private final ProgressBar hpBar;
    private final Skin skin;

    private final World world;

    public Hud(World world) {
        super(new ScalingViewport( Scaling.fill, 800, 480));
        this.world = world;
        skin = new Skin(Gdx.files.internal("ui/flat/skin.json"));

        var mpBarStyle = new ProgressBar.ProgressBarStyle();
        mpBarStyle.background = skin.getDrawable("progressHorizontal");
        mpBarStyle.knob = skin.getDrawable("progressHorizontalKnobMP");
        mpBarStyle.knobBefore = skin.getDrawable("progressHorizontalKnobMP");

        mpBar = new ProgressBar(0, 1, 1, false, mpBarStyle);
        addActor(mpBar);

        var hpBarStyle = new ProgressBar.ProgressBarStyle();
        hpBarStyle.background = skin.getDrawable("progressHorizontal");
        hpBarStyle.knob = skin.getDrawable("progressHorizontalKnobHP");
        hpBarStyle.knobBefore = skin.getDrawable("progressHorizontalKnobHP");

        hpBar = new ProgressBar(0, 1, 1, false, hpBarStyle);
        hpBar.setPosition(mpBar.getX(), mpBar.getY() + mpBar.getHeight());
        addActor(hpBar);

        var btn = new ImageButton(skin);
        btn.setPosition(10, hpBar.getY() + hpBar.getHeight() * 2);
        btn.getStyle().imageUp = new TextureRegionDrawable(getGameResources().textures.get(TextureId.SPELLBOOK));
        btn.getStyle().imageDown = new TextureRegionDrawable(getGameResources().textures.get(TextureId.SPELLBOOK));
        addActor(btn);
        btn.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                new CastSpellCommand().execute(world.getPlayer(), world);
                return true;
            }
        });

    }

    public static void addMessage(String message) {
        addMessage(message, Color.WHITE);
    }

    public static void addMessage(String message, Color color) {
        if (messages.size() > MAX_MESSAGES) {
            messages.remove(0);
        }

        messages.add(new Message(message, color, 5));
    }

    public static void setActionMessage(String message) {
        actionMessage = message;
    }

    public static void setTargets(List<Point> newTargets) {
        targets.clear();
        targets.addAll(newTargets);
    }

    public void update(float delta, World world) {
        // update messages
        messages.removeIf(message -> message.ttl <= 0);

        // update progress bars
        Attackable playerAttackable = world.getPlayer().getFeature(FeatureType.ATTACKABLE);
        hpBar.setRange(0, playerAttackable.getMaxHp());
        hpBar.setValue(playerAttackable.getHp());
        MagicUser playerMagicUser = world.getPlayer().getFeature(FeatureType.MAGIC_USER);
        mpBar.setRange(0, playerMagicUser.getManaMax());
        mpBar.setValue(playerMagicUser.getMana());

        // update quickbar
        quickBarIcons.clear();
        quickBarIcons.addAll(world.getPlayer().getQuickbar().getSlotIcons());
        quickBarIcons.sort(Comparator.comparingInt(Quickbar.SlotIcon::getNum));

        // update stage
        act(delta);
    }

    public void render(float delta) {
        // render messages
        var batch = getGameResources().batch;
        var camera = getCamera();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        var messagePos = CameraUtils.getTopLeftCorner(camera);
        for (int i = 0; i < messages.size(); i++) {
            var message = messages.get(i);
            message.ttl -= delta;
            getGameResources().fontItalic15.setColor(message.color);
            getGameResources().fontItalic15.draw(batch, message.text, messagePos.x + 10, messagePos.y - 10 - (17 * i));
        }

        if (!actionMessage.isEmpty()) {
            var bottomLeft = CameraUtils.getBottomCenter(camera);
            var textWidth = FontUtils.getTextWidth(getGameResources().fontItalic15, actionMessage);
            var actionMessagePos = new Vector2(bottomLeft.x - textWidth / 2, bottomLeft.y + (actionMessage.contains("\n") ? 50 : 25));
            getGameResources().fontItalic15.setColor(Color.GOLD);
            getGameResources().fontItalic15.draw(batch, actionMessage, actionMessagePos.x, actionMessagePos.y);
        }

        // renader quickbar
        getGameResources().fontItalic12.setColor(Color.WHITE);
        var highlightTexture = getGameResources().textures.get(TextureId.HIGHLIGHT_YELLOW);
        for (int i = 0; i < quickBarIcons.size(); i++) {
            var slotIcon = quickBarIcons.get(i);
            var icon = slotIcon.getIcon();
            var bottomRight = CameraUtils.getBottomRightCorner(camera);
            var posX = bottomRight.x - TILE_WIDTH - 8;
            var posY = bottomRight.y + 8 + i * 40;
            var texture = getGameResources().textures.get(icon);
            batch.draw(texture, posX, posY);
            getGameResources().fontItalic12.draw(batch, String.valueOf(slotIcon.getNum()), posX + 4, posY + 16);

            if (slotIcon.isHighlight()) {
                batch.draw(highlightTexture, posX, posY);
            }
        }

        batch.end();

        batch.setProjectionMatrix(getGameResources().camera.combined);
        batch.begin();
        // debug render
        if (!debugHighlight.isEmpty()) {
            var texture = getGameResources().textures.get(TextureId.HIGHLIGHT_YELLOW);
            float originX = texture.getRegionWidth() / 2f;
            float originY = texture.getRegionHeight() / 2f;
            for (Point point : debugHighlight) {
                batch.draw(texture, point.x() * TILE_WIDTH - originX, point.y() * TILE_HEIGHT - originY);
            }
        }

        // render targeting
        if (!targets.isEmpty()) {
            var texture = getGameResources().textures.get(TextureId.TARGET);
            float originX = texture.getRegionWidth() / 2f;
            float originY = texture.getRegionHeight() / 2f;
            for (Point point : targets) {
                batch.draw(texture, point.x() * TILE_WIDTH - originX, point.y() * TILE_HEIGHT - originY);
            }
        }
        batch.end();

        // draw stage
        draw();
    }

    private static class Message {

        String text;
        Color color;
        float ttl;

        public Message(String text, Color color, float ttl) {
            this.text = text;
            this.color = color;
            this.ttl = ttl;
        }
    }
}
