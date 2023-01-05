package si.feri.slidegame.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import si.feri.slidegame.config.GameConfig;

import java.io.IOException;

public class Map extends Actor {
    private final Geolocation CENTER_GEOLOCATION = new Geolocation(46.557314, 15.637771, "","","","","","","");

    //
    //
    //

    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;

    private Texture[] mapTiles;
    public ZoomXY beginTile;   // top left tile

    //
    //
    //

    public Map() {
        //
        // Download tiles
        //

        try {
            //in most cases, geolocation won't be in the center of the tile because tile borders are predetermined (geolocation can be at the corner of a tile)
            ZoomXY centerTile = MapRasterTiles.getTileNumber(CENTER_GEOLOCATION.lat, CENTER_GEOLOCATION.lng, GameConfig.ZOOM);
            mapTiles = MapRasterTiles.getRasterTileZone(centerTile, GameConfig.NUM_TILES);
            //you need the beginning tile (tile on the top left corner) to convert geolocation to a location in pixels.
            beginTile = new ZoomXY(GameConfig.ZOOM, centerTile.x - ((GameConfig.NUM_TILES - 1) / 2), centerTile.y - ((GameConfig.NUM_TILES - 1) / 2));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //
        // Gen Map
        //

        tiledMap = new TiledMap();
        MapLayers layers = tiledMap.getLayers();

        TiledMapTileLayer layer = new TiledMapTileLayer(GameConfig.NUM_TILES, GameConfig.NUM_TILES, MapRasterTiles.TILE_SIZE, MapRasterTiles.TILE_SIZE);
        int index = 0;
        for (int j = GameConfig.NUM_TILES - 1; j >= 0; j--) {
            for (int i = 0; i < GameConfig.NUM_TILES; i++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(new TextureRegion(mapTiles[index], MapRasterTiles.TILE_SIZE, MapRasterTiles.TILE_SIZE)));
                layer.setCell(i, j, cell);
                index++;
            }
        }
        layers.add(layer);

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        setWidth(GameConfig.NUM_TILES*MapRasterTiles.TILE_SIZE);
        setHeight(GameConfig.NUM_TILES*MapRasterTiles.TILE_SIZE);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        tiledMapRenderer.setView((OrthographicCamera) getStage().getCamera());
        tiledMapRenderer.render();
    }

    @Override
    protected void drawDebugBounds(ShapeRenderer shapes) {
        super.drawDebugBounds(shapes);
        if(!getDebug()) return;

        for(int x = 0; x < GameConfig.NUM_TILES; x++) {
            for(int y = 0; y < GameConfig.NUM_TILES; y++) {
                float px = getX() + MapRasterTiles.TILE_SIZE*x;
                float py = getX() + MapRasterTiles.TILE_SIZE*y;

                shapes.rect(
                        px,
                        py,
                        getOriginX(),
                        getOriginY(),
                        MapRasterTiles.TILE_SIZE,
                        MapRasterTiles.TILE_SIZE,
                        getScaleX(),
                        getScaleY(),
                        getRotation()
                );
            }
        }
    }


    //
    //
    //

    public PixelPosition getPixelPosition(Geolocation loc) {
        return MapRasterTiles.getPixelPosition(
                loc.lat,
                loc.lng,
                MapRasterTiles.TILE_SIZE,
                GameConfig.ZOOM,
                beginTile.x,
                beginTile.y,
                GameConfig.MAP_HEIGHT
        );
    }

    public Geolocation getGeolocation(Vector3 loc) {
        Vector3 pos = getStage().getCamera().unproject(new Vector3(loc.x, loc.y, 0f));
        return MapRasterTiles.getGeolocation(
                (int) pos.x,
                GameConfig.MAP_HEIGHT - (int) pos.y,
                MapRasterTiles.TILE_SIZE,
                GameConfig.ZOOM,
                beginTile.x,
                beginTile.y,
                GameConfig.MAP_HEIGHT
        );
    }
}
