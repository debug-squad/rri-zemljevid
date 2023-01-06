package si.feri.slidegame.screen.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import si.feri.slidegame.MyGdxGame;
import si.feri.slidegame.assets.AssetDescriptors;
import si.feri.slidegame.assets.RegionNames;
import si.feri.slidegame.common.Database;
import si.feri.slidegame.config.GameConfig;
import si.feri.slidegame.utils.Geolocation;
import si.feri.slidegame.utils.Map;
import si.feri.slidegame.utils.PixelPosition;

import java.io.IOException;
import java.util.ArrayList;

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

    public static final float REFRESH_INTERVAL = 10f;

    public float refreshTime;

    public ArrayList<Geolocation> locations;
    public Group markers;


    public Map map;

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

        refreshTime = 0;

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
                new GestureDetector(mapGestureListener),
                stage,
                hudStage
        ));

        //
        markers = new Group();
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
        // Dynamic data
        //

        refreshTime -= delta;
        if(refreshTime <= 0) {
            refreshTime = REFRESH_INTERVAL;
            locations = new ArrayList<>();
            locations.add(new Geolocation(0, 0, "","","","","","",""));
            try {
                markers.clearChildren();
                for(Database.Event event: Database.fetchEvents().values()) {
                    final Geolocation geo = event.getLocation();
                    final PixelPosition pos = map.getPixelPosition(geo);

                    // Add to cache
                    locations.add(geo);

                    //
                    TextureRegion markerTexture = gameplayAtlas.findRegion(RegionNames.MARKER);
                    Image marker = new Image(markerTexture);
                    marker.setWidth(50);
                    marker.setHeight(50);
                    marker.setPosition(pos.x-marker.getWidth()/2,pos.y);
                    marker.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            showMenu(geo);
                        }
                    });
                    markers.addActor(marker);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //
        //
        //

        mapGestureListener.handleInput(delta);

        stage.act(delta);
        hudStage.act(delta);

        Geolocation geo = map.getGeolocation(new Vector3(
                (float) Gdx.input.getX(),
                (float) Gdx.input.getY(),
                0f
        ));
        pos.setText(geo + "");
        locations.set(0, geo);

        stage.draw();
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

    //
    //
    //

    public void showMenu(Geolocation geo){
        Table table = new Table();
        //table.defaults().pad(20);

        TextField name = new TextField(geo.name, uskin);
        TextField description = new TextField(geo.description, uskin);
        TextField eventcreator = new TextField(geo.eventcreator, uskin);
        TextField latitude = new TextField(geo.latitude, uskin);
        TextField longitude = new TextField(geo.longitude, uskin);
        TextField date = new TextField(geo.date, uskin);
        TextField time = new TextField(geo.time, uskin);

        TextButton buttonEdit = new TextButton("Edit", uskin);
        buttonEdit.setTransform(true);
        buttonEdit.setScale(0.3f);
        buttonEdit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });


        //Gdx.input.setInputProcessor(hudStage);

        table.add(name).row();
        table.add(description).row();
        table.add(eventcreator).row();
        table.add(latitude).row();
        table.add(longitude).row();
        table.add(date).row();
        table.add(time).row();
        //table.add(buttonEdit).center().padLeft(300);

        table.top().right();
        table.setFillParent(true);
        table.pack();

        hudStage.addActor(table);
    }
}