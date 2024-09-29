package pl.lonski.dzibdzikon.map;

public enum TextureId {
    PLAYER("dzibdzik.png"),

    HIGHLIGHT_YELLOW("yellow-highlight.png"),
    TARGET("target.png"),
    ICON_BACKGROUND("icon_bg.png"),
    ARROW_UP("arrow_up.png"),
    ARROW_DOWN("arrow_down.png"),

    WALL("terrian/wall_1.png"),
    FLOOR("terrian/floor_1.png"),
    WALL_GREEN("terrian/wall_green.png"),
    FLOOR_GREEN("terrian/floor_green.png"),

    DOOR_OPEN("door_open.png"),
    DOOR_CLOSED("door_closed.png"),
    DOWNSTAIRS("downstairs.png"),
    PLANK("plank.png"),

    WND_SPELLBOOK("window/spellbook.png"),

    MOB_ZOMBIE("mob/zombie.png"),
    MOB_GLAZOLUD("mob/glazolud.png"),
    MOB_BIG_ROCK("mob/glazolud_glaz.png"),
    MOB_PTAKODRZEWO("mob/ptakodrzewo.png"),
    MOB_BIRD_PLANKER("mob/bird_planker.png"),
    MOB_BIRD_THROWER("mob/bird_thrower.png"),
    MOB_BIRD_BITER("mob/bird_biter.png"),

    SPELL_SPIKE("spell/kolec.png"),
    SPELL_EFFECT_SPIKE("spell/kolec_effect.png"),
    SPELL_FIREBALL("spell/fireball.png"),
    SPELL_EFFECT_FIREBALL("spell/fireball_effect.png"),
    SPELL_EFFECT_BURN("spell/burn.png"),

    POTION_RED("potion/red.png"),
    POTION_LIGHT_GREEN("potion/light_green.png");

    private String filename;

    TextureId(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isWall() {
        return this == WALL || this == WALL_GREEN;
    }

    public boolean isFloor() {
        return this == FLOOR || this == FLOOR_GREEN;
    }
}
