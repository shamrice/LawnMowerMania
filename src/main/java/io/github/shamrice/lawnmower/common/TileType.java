package io.github.shamrice.lawnmower.common;

public enum TileType {
    UNCUT_GRASS(1),
    CUT_GRASS(2),
    OVER_MOWED_GRASS(3),
    DEAD_GRASS(4),
    BUSH(5),
    FLOWERS(6),
    BRICK_WALL(7),
    MOWER(8); //not sure why that's in the tile set...

    private final int id;

    private TileType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
