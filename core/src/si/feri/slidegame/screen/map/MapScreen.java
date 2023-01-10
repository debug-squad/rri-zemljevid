package si.feri.slidegame.screen.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import si.feri.slidegame.MyGdxGame;
import si.feri.slidegame.assets.AssetDescriptors;
import si.feri.slidegame.assets.RegionNames;
import si.feri.slidegame.config.GameConfig;
import si.feri.slidegame.utils.Geolocation;
import si.feri.slidegame.utils.Map;

public class MapScreen extends ScreenAdapter {

    public final MyGdxGame game;
    public final AssetManager assetManager;

    public Viewport viewport;
    public Viewport hudViewport;

    public Stage stage;
    public Stage hudStage;

    public MapGestureListener mapGestureListener;
    public Boolean deploy;
    //
    //
    //

    public ShapeRenderer shapeRenderer;
    public Vector3 touchPosition;

    public SideMenue sideMenue;

    public Map map;
    public Markers markers;

    //
    //
    //

    private SpriteBatch batch;

    private Skin uskin;
    private TextureAtlas gameplayAtlas;
    Label pos;

    //
    //
    //

    public MapScreen(MyGdxGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.MAP_WIDTH, GameConfig.MAP_HEIGHT);
        hudViewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);

        stage = new Stage(viewport, game.getBatch());
        hudStage = new Stage(hudViewport, game.getBatch());

        stage.setDebugAll(true);
        hudStage.setDebugAll(true);

        batch = new SpriteBatch();

        //
        //
        //

        map = new Map();
        stage.addActor(map);

        uskin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);
        pos = new Label("A, B, C", uskin);
        pos.setWidth(GameConfig.HUD_WIDTH - 10f);
        pos.setHeight(pos.getHeight() * 2f);
        pos.setPosition(5, 5);
        hudStage.addActor(pos);

        //
        //
        //

        shapeRenderer = new ShapeRenderer();
        touchPosition = new Vector3();

        deploy = true;
        mapGestureListener = new MapGestureListener((OrthographicCamera) stage.getCamera(), touchPosition, this);
        Gdx.input.setInputProcessor(new InputMultiplexer(
                stage,
                new GestureDetector(mapGestureListener),
                hudStage
        ));

        //
        sideMenue = new SideMenue(uskin, this);
        hudStage.addActor(sideMenue);

        //
        markers = new Markers(this, gameplayAtlas.findRegion(RegionNames.MARKER));
        stage.addActor(markers);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.GRAY);

        //
        //
        //

        mapGestureListener.handleInput(delta);

        stage.act(delta);
        hudStage.act(delta);

        // Set debug position
        Vector2 loc = new Vector2(
                (float) Gdx.input.getX(),
                (float) Gdx.input.getY()
        );
        Geolocation geo = map.getGeolocation(loc);
        pos.setText(geo + "");

        stage.getViewport().apply();
        stage.draw();
        hudStage.getViewport().apply();
        hudStage.draw();
    }


    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        stage.dispose();
        hudStage.dispose();
    }
}