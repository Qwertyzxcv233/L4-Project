package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;

import java.util.ArrayList;

public class GDXGame extends ApplicationAdapter {
    private Collision collision;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Player player;
    private static ArrayList<Enemy> enemies;
    private static ArrayList<Bullet> bullets;
    private boolean isGameOver;
    private BitmapFont font;
    private Room currentRoom;
    private boolean isTransitioning;
    private float transitionTime;
    private Texture blackTexture;
    private Music bgm;

    @Override
    public void create() {
        initCameraAndBatch();
        initGameAssets();

        initPlayer();
        initEnemies();
        initUI();
        initMusic();
        loadNewRoom("battleroom1.tmx");
    }

    @Override
    public void render() {
        clearScreen();
        float delta = Gdx.graphics.getDeltaTime();
        if (isTransitioning) {
            handleTransition();
        } else if (!isGameOver) {
            currentRoom.updateWave(delta);
            updateGameLogic();
        }
        renderGame();
    }

    @Override
    public void dispose() {
        disposeResources();
    }


    private void initGameAssets() {
        GameAssetManager.getInstance().loadGameAssets();
    }

    private void initCameraAndBatch() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);
        camera.zoom = 0.3f;
    }

    private void initPlayer() {
        player = new Player(50, 50, 150f, 100, 10);
    }

    private void initEnemies() {
        enemies = new ArrayList<>();
        bullets = new ArrayList<>();
        collision = new Collision(currentRoom);
    }

    private void initUI() {
        font = new BitmapFont();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        blackTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private void initMusic() {
        bgm = Gdx.audio.newMusic(Gdx.files.internal("bgm.mp3"));
        bgm.setLooping(true);
        bgm.play();
    }

    private void loadNewRoom(String mapFile) {
        if (currentRoom != null) {
            currentRoom.dispose();
        }
        player.setX(60);
        player.setY(60);
        currentRoom = new Room(mapFile);
        collision.setRoom(currentRoom);
        enemies = currentRoom.getEnemies();
        isTransitioning = true;
        transitionTime = 1.5f;
    }

    // 更新逻辑方法
    private void updateGameLogic() {
        float delta = Gdx.graphics.getDeltaTime();
        updatePlayer(delta);
        updateEnemies(delta);
        updateBullets(delta);
        updateItems(delta);
        collision.updateCollisions(bullets, enemies, player);
        checkRoomTransition();
        checkGameOver();
    }

    private void updatePlayer(float delta) {
        collision.PlayerCollision(player);
        player.update(delta, camera);
        camera.position.set(player.getX() + 16, player.getY() + 16, 0);
        camera.update();
    }

    private void updateEnemies(float delta) {
        for (Enemy enemy : enemies) {
            enemy.update(delta, player.getX(), player.getY());
            collision.EnemyCollision(enemy);
            enemy.attackPlayer(player);
        }
        enemies.removeIf(enemy -> !enemy.isAlive());
    }

    private void updateBullets(float delta) {
        for (Bullet bullet : bullets) {
            if (bullet.isActive()) {
                bullet.update(delta);
                collision.BulletCollision(bullet,player);
            }
        }
        bullets.removeIf(bullet -> !bullet.isActive());
    }

    private void updateItems(float delta) {
        for (Item item : currentRoom.getItems()) {
            item.update(delta, player);
        }
        currentRoom.getItems().removeIf(Item::isCollected);
    }

    private void checkRoomTransition() {
        if (currentRoom.isCleared()) {
            if (Intersector.overlaps(player.getBoundingCircle(), currentRoom.getExitTop())) {
                loadNewRoom("battleroom2.tmx");
            }
        }
    }

    private void checkGameOver() {
        if (!player.isAlive()) {
            isGameOver = true;
        }
    }


    private void renderGame() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        currentRoom.render(camera);

        if (!isGameOver) {
            renderPlayer();
            renderEnemies();
            renderBullets();
            renderItems();
            currentRoom.renderExit(batch);
        } else {
            renderGameOverUI();
        }

        renderTransitionEffect();
        batch.end();
    }

    private void renderPlayer() {
        player.render(batch);
    }

    private void renderEnemies() {
        for (Enemy enemy : enemies) {
            enemy.render(batch);
        }
    }

    private void renderBullets() {
        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }
    }

    private void renderItems() {
        for (Item item : currentRoom.getItems()) {
            item.render(batch);
        }
    }

    private void renderGameOverUI() {
        font.draw(batch, "Game Over! Press R to Restart", camera.position.x - 80, camera.position.y);
    }

    private void renderTransitionEffect() {
        if (isTransitioning || transitionTime < 1.0f) {
            batch.setColor(0, 0, 0, transitionTime);
            batch.draw(blackTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(1, 1, 1, 1);
        }
    }

    private void handleTransition() {
        float delta = Gdx.graphics.getDeltaTime();
        transitionTime -= delta;
        if (transitionTime <= 0) {
            isTransitioning = false;
        }
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void disposeResources() {
        batch.dispose();
        player.dispose();
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
        for (Bullet bullet : bullets) {
            bullet.dispose();
        }
        for (Item item : currentRoom.getItems()) {
            item.dispose();
        }
        font.dispose();
    }

    public static void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public static ArrayList<Enemy> getEnemies() {
        return enemies;
    }
}
