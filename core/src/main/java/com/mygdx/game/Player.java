package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;

public class Player {
    private Texture texture;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> idleAnimation;
    private float animationTimer = 0f;

    private float x, y;
    private float speed;
    private int health;
    private int attackPower;
    private Weapon weapon;
    private boolean isAlive;
    private int coins = 0;
    private float lastDeltaX = 0;
    private float lastDeltaY = 0;

    private boolean isMoving = false;
    private boolean isFacingRight = true;
    private Sound shootSound;

    private Animation<TextureRegion> hurtAnimation; // 受伤动画
    private float hurtAnimationTimer = 0f;
    private boolean isHurt = false;
    private float damageFlashTimer = 0f; // 受伤变白时间


    private Circle boundingCircle;

    public Player(float startX, float startY, float speed, int health, int attackPower) {
        this.texture = GameAssetManager.getInstance().get("Player.png", Texture.class);
        TextureRegion[][] frames = TextureRegion.split(this.texture, 32, 32);
        TextureRegion[] walkFrames = new TextureRegion[6]; // 6帧
        TextureRegion[] idleFrames = new TextureRegion[6];
        TextureRegion[] hurtFrames = new TextureRegion[3];

        for (int i = 0; i < 6; i++) {
            walkFrames[i] = frames[4][i]; // 行走动画在精灵图的第5行
        }
        for (int i = 0; i < 6; i++) {
            idleFrames[i] = frames[1][i];
        }
        for (int i = 0; i < 3; i++) {
            hurtFrames[i] = frames[6][i];
        }

        walkAnimation = new Animation<>(0.2f, walkFrames);
        idleAnimation = new Animation<>(0.2f, idleFrames);
        hurtAnimation = new Animation<>(0.2f, hurtFrames);
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.health = health;
        this.attackPower = attackPower;
        this.weapon = new Gun(1, 20); // 给玩家初始化装备一把手枪
        this.isAlive = true;

        this.boundingCircle = new Circle(startX + 16, startY + 16, 16);
        shootSound = GameAssetManager.getInstance().get("shoot.mp3", Sound.class);
    }

    public void update(float delta, OrthographicCamera camera) {
        if (!isAlive) return;

        if (damageFlashTimer > 0) {
            damageFlashTimer -= delta;
        }

        if (isHurt) {
            hurtAnimationTimer -= delta;
            if (hurtAnimationTimer <= 0) {
                isHurt = false;
            }
        }

        Input(delta);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 worldCoordinates = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            weapon.attack(x+16, y+16, worldCoordinates.x, worldCoordinates.y);
           shootSound.play();
        }

        animationTimer += delta;
        boundingCircle.setPosition(x + 16, y + 16);
    }

    private void Input(float delta) {
        lastDeltaX = 0;
        lastDeltaY = 0;
        isMoving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            lastDeltaY = speed * delta;
            y += lastDeltaY;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            lastDeltaY = -speed * delta;
            y += lastDeltaY;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            lastDeltaX = -speed * delta;
            x += lastDeltaX;
            isMoving = true;
            isFacingRight = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            lastDeltaX = speed * delta;
            x += lastDeltaX;
            isMoving = true;
            isFacingRight = true;
        }
    }

    public void preventMovement() {
        x -= lastDeltaX;
        y -= lastDeltaY;
        lastDeltaX = 0;
        lastDeltaY = 0;
    }

    public void render(SpriteBatch batch) {
        if (damageFlashTimer > 0) {
            batch.setColor(Color.WHITE); // 变白
        }
        if (!isAlive) return;

        TextureRegion currentFrame;
        if (isHurt && hurtAnimation != null) {
            currentFrame = hurtAnimation.getKeyFrame(hurtAnimationTimer, false); // 受伤动画
        } else if (isMoving) {
            currentFrame = walkAnimation.getKeyFrame(animationTimer, true);
        } else {
            currentFrame = idleAnimation.getKeyFrame(animationTimer, true);
        }
        if ((isFacingRight && currentFrame.isFlipX()) || (!isFacingRight && !currentFrame.isFlipX())) {
            currentFrame.flip(true, false);
        }

        batch.draw(currentFrame, x, y);
    }

    public void takeDamage(int damage) {
        if (!isAlive) return;
        health -= damage;
        System.out.println("Player Health: " + health);
        damageFlashTimer = 0.1f;
        isHurt = true;
        hurtAnimationTimer = 0.3f;
        if (health <= 0) {
            health = 0;
            isAlive = false;
            System.out.println("Player is dead!");
        }
    }

    public void dispose() {
        texture.dispose();
    }

    // Getter and Setter Methods
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public Circle getBoundingCircle() {
        return boundingCircle;
    }

    public void addCoins(int num) {
        coins += num;
    }

    public int getCoins() {
        return coins;
    }

    public void addHealth(int amount) {
        health += amount;
    }
}
