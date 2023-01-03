package si.feri.slidegame.screen.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import si.feri.slidegame.common.Database;
import si.feri.slidegame.config.GameConfig;
import si.feri.slidegame.utils.Geolocation;

import javax.xml.crypto.Data;
import java.io.IOException;

public class MapGestureListener implements GestureDetector.GestureListener {
    OrthographicCamera camera;
    private Vector3 touchPosition;
    private MapScreen mapScreen;

    //
    //
    //

    public MapGestureListener(OrthographicCamera camera, Vector3 touchPosition, MapScreen mapScreen) {
        this.camera = camera;
        this.touchPosition = touchPosition;
        this.mapScreen = mapScreen;
    }

    //
    //
    //

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        touchPosition.set(x, y, 0);
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        Geolocation geo = mapScreen.map.getGeolocation(new Vector3(x, y, 0));
        Database.Event event = new Database.Event(geo);
        try {
            Database.addEvent(event);
            mapScreen.locations.add(geo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Click");
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        camera.translate(-deltaX, deltaY);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        if (initialDistance >= distance)
            camera.zoom += 0.02;
        else
            camera.zoom -= 0.02;
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {}

    //
    //
    //

    public void centerCamera() {
        camera.setToOrtho(false, GameConfig.MAP_WIDTH, GameConfig.MAP_HEIGHT);
        camera.position.set(GameConfig.MAP_WIDTH / 2f, GameConfig.MAP_HEIGHT / 2f, 0);
        camera.viewportWidth = GameConfig.MAP_WIDTH / 2f;
        camera.viewportHeight = GameConfig.MAP_HEIGHT / 2f;
        camera.zoom = 2f;
        camera.update();
    }

    public void handleInput(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.zoom += 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom -= 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, -3, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, 3, 0);
        }

        // camera.zoom = MathUtils.clamp(camera.zoom, 0.5f, 1f);

        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        // camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, GameConfig.MAP_WIDTH - effectiveViewportWidth / 2f);
        // camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, GameConfig.MAP_HEIGHT - effectiveViewportHeight / 2f);
    }
}
