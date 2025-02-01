package com.mygdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class Room {
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Rectangle exitTop;
    private Texture exitLockedTexture;
    private boolean isCleared;
    private ArrayList<Enemy> enemies;
    private ArrayList<Item> items;
    private int pixelWidth;
    private int pixelHeight;
    private ArrayList<Rectangle> collisionRectangles;

    GameAssetManager assets = GameAssetManager.getInstance();
    Texture skeletonTexture = assets.get("Skeleton.png", Texture.class);
    Texture slimeTexture = assets.get("slime.png", Texture.class);

    private int waveCount;
    private float waveTimer;
    private final float WAVE_INTERVAL = 3f;

    public Room(String mapFile) {
        map = new TmxMapLoader().load(mapFile);
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        int mapWidth = map.getProperties().get("width", Integer.class);
        int mapHeight = map.getProperties().get("height", Integer.class);
        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);

        pixelWidth = mapWidth * tileWidth;
        pixelHeight = mapHeight * tileHeight;
        enemies = new ArrayList<>();
        items = new ArrayList<>();
        collisionRectangles = new ArrayList<>();
        generateItems();

        MapObjects objects = map.getLayers().get("collision").getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                String objectName = object.getName();

                if ("ExitTop".equals(objectName)) {
                    exitTop = rectObject.getRectangle();
                }
                else {
                    collisionRectangles.add(rectObject.getRectangle());
                }
            }
        }

        isCleared = false;
        waveCount = 5; // 初始波次
        waveTimer = WAVE_INTERVAL;
        exitLockedTexture = new Texture("x.png");
    }

    public ArrayList<Rectangle> getCollisionRectangles() {
        return collisionRectangles;
    }

    public void updateWave(float delta) {
        if (waveCount > 0) {
            if (enemies.isEmpty()) {
                waveTimer -= delta;
                if (waveTimer <= 0) {
                    waveCount--;
                    waveTimer = WAVE_INTERVAL;
                    generateEnemies();
                }
            }
        } else {
            isCleared = true;
        }
    }

    public void renderExit(SpriteBatch batch) {
        if (waveCount > 0) {
            batch.draw(exitLockedTexture, exitTop.x, exitTop.y, exitTop.width, exitTop.height);
        }
    }


    public void render(float delta, OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    private int getRand(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    private void generateEnemies() {
        int baseCount = 3; // 每波敌人基础数量
        int difficultyMultiplier = 2; // 难度增幅

        for (int i = 0; i < baseCount + waveCount * difficultyMultiplier; i++) {
            int x = getRand(50, pixelWidth - 50);
            int y = getRand(50, pixelHeight - 180);
            if (Math.random() > 0.5) {
                enemies.add(new RangedEnemy(skeletonTexture, x, y, 75f, 40, 200f));
            } else {
                enemies.add(new MeleeEnemy(slimeTexture, x, y, 90f, 50));
            }
        }
    }


    private void generateItems(){
        for (int i = 0; i < getRand(1, 5); i++) {
            int x = getRand(50, pixelWidth - 50);
            int y = getRand(50, pixelHeight - 180);
            items.add(new Coin(x, y));
        }

        for (int i = 0; i < getRand(1, 5); i++) {
            int x = getRand(50, pixelWidth - 50);
            int y = getRand(50, pixelHeight - 180);
            items.add(new Heart( x, y));
        }

    }


    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public Rectangle getExitTop() {
        return exitTop;
    }



    public boolean isCleared() {
        return isCleared;
    }

    public void clearRoom() {
        isCleared = true;
    }

    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
        for (Item item : items) {
            item.dispose();
        }
        exitLockedTexture.dispose();
    }

    public ArrayList<Item> getItems() {
        return items;
    }
}
