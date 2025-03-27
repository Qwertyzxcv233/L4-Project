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
    private OrthogonalTiledMapRenderer render;
    private Rectangle exit;
    private Texture XTexture;
    private boolean isCleared;
    private ArrayList<Enemy> enemies;
    private ArrayList<Item> items;
    private int width;
    private int height;
    private ArrayList<Rectangle> collisionRectangles;
    private int waveCount;
    private float waveTimer;
    private float waveInterval = 3f;
    private int totalWaves;

    GameAssetManager assets = GameAssetManager.getInstance();




    public Room(String mapFile) {
        map = new TmxMapLoader().load(mapFile);
        render = new OrthogonalTiledMapRenderer(map);

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
                    exit = rectObject.getRectangle();
                }
                else {
                    collisionRectangles.add(rectObject.getRectangle());
                }
            }
        }

        isCleared = false;
        waveCount = 4;
        waveTimer = waveInterval;
        totalWaves = waveCount;
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
                    waveTimer = waveInterval;
                    generateEnemies();
                }
            }
        } else {
            isCleared = true;
        }
    }

    public void renderExit(SpriteBatch batch) {
        if (waveCount > 0) {
            batch.draw(XTexture, exit.x, exit.y, exit.width, exit.height);
        }
    }


    public void render(OrthographicCamera camera) {
        render.setView(camera);
        render.render();
    }

    private int getRand(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    private void generateEnemies() {
        int baseCount = 5;
        int difficultyMultiplier = 2;

        for (int i = 0; i < baseCount + waveCount + difficultyMultiplier; i++) {
            int x = getRand(50, width - 50);
            int y = getRand(50, height - 180);
            String enemyType;
            if (Math.random() > 0.5){
                enemyType = "ranged";
            }else {
                enemyType = "melee";
            }
            enemies.add(createEnemy(enemyType, x, y));
        }
    }


    private void generateItems(){
        for (int i = 0; i < getRand(1, 5); i++) {
            int x = getRand(50, width - 50);
            int y = getRand(50, height - 180);
            String itemType;
            if (Math.random() > 0.5){
                itemType = "heart";
            }else {
                itemType = "coin";
            }
            items.add(createItem(itemType, x, y));
        }



    }
    public int getCurrentWave() {
        return totalWaves - waveCount;
    }


    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public Rectangle getExit() {
        return exit;
    }

    public int getTotalWaves() {
        return totalWaves;
    }

    public boolean isCleared() {
        return isCleared;
    }

    public void clearRoom() {
        isCleared = true;
    }

    public void dispose() {
        map.dispose();
        render.dispose();
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
