package pl.lonski.dzibdzikon.map;

import pl.lonski.dzibdzikon.Point;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TileGrid {
    private final int width;
    private final int height;
    private final TextureId[][] tiles;

    private final Set<Room> rooms = new HashSet<>();

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

    public boolean inBoundsWithFrame(Point pos) {
        int x = pos.x();
        int y = pos.y();

        return x > 0 && (x + 1) < width && y > 0 && (y + 1) < height;
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
        return new ArrayList<>(rooms);
    }

    public Room getRandomRoom() {
        return getRooms().get((int) (Math.random() * rooms.size()));
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

    public boolean horizontalNeighboursAreWalls(Point p) {
        var nbPos1 = new Point(p.x() - 1, p.y());
        var nbPos2 = new Point(p.x() + 1, p.y());
        return (inBounds(nbPos1)
                && getTile(nbPos1).isWall()
                && inBounds(nbPos2)
                && getTile(nbPos2).isWall());
    }

    public boolean horizontalNeighboursAreFloors(Point p) {
        var nbPos1 = new Point(p.x() - 1, p.y());
        var nbPos2 = new Point(p.x() + 1, p.y());
        return (inBounds(nbPos1)
                && getTile(nbPos1).isFloor()
                && inBounds(nbPos2)
                && getTile(nbPos2).isFloor());
    }

    public boolean verticalNeighboursAreWalls(Point p) {
        var nbPos1 = new Point(p.x(), p.y() - 1);
        var nbPos2 = new Point(p.x(), p.y() + 1);
        return (inBounds(nbPos1)
                && getTile(nbPos1).isWall()
                && inBounds(nbPos2)
                && getTile(nbPos2).isWall());
    }

    public boolean verticalNeighboursAreFloors(Point p) {
        var nbPos1 = new Point(p.x(), p.y() - 1);
        var nbPos2 = new Point(p.x(), p.y() + 1);
        return (inBounds(nbPos1)
                && getTile(nbPos1).isFloor()
                && inBounds(nbPos2)
                && getTile(nbPos2).isFloor());
    }
}
