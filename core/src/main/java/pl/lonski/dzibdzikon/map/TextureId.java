package pl.lonski.dzibdzikon.map;

public enum TextureId {
    WALL("wall_1.png"),
    FLOOR("floor_1.png"),
    PLAYER("dzibdzik.png"),
    ZOMBIE("zombie.png"),
    DOOR_OPEN("door_open.png"),
    DOOR_CLOSED("door_closed.png"),
    DOWNSTAIRS("downstairs.png"),
    GLAZOLUD("glazolud.png"),
    BIG_ROCK("glazolud_glaz.png"),
    WND_SPELLBOOK("window/spellbook.png"),
    SPELL_SPIKE("spell/kolec.png"),
    HIGHLIGHT_YELLOW("yellow-highlight.png"),
    TARGET("target.png");

    private String filename;

    TextureId(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isWall() {
        return this == WALL;
    }

    public boolean isFloor() {
        return this == FLOOR;
    }
}
