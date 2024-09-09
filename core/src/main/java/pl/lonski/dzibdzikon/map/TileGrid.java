package pl.lonski.dzibdzikon.map;

import java.util.ArrayList;
import java.util.List;
import pl.lonski.dzibdzikon.Point;

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

    public boolean inBounds(Point pos) {
        return inBounds(pos.x(), pos.y());
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

    public Room getRandomRoom() {
        return rooms.get((int) (Math.random() * rooms.size()));
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public Glyph getTile(int x, int y) {
        return tiles[x][y];
    }

    public Glyph getTile(Point pos) {
        return getTile(pos.x(), pos.y());
    }

    public void setTile(Point pos, Glyph tile) {
        setTile(pos.x(), pos.y(), tile);
    }

    public void setTile(int x, int y, Glyph tile) {
        tiles[x][y] = tile;
    }

    public boolean isBorderTile(Point pos) {
        return pos.x() == 0 || pos.y() == 0 || pos.x() == width - 1 || pos.y() == height - 1;
    }
}
