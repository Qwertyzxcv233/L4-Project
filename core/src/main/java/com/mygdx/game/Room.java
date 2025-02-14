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
    private OrthogonalTiledMapRenderer mapRender;
    private Rectangle exitTop;
    private Texture XTexture;
    private boolean isCleared;
    private ArrayList<Enemy> enemies;
    private ArrayList<Item> items;
    private int width;
    private int height;
    private ArrayList<Rectangle> collisionRectangles;

    GameAssetManager assets = GameAssetManager.getInstance();


    private int waveCount;
    private float waveTimer;
    private final float WAVE_INTERVAL = 3f;

    public Room(String mapFile) {
        map = new TmxMapLoader().load(mapFile);
        mapRender = new OrthogonalTiledMapRenderer(map);

        int mapWidth = map.getProperties().get("width", Integer.class);
        int mapHeight = map.getProperties().get("height", Integer.class);
        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);

        width = mapWidth * tileWidth;
        height = mapHeight * tileHeight;
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
        waveCount = 5;
        waveTimer = WAVE_INTERVAL;
        XTexture = new Texture("x.png");
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
            batch.draw(XTexture, exitTop.x, exitTop.y, exitTop.width, exitTop.height);
        }
    }


    public void render(OrthographicCamera camera) {
        mapRender.setView(camera);
        mapRender.render();
    }

    private int getRand(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    private void generateEnemies() {
        int baseCount = 3;
        int difficultyMultiplier = 2;

        for (int i = 0; i < baseCount + waveCount + difficultyMultiplier; i++) {
            int x = getRand(50, width - 50);
            int y = getRand(50, height - 180);
            String enemyType = Math.random() > 0.5 ? "ranged" : "melee";
            enemies.add(createEnemy(enemyType, x, y));
        }
    }


    private void generateItems(){
        for (int i = 0; i < getRand(1, 5); i++) {
            int x = getRand(50, width - 50);
            int y = getRand(50, height - 180);
            String itemType = Math.random() > 0.5 ? "coin" : "heart";
            items.add(createItem(itemType, x, y));
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
        mapRender.dispose();
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
        for (Item item : items) {
            item.dispose();
        }
        XTexture.dispose();
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public static Enemy createEnemy(String type, float x, float y) {
        GameAssetManager assets = GameAssetManager.getInstance();
        int randomNum = (int)(Math.random() * 21);

        switch (type.toLowerCase()) {
            case "ranged":
                return new RangedEnemy(assets.get("Skeleton.png", Texture.class), x, y, 75f-randomNum, 40, 200f);
            case "melee":
                return new MeleeEnemy(assets.get("slime.png", Texture.class), x, y, 90f-randomNum, 50);
            default:
                throw new IllegalArgumentException("unknown：" + type);
        }
    }
    public static Item createItem(String type, float x, float y) {
        switch (type.toLowerCase()) {
            case "heart":
                return new Heart(x, y);
            case "coin":
                return new Coin(x, y);
            default:
                throw new IllegalArgumentException("unknown" + type);
        }
    }
}
