package si.feri.slidegame.config;

import si.feri.slidegame.utils.MapRasterTiles;

public class GameConfig {
    public static final float WIDTH = 800f; // pixels
    public static final float HEIGHT = 600f;    // pixels

    public static final float HUD_WIDTH = 800f; // pixels
    public static final float HUD_HEIGHT = 600f;    // pixels

    //
    //
    //

    public static final int ZOOM = 15;
    public static final int NUM_TILES = 3;
    public static final int MAP_WIDTH = MapRasterTiles.TILE_SIZE * NUM_TILES;    // world units
    public static final int MAP_HEIGHT = MapRasterTiles.TILE_SIZE * NUM_TILES;   // world units

    //
    //
    //

    private GameConfig() {
    }
}
