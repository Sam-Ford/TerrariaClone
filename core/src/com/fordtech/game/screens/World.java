package com.fordtech.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.fordtech.game.GdxGame;
import com.fordtech.game.entity.Player;

/**
 * Created by samuel on 13/11/16.
 */

public class World implements Screen {
    final GdxGame game;
    public static int TILE_SIZE = 64;
    private Vector2 screenSize;

    private OrthographicCamera camera;
    private TiledMap map;
    private TiledMapTileLayer collisionLayer;
    private OrthogonalTiledMapRenderer mapRenderer;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private Player player;

    public World(final GdxGame g) {
        game = g;

        screenSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenSize.x, screenSize.y);
        //camera.position.set(5 * TILE_SIZE, 5 * TILE_SIZE, 0);

        map = new TmxMapLoader().load("maps/map.tmx");
        //map = createMap(new TmxMapLoader().load("maps/tiles.tmx").getTileSets().getTileSet("tiles"), 10, 10, 64, 64);

        collisionLayer = (TiledMapTileLayer) map.getLayers().get("collisionLayer");
        //System.out.println(collisionLayer.getCell(2, 0).getTile().getId());

        mapRenderer = new OrthogonalTiledMapRenderer(map);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        Texture texture = new Texture("img/player.png");
        player = new Player(collisionLayer, texture, 8, 37);
        player.setCameraHandle(camera);
        Gdx.input.setInputProcessor(player);

    }

    public TiledMapTileLayer getCollisionLayer() {
        return (TiledMapTileLayer) map.getLayers().get(0);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        camera.update();
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //System.out.println("Fps: " + Gdx.graphics.getFramesPerSecond());

        update(delta);

        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        player.render(batch);
        //batch.draw(lightMap, 0, 0);

        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        shapeRenderer.end();
    }

    public void update(float delta) {
        player.update(delta);
        camera.position.set(player.position.x + player.size.x / 2, player.position.y + player.size.y / 2, 0);
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
    }


    public Texture createMiniMap(TiledMapTileLayer tileLayer, float screenX, float screenY, int mapWidth, int mapHeight) {
        int xPos = (int) (screenX / collisionLayer.getTileWidth());
        int yPos = (int) (screenY / collisionLayer.getTileHeight());
        Pixmap pixmap = new Pixmap(mapWidth, mapHeight, Pixmap.Format.RGBA8888);


        float xBound = tileLayer.getTileWidth() - 1;
        float yBound = tileLayer.getTileHeight() - 1;

        if (xPos < 0 || yPos < 0 || xPos > xBound || yPos > yBound - 1) {
            System.out.println("Error: co-ordinates out of bounds");
            return null;
        }

        if (xPos + mapWidth > xBound || yPos + mapHeight > yBound) {
            System.out.println("Error: size out of bounds");
            return null;
        }

        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                float v = 1f / tileLayer.getCell(xPos + x, yPos + yPos).getTile().getId();
                pixmap.setColor(new Color(v, v, v, 1f));
                pixmap.drawPixel(x, y);
            }
        }

        Texture t = new Texture(pixmap);
        t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
        return t;
    }

    /*
    private TiledMap createMap(TiledMapTileSet tileSet, int w, int h, int tW, int tH) {
        TiledMap map = new TiledMap();
        MapLayers layers = map.getLayers();

        TiledMapTileLayer collisionLayer = new TiledMapTileLayer(w, h, tW, tH);
        collisionLayer.setName("collisionLayer");

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                if (y > h * 0.75f) cell.setTile(tileSet.getTile(3));
                if (y > h / 2) cell.setTile(tileSet.getTile(2));
                else cell.setTile(tileSet.getTile(4));

                collisionLayer.setCell(x, y, cell);
            }
        }


        layers.add(collisionLayer);

        return map;
    }
    */

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
        batch.dispose();
        //lightMap.dispose();
    }
}
