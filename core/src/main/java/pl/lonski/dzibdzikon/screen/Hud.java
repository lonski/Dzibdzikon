package pl.lonski.dzibdzikon.screen;

import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_HEIGHT;
import static pl.lonski.dzibdzikon.Dzibdzikon.TILE_WIDTH;
import static pl.lonski.dzibdzikon.Dzibdzikon.getGameResources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import pl.lonski.dzibdzikon.CameraUtils;
import pl.lonski.dzibdzikon.DzibdziInput;
import pl.lonski.dzibdzikon.FontUtils;
import pl.lonski.dzibdzikon.Point;
import pl.lonski.dzibdzikon.World;
import pl.lonski.dzibdzikon.command.CastSpellCommand;
import pl.lonski.dzibdzikon.command.OpenInventoryCommand;
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
    private static boolean targetingButtonsVisible = false;

    private final ProgressBar mpBar;
    private final ProgressBar hpBar;
    private final Skin skin;
    private final World world;
    private final List<ImageButton> quickbarButtons = new ArrayList<>();
    private Table targetingOverlay;

    public Hud(World world) {
        super(new ScalingViewport(Scaling.stretch, 800, 480));
        this.world = world;
        skin = new Skin(Gdx.files.internal("ui/flat/skin.json"));

        var hpBarStyle = new ProgressBar.ProgressBarStyle();
        hpBarStyle.background = skin.getDrawable("progressHorizontal");
        hpBarStyle.knob = skin.getDrawable("progressHorizontalKnobHP");
        hpBarStyle.knobBefore = skin.getDrawable("progressHorizontalKnobHP");
        hpBar = new ProgressBar(0, 1, 1, false, hpBarStyle);
        hpBar.setSize(220, 18);
        hpBar.setPosition(5, 458);
        addActor(hpBar);

        var mpBarStyle = new ProgressBar.ProgressBarStyle();
        mpBarStyle.background = skin.getDrawable("progressHorizontal");
        mpBarStyle.knob = skin.getDrawable("progressHorizontalKnobMP");
        mpBarStyle.knobBefore = skin.getDrawable("progressHorizontalKnobMP");
        mpBar = new ProgressBar(0, 1, 1, false, mpBarStyle);
        mpBar.setSize(220, 18);
        mpBar.setPosition(5, 437);
        addActor(mpBar);

        setupBottomBar();
        setupTargetingOverlay();
    }

    private void setupBottomBar() {
        Table bottomBar = new Table();
        bottomBar.setWidth(800);
        bottomBar.setHeight(44);
        bottomBar.setPosition(0, 0);
        bottomBar.left();

        for (int i = 0; i < 5; i++) {
            final Quickbar.SlotType slotType = Quickbar.SlotType.values()[i];
            var slotStyle = new ImageButton.ImageButtonStyle(skin.get(ImageButton.ImageButtonStyle.class));
            slotStyle.up = null;
            slotStyle.down = null;
            slotStyle.over = null;
            slotStyle.imageUp = new TextureRegionDrawable(getGameResources().textures.get(TextureId.ICON_BACKGROUND));
            slotStyle.imageDown = slotStyle.imageUp;
            var btn = new ImageButton(slotStyle);
            btn.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (targetingButtonsVisible) {
                        DzibdziInput.broadcast(
                                new DzibdziInput.DzibdziKey(Input.Keys.ENTER, false, null, null, false));
                    } else {
                        world.getPlayer().getQuickbar().useSlot(slotType)
                                .ifPresent(world.getPlayer()::takeAction);
                    }
                    return true;
                }
            });
            btn.addListener(new ActorGestureListener() {
                @Override
                public boolean longPress(Actor actor, float x, float y) {
                    world.getPlayer().getQuickbar().clearSlot(slotType);
                    return true;
                }
            });
            quickbarButtons.add(btn);
            bottomBar.add(btn).size(44, 40).pad(2);
        }

        bottomBar.add().expandX();

        var spellStyle = new ImageButton.ImageButtonStyle(skin.get(ImageButton.ImageButtonStyle.class));
        spellStyle.up = null;
        spellStyle.down = null;
        spellStyle.over = null;
        spellStyle.imageUp = new TextureRegionDrawable(getGameResources().textures.get(TextureId.SPELLBOOK));
        spellStyle.imageDown = spellStyle.imageUp;
        var spellBtn = new ImageButton(spellStyle);
        spellBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                new CastSpellCommand().execute(world.getPlayer(), world);
                return true;
            }
        });
        bottomBar.add(spellBtn).size(44, 40).pad(2);

        var invStyle = new ImageButton.ImageButtonStyle(skin.get(ImageButton.ImageButtonStyle.class));
        invStyle.up = null;
        invStyle.down = null;
        invStyle.over = null;
        invStyle.imageUp = new TextureRegionDrawable(getGameResources().textures.get(TextureId.BACKPACK));
        invStyle.imageDown = invStyle.imageUp;
        var invBtn = new ImageButton(invStyle);
        invBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                new OpenInventoryCommand().execute(world.getPlayer(), world);
                return true;
            }
        });
        bottomBar.add(invBtn).size(44, 40).pad(2);

        addActor(bottomBar);
    }

    private void setupTargetingOverlay() {
        targetingOverlay = new Table();
        targetingOverlay.setFillParent(true);
        targetingOverlay.bottom().center();
        targetingOverlay.padBottom(48);

        var cancelBtn = new TextButton("Cancel", skin);
        cancelBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                DzibdziInput.broadcast(
                        new DzibdziInput.DzibdziKey(Input.Keys.ESCAPE, false, null, null, false));
                return true;
            }
        });

        targetingOverlay.add(cancelBtn).size(80, 35);
        targetingOverlay.setVisible(false);
        addActor(targetingOverlay);
    }

    public static void showTargetingButtons(boolean visible) {
        targetingButtonsVisible = visible;
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
        messages.removeIf(message -> message.ttl <= 0);

        Attackable playerAttackable = world.getPlayer().getFeature(FeatureType.ATTACKABLE);
        hpBar.setRange(0, playerAttackable.getMaxHp());
        hpBar.setValue(playerAttackable.getHp());
        MagicUser playerMagicUser = world.getPlayer().getFeature(FeatureType.MAGIC_USER);
        mpBar.setRange(0, playerMagicUser.getManaMax());
        mpBar.setValue(playerMagicUser.getMana());

        var slotIcons = world.getPlayer().getQuickbar().getSlotIcons();
        var slotMap = slotIcons.stream()
                .collect(Collectors.toMap(Quickbar.SlotIcon::getNum, s -> s));
        for (int i = 0; i < quickbarButtons.size(); i++) {
            var btn = quickbarButtons.get(i);
            var slotIcon = slotMap.get(i + 1);
            var texId = slotIcon != null ? slotIcon.getIcon() : TextureId.ICON_BACKGROUND;
            var drawable = new TextureRegionDrawable(getGameResources().textures.get(texId));
            btn.getStyle().imageUp = drawable;
            btn.getStyle().imageDown = drawable;
            btn.getStyle().imageChecked = slotIcon != null && slotIcon.isHighlight() ? drawable : null;
        }

        targetingOverlay.setVisible(targetingButtonsVisible);

        act(delta);
    }

    public void render(float delta) {
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
            var actionMessagePos = new Vector2(
                    bottomLeft.x - textWidth / 2,
                    bottomLeft.y + (actionMessage.contains("\n") ? 50 : 25));
            getGameResources().fontItalic15.setColor(Color.GOLD);
            getGameResources().fontItalic15.draw(batch, actionMessage, actionMessagePos.x, actionMessagePos.y);
        }

        batch.end();

        batch.setProjectionMatrix(getGameResources().camera.combined);
        batch.begin();
        if (!debugHighlight.isEmpty()) {
            var texture = getGameResources().textures.get(TextureId.HIGHLIGHT_YELLOW);
            float originX = texture.getRegionWidth() / 2f;
            float originY = texture.getRegionHeight() / 2f;
            for (Point point : debugHighlight) {
                batch.draw(texture, point.x() * TILE_WIDTH - originX, point.y() * TILE_HEIGHT - originY);
            }
        }

        if (!targets.isEmpty()) {
            var texture = getGameResources().textures.get(TextureId.TARGET);
            float originX = texture.getRegionWidth() / 2f;
            float originY = texture.getRegionHeight() / 2f;
            for (Point point : targets) {
                batch.draw(texture, point.x() * TILE_WIDTH - originX, point.y() * TILE_HEIGHT - originY);
            }
        }
        batch.end();

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
