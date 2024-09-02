package pl.lonski.dzibdzikon.map;

import java.util.ArrayList;
import java.util.List;

public class TileGrid {
    private final int width;
    private final int height;
    private final Glyph[][] tiles;

    private final List<Room> rooms = new ArrayList<>();

    public TileGrid(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Glyph[width][height];
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Glyph[][] getTiles() {
        return tiles;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public Glyph getTile(int x, int y) {
        return tiles[x][y];
    }

    public void setTile(int x, int y, Glyph tile) {
        tiles[x][y] = tile;
    }
}
