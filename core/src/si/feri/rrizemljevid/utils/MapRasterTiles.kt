package si.feri.rrizemljevid.utils

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import si.feri.rrizemljevid.config.Keys
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

object MapRasterTiles {
    //Mapbox
    //https://docs.mapbox.com/api/maps/raster-tiles/
    /*static String mapServiceUrl = "https://api.mapbox.com/v4/";
    static String token = "?access_token=" + Keys.MAPBOX;
    static String tilesetId = "mapbox.satellite";
    static String format = "@2x.jpg90";*/
    //Geoapify
    //https://www.geoapify.com/get-started-with-maps-api
    var mapServiceUrl = "https://maps.geoapify.com/v1/tile/"
    var token = "?&apiKey=" + Keys.GEOAPIFY
    var tilesetId = "dark-matter-yellow-roads"
    var format = "@2x.png"

    //@2x in format means it returns higher DPI version of the image and the image size is 512px (otherwise it is 256px)
    const val TILE_SIZE = 512

    /**
     * Get raster tile based on zoom and tile number.
     *
     * @param zoom
     * @param x
     * @param y
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getRasterTile(zoom: Int, x: Int, y: Int): Texture {
        return getRasterTile("$zoom/$x/$y")
    }

    /**
     * Get raster tile based on zoom and tile number.
     *
     * @param zoomXY
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getRasterTile(zoomXY: ZoomXY): Texture {
        return getRasterTile(zoomXY.toString())
    }

    /**
     * Get raster tile based on zoom and tile number.
     *
     * @param zoomXY string should be in format zoom/x/y
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getRasterTile(zoomXY: String): Texture {
        val url = URL(mapServiceUrl + tilesetId + "/" + zoomXY + format + token)
        val filePath = Paths.get("maps/" + zoomXY + format)
        return try {
            val readBytes = Files.readAllBytes(filePath)
            println("Cache!")
            getTexture(readBytes)
        } catch (e: Exception) {
            if (!Files.exists(filePath.parent)) {
                Files.createDirectories(filePath.parent)
            }
            val data = fetchTile(url).toByteArray()
            Files.write(filePath, data)
            getTexture(data)
        }
    }

    /**
     * Returns tiles for the area of size * size of provided center tile.
     *
     * @param zoomXY center tile
     * @param size
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getRasterTileZone(zoomXY: ZoomXY?, size: Int): Array<Texture?> {
        val array = arrayOfNulls<Texture>(size * size)
        val factorY = IntArray(size * size) //if size is 3 {-1, -1, -1, 0, 0, 0, 1, 1, 1};
        val factorX = IntArray(size * size) //if size is 3 {-1, 0, 1, -1, 0, 1, -1, 0, 1};
        var value = (size - 1) / -2
        for (i in 0 until size) {
            for (j in 0 until size) {
                factorY[i * size + j] = value
                factorX[i + j * size] = value
            }
            value++
        }
        for (i in 0 until size * size) {
            array[i] = getRasterTile(zoomXY!!.zoom, zoomXY.x + factorX[i], zoomXY.y + factorY[i])
            println(zoomXY.zoom.toString() + "/" + (zoomXY.x + factorX[i]) + "/" + (zoomXY.y + factorY[i]))
        }
        return array
    }

    /**
     * Gets tile from provided URL and returns it as ByteArrayOutputStream.
     *
     * @param url
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun fetchTile(url: URL): ByteArrayOutputStream {
        val bis = ByteArrayOutputStream()
        val `is` = url.openStream()
        val bytebuff = ByteArray(4096)
        var n: Int
        while (`is`.read(bytebuff).also { n = it } > 0) {
            bis.write(bytebuff, 0, n)
        }
        return bis
    }

    /**
     * Converts byte[] to Texture.
     *
     * @param array
     * @return
     */
    fun getTexture(array: ByteArray): Texture {
        return Texture(Pixmap(array, 0, array.size))
    }
    //https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Java
    /**
     * It converts to tile number based on the geolocation (latitude and longitude) and zoom
     *
     * @param lat  latitude
     * @param lon  longitude
     * @param zoom
     * @return
     */
    fun getTileNumber(lat: Double, lon: Double, zoom: Int): ZoomXY {
        var xtile = Math.floor((lon + 180) / 360 * (1 shl zoom)).toInt()
        var ytile =
            Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 shl zoom))
                .toInt()
        if (xtile < 0) xtile = 0
        if (xtile >= 1 shl zoom) xtile = (1 shl zoom) - 1
        if (ytile < 0) ytile = 0
        if (ytile >= 1 shl zoom) ytile = (1 shl zoom) - 1
        return ZoomXY(zoom, xtile, ytile)
    }

    //https://www.maptiler.com/google-maps-coordinates-tile-bounds-projection/#15/15.63/46.56
    //https://gis.stackexchange.com/questions/17278/calculate-lat-lon-bounds-for-individual-tile-generated-from-gdal2tiles
    fun tile2long(tileNumberX: Double, zoom: Int): Double {
        return tileNumberX / Math.pow(2.0, zoom.toDouble()) * 360 - 180
    }

    fun tile2lat(tileNumberY: Double, zoom: Int): Double {
        val n = Math.PI - 2 * Math.PI * tileNumberY / Math.pow(2.0, zoom.toDouble())
        return 180 / Math.PI * Math.atan(0.5 * (Math.exp(n) - Math.exp(-n)))
    }

    fun project(lat: Double, lng: Double, tileSize: Int): DoubleArray {
        var siny = Math.sin(lat * Math.PI / 180)

        // Truncating to 0.9999 effectively limits latitude to 89.189. This is
        // about a third of a tile past the edge of the world tile.
        siny = Math.min(Math.max(siny, -0.9999), 0.9999)
        return doubleArrayOf(
            tileSize * (0.5 + lng / 360),
            tileSize * (0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI))
        )
    }

    /**
     * Converts geolocation to pixel position.
     *
     * @param lat        latitude
     * @param lng        longitude
     * @param tileSize
     * @param zoom
     * @param beginTileX x (tile number) of top left tile
     * @param beginTileY y (tile number) of top left tile
     * @param height     viewport height
     * @return
     */
    fun getPixelPosition(
        lat: Double,
        lng: Double,
        tileSize: Int,
        zoom: Int,
        beginTileX: Int,
        beginTileY: Int,
        height: Int
    ): PixelPosition {
        val worldCoordinate = project(lat, lng, tileSize)
        // Scale to fit our image
        val scale = Math.pow(2.0, zoom.toDouble())

        // Apply scale to world coordinates to get image coordinates
        return PixelPosition(
            (Math.floor(worldCoordinate[0] * scale) - beginTileX * tileSize).toInt(),
            height - (Math.floor(worldCoordinate[1] * scale) - beginTileY * tileSize - 1).toInt()
        )
    }

    fun getGeolocation(
        px: Int,
        py: Int,
        tileSize: Int,
        zoom: Int,
        beginTileX: Int,
        beginTileY: Int,
        height: Int
    ): Geolocation {
        return Geolocation(
            tile2lat(beginTileY.toDouble() + py.toDouble() / tileSize.toDouble(), zoom),
            tile2long(beginTileX.toDouble() + px.toDouble() / tileSize.toDouble(), zoom)
        )
    }
}