package si.feri.slidegame.config;

import si.feri.slidegame.utils.MapRasterTiles;

public class GameConfig {



    //
    //
    //

    public static final int ZOOM = 15;
    public static final int NUM_TILES = 3;
    public static final int MAP_WIDTH = MapRasterTiles.TILE_SIZE * NUM_TILES;    // world units
    public static final int MAP_HEIGHT = MapRasterTiles.TILE_SIZE * NUM_TILES;   // world units

    public static final float MARKER_HEIGHT = 40f;
    public static final float MARKER_WIDTH = 40f;

    //
    //
    //

    public static final float ASPECT_RATIO =  MAP_HEIGHT / (float) MAP_WIDTH;
    public static final float HUD_WIDTH = 800f; // pixels
    public static final float HUD_HEIGHT = HUD_WIDTH * ASPECT_RATIO;    // pixels

    //
    //
    //

    private GameConfig() {
    }
}
