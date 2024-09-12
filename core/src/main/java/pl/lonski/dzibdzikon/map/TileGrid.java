package pl.lonski.dzibdzikon.map;

import java.util.ArrayList;
import java.util.List;
import pl.lonski.dzibdzikon.Point;

public class TileGrid {
    private final int width;
    private final int height;
    private final TextureId[][] tiles;

    private final List<Room> rooms = new ArrayList<>();

    public TileGrid(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new TextureId[width][height];
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

    public TextureId[][] getTiles() {
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

    public TextureId getTile(int x, int y) {
        return tiles[x][y];
    }

    public TextureId getTile(Point pos) {
        return getTile(pos.x(), pos.y());
    }

    public void setTile(Point pos, TextureId tile) {
        setTile(pos.x(), pos.y(), tile);
    }

    public void setTile(int x, int y, TextureId tile) {
        tiles[x][y] = tile;
    }

    public boolean isBorderTile(Point pos) {
        return pos.x() == 0 || pos.y() == 0 || pos.x() == width - 1 || pos.y() == height - 1;
    }
}
