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
    private Animation<TextureRegion> walkAnima;
    private Animation<TextureRegion> idleAnima;
    private Animation<TextureRegion> hurtAnima;
    private float animationTimer = 0f;

    private float x, y;
    private float speed;
    private int health;
    private int attackPower;
    private Weapon weapon;
    private boolean isAlive;
    private int coins = 0;
    private float lastX = 0;
    private float lastY = 0;

    private boolean isMoving = false;
    private boolean isFacingRight = true;
    private Sound shootSound;

    private float hurtAnimaTimer = 0f;
    private boolean isHurt = false;
    private float FlashTimer = 0f;


    private Circle boundingCircle;

    public Player(float startX, float startY, float speed, int health, int attackPower) {
        this.texture = GameAssetManager.getInstance().get("Player.png", Texture.class);
        TextureRegion[][] frames = TextureRegion.split(this.texture, 32, 32);
        TextureRegion[] walkFrames = new TextureRegion[6]; // 6frames
        TextureRegion[] idleFrames = new TextureRegion[6];
        TextureRegion[] hurtFrames = new TextureRegion[3];

        for (int i = 0; i < 6; i++) {
            walkFrames[i] = frames[4][i]; // in line 5
        }
        for (int i = 0; i < 6; i++) {
            idleFrames[i] = frames[1][i];
        }
        for (int i = 0; i < 3; i++) {
            hurtFrames[i] = frames[6][i];
        }

        walkAnima = new Animation<>(0.2f, walkFrames);
        idleAnima = new Animation<>(0.2f, idleFrames);
        hurtAnima = new Animation<>(0.2f, hurtFrames);
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.health = health;
        this.attackPower = attackPower;
        this.weapon = new Gun(1, attackPower); //give player a gun
        this.isAlive = true;

        this.boundingCircle = new Circle(startX + 16, startY + 16, 16);
        shootSound = GameAssetManager.getInstance().get("shoot.mp3", Sound.class);
    }

    public void update(float delta, OrthographicCamera camera) {
        if (!isAlive) return;

        if (FlashTimer > 0) {
            FlashTimer -= delta;
        }

        if (isHurt) {
            hurtAnimaTimer -= delta;
            if (hurtAnimaTimer <= 0) {
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
        lastX = 0;
        lastY = 0;
        isMoving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            lastY = speed * delta;
            y += lastY;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            lastY = -speed * delta;
            y += lastY;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            lastX = -speed * delta;
            x += lastX;
            isMoving = true;
            isFacingRight = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            lastX = speed * delta;
            x += lastX;
            isMoving = true;
            isFacingRight = true;
        }
    }

    public void preventMovement() {
        x -= lastX;
        y -= lastY;
        lastX = 0;
        lastY = 0;
    }

    public void render(SpriteBatch batch) {
        if (FlashTimer > 0) {
            batch.setColor(Color.WHITE);
        }
        if (!isAlive) return;

        TextureRegion currentFrame;
        if (isHurt && hurtAnima != null) {
            currentFrame = hurtAnima.getKeyFrame(hurtAnimaTimer, false); // 受伤动画
        } else if (isMoving) {
            currentFrame = walkAnima.getKeyFrame(animationTimer, true);
        } else {
            currentFrame = idleAnima.getKeyFrame(animationTimer, true);
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
        FlashTimer = 0.1f;
        isHurt = true;
        hurtAnimaTimer = 0.3f;
        if (health <= 0) {
            health = 0;
            isAlive = false;
            System.out.println("Player is dead!");
        }
    }
    public void reset() {
        this.health = 100;
        this.coins = 0;
        this.x = 50;
        this.y = 50;
        this.isAlive = true;
        this.animationTimer = 0f;
        this.hurtAnimaTimer = 0f;
        this.FlashTimer = 0f;
        this.isHurt = false;
        this.attackPower = 10;
        updateWeaponDamage();

        this.boundingCircle.setPosition(x + 16, y + 16);
    }
    public void updateWeaponDamage() {
        if (weapon instanceof Gun) {
            ((Gun) weapon).setDamage(attackPower);
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
        updateWeaponDamage();
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
