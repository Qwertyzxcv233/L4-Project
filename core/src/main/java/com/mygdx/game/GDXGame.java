package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    private HUD hud;
    private int lastWaveDisplayed = 0;
    private boolean isShopOpen = false;
    private float shopMessageTimer = 0;
    private String shopMessage = "";
    private int currentRoomNumber = 1;
    @Override
    public void create() {
        initCameraAndBatch();
        initGameAssets();
        isShopOpen = false;
        initPlayer();
        initEnemies();
        initUI();
//        initMusic();
        loadNewRoom("battleroom1.tmx");
    }

    @Override
    public void render() {
        clearScreen();
        float delta = Gdx.graphics.getDeltaTime();
        if (isTransitioning) {
            handleTransition();
        } else if (isShopOpen) {
            handleShopInput();

        }else if (!isGameOver) {
            currentRoom.updateWave(delta);
            updateGameLogic();
        }else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                resetGame();
            }
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        currentRoom.render(camera);
        if (isShopOpen) {
            renderShop();
        } else if (!isGameOver) {
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

        if (currentRoom.getCurrentWave() > lastWaveDisplayed) {
            lastWaveDisplayed = currentRoom.getCurrentWave();
            hud.showWaveMessage(currentRoom.getCurrentWave(), currentRoom.getTotalWaves());
        }
        hud.update(delta);
        batch.begin();
        hud.render(batch);
        batch.end();

        if (shopMessageTimer > 0) {
            shopMessageTimer -= delta;
        }
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
        camera.zoom = 0.3f;
        camera.setToOrtho(false);

    }

    private void resetGame() {
        isGameOver = false;
        isShopOpen = false;
        lastWaveDisplayed = 0;
        currentRoomNumber = 1;
        enemies.clear();
        bullets.clear();
        loadNewRoom("battleroom1.tmx");
        isShopOpen = false;
//        if (bgm != null && !bgm.isPlaying()) {
//            bgm.play();
//        }
        player.reset();
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
        hud = new HUD(player);
    }

    private void initMusic() {
        bgm = Gdx.audio.newMusic(Gdx.files.internal("bgm.mp3"));
        bgm.setLooping(true);
        bgm.play();
    }

    private void loadNewRoom(String mapFile) {
        if (currentRoom != null) {
            isShopOpen = true;
        }else {
            isShopOpen = false;
        }
        player.setX(60);
        player.setY(60);
        currentRoom = new Room(mapFile);
        collision.setRoom(currentRoom);
        enemies = currentRoom.getEnemies();
        isTransitioning = true;
        transitionTime = 1.5f;
        lastWaveDisplayed = 0;
    }

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
            if (!enemy.isAlive()) {
                player.addCoins(10);
            }
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
            if (Intersector.overlaps(player.getBoundingCircle(), currentRoom.getExit())) {
                if (currentRoomNumber == 1) {
                    currentRoomNumber = 2;
                    loadNewRoom("battleroom2.tmx");
                } else {
                    currentRoomNumber = 1;
                    loadNewRoom("battleroom1.tmx");
                }
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
        float centerX = camera.position.x;
        float centerY = camera.position.y;
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(blackTexture, centerX - 150, centerY - 100, 300, 200);
        batch.setColor(1, 1, 1, 1);
        font.setColor(Color.RED);
        font.draw(batch, "Game Over!", centerX - 50, centerY + 50);
        font.setColor(Color.YELLOW);
        font.draw(batch, "Coins: " + player.getCoins(), centerX - 40, centerY + 10);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "Press R to Restart", centerX - 80, centerY - 60);

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

    private void handleShopInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            if (player.getCoins() >= 100) {
                player.addCoins(-100);

                if (player.getHealth() >= 100) {
                    showShopMessage("Life is already full, but you still spent 100 coins!");
                } else {
                    player.setHealth(100);
                    showShopMessage("Health restored to 100!");
                }
            } else {
                showShopMessage("Not enough coins! You need 100 coins.");
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            if (player.getCoins() >= 200) {
                player.addCoins(-200);
                player.setAttackPower(player.getAttackPower() + 5);
                showShopMessage("Attack power increased by 5!");
            } else {
                showShopMessage("Not enough coins! You need 200 coins.");
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            if (player.getCoins() >= 300) {
                boolean canReduce = false;
                for (Enemy enemy : currentRoom.getEnemies()) {
                    if (enemy.getAttackDamage() > 10) {
                        canReduce = true;
                        break;
                    }
                }

                if (canReduce) {
                    player.addCoins(-300);

                    for (Enemy enemy : currentRoom.getEnemies()) {
                        if (enemy.getAttackDamage() > 10) {
                            enemy.setAttackDamage(enemy.getAttackDamage() - 2);
                        }
                    }

                    showShopMessage("Enemies' attack damage reduced by 2!");
                } else {
                    showShopMessage("Enemies are already weak enough!");
                    player.addCoins(-300);
                }
            } else {
                showShopMessage("Not enough coins! You need 300 coins.");
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isShopOpen = false;
        }
    }

    private void renderShop() {
        float centerX = camera.position.x;
        float centerY = camera.position.y;
        batch.setColor(0, 0, 0, 0.8f);
        batch.draw(blackTexture, centerX - 150, centerY - 120, 300, 240);
        batch.setColor(1, 1, 1, 1);
        font.setColor(Color.YELLOW);
        font.draw(batch, "SHOP", centerX - 25, centerY + 100);
        font.setColor(Color.GOLD);
        font.draw(batch, "Your Coins: " + player.getCoins(), centerX - 70, centerY + 70);
        font.setColor(Color.WHITE);
        font.draw(batch, "1. Restore Health - 100 coins", centerX - 120, centerY + 30);
        font.draw(batch, "2. Increase Attack - 200 coins", centerX - 120, centerY);
        font.draw(batch, "3. Reduce Enemy Damage - 300 coins", centerX - 120, centerY - 30);

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "Current Health: " + player.getHealth(), centerX - 120, centerY - 70);
        font.draw(batch, "Current Attack: " + player.getAttackPower(), centerX - 120, centerY - 90);

        if (shopMessageTimer > 0) {
            font.setColor(Color.CYAN);
            font.draw(batch, shopMessage, centerX - 140, centerY - 140);
        }

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "Press ESC to continue", centerX - 130, centerY - 122);
    }
    private void showShopMessage(String message) {
        shopMessage = message;
        shopMessageTimer = 3.0f;
    }

}
