package si.feri.rrizemljevid.screen.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.input.GestureDetector.GestureListener
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import si.feri.rrizemljevid.config.GameConfig

class MapGestureListener(
    var camera: OrthographicCamera, private val touchPosition: Vector3, private val mapScreen: MapScreen
) : GestureListener {
    //
    //
    //
    override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        touchPosition[x, y] = 0f
        return false
    }

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
        println("Click")
        val geo = mapScreen.map!!.getGeolocation(Vector2(x, y))
        if (mapScreen.sideMenue!!.capture) {
            mapScreen.sideMenue!!.capture = false
            mapScreen.sideMenue!!.marker!!.setLocation(geo)
            return false
        }
        return false
    }

    override fun longPress(x: Float, y: Float): Boolean {
        return false
    }

    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
        return false
    }

    override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean {
        camera.translate(-deltaX, deltaY)
        return false
    }

    override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun zoom(initialDistance: Float, distance: Float): Boolean {
        if (initialDistance >= distance) camera.zoom += 0.02.toFloat() else camera.zoom -= 0.02.toFloat()
        return false
    }

    override fun pinch(
        initialPointer1: Vector2, initialPointer2: Vector2, pointer1: Vector2, pointer2: Vector2
    ): Boolean {
        return false
    }

    override fun pinchStop() {}

    //
    //
    //
    fun centerCamera() {
        camera.setToOrtho(false, GameConfig.MAP_WIDTH.toFloat(), GameConfig.MAP_HEIGHT.toFloat())
        camera.position[GameConfig.MAP_WIDTH / 2f, GameConfig.MAP_HEIGHT / 2f] = 0f
        camera.viewportWidth = GameConfig.MAP_WIDTH / 2f
        camera.viewportHeight = GameConfig.MAP_HEIGHT / 2f
        camera.zoom = 2f
        camera.update()
    }

    fun handleInput(delta: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.zoom += 0.02.toFloat()
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom -= 0.02.toFloat()
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-3f, 0f, 0f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(3f, 0f, 0f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0f, -3f, 0f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0f, 3f, 0f)
        }

        // camera.zoom = MathUtils.clamp(camera.zoom, 0.5f, 1f);
        val effectiveViewportWidth = camera.viewportWidth * camera.zoom
        val effectiveViewportHeight = camera.viewportHeight * camera.zoom

        // camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, GameConfig.MAP_WIDTH - effectiveViewportWidth / 2f);
        // camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, GameConfig.MAP_HEIGHT - effectiveViewportHeight / 2f);
    }
}