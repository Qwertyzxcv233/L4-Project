package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;

public class Enemy {
    private float x, y;
    private final float speed;
    private int health;
    private float lastDeltaX = 0;
    private float lastDeltaY = 0;
    private float attackCD = 2;
    private float lastAttackTime = 0;
    private int attackDamage = 10;
    private Sound hitSound;


    protected Texture texture;
    protected Animation<TextureRegion> walkAnima;
    protected Animation<TextureRegion> idleAnima;
    protected float animaTimer = 0f;
    protected boolean isMoving = false;
    protected boolean isFacingRight = true;
    private boolean isHurt = false;
    private float hurtAnimaTimer = 0f;
    private final float hurtAnimaDur = 0.4f;


    private Circle boundingCircle;

    public Enemy(Texture texture, float startX, float startY, float speed, int health) {
        this.texture = texture;
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.health = health;
        this.boundingCircle = new Circle(x + 16, y + 16, 16); // 半径为 16

        hitSound = Gdx.audio.newSound(Gdx.files.internal("hurt.mp3"));
    }

    public Enemy(float startX, float startY, float speed, int health) {
        this.texture = new Texture("sl.png");
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.health = health;
        this.boundingCircle = new Circle(x + 16, y + 16, 16); // 假设半径为 16
    }

    public void update(float delta, float playerX, float playerY) {
        lastAttackTime += delta;
        animaTimer += delta;
        moveTowardsPlayer(playerX, playerY, delta);
        boundingCircle.setPosition(x + 16, y + 16);
    }

    protected void moveTowardsPlayer(float playerX, float playerY, float deltaTime) {
        float directionX = playerX - x;
        float directionY = playerY - y;
        float length = (float) Math.sqrt(directionX * directionX + directionY * directionY);
        isMoving = false;

        if (length != 0) {
            directionX /= length;
            directionY /= length;
            lastDeltaX = directionX * speed * deltaTime;
            lastDeltaY = directionY * speed * deltaTime;
            x += lastDeltaX;
            y += lastDeltaY;
            isMoving = true;

            if (lastDeltaX > 0) {
                isFacingRight = true;
            } else if (lastDeltaX < 0) {
                isFacingRight = false;
            }
        }
    }

    public void preventMovement() {
        x -= lastDeltaX;
        y -= lastDeltaY;
        lastDeltaX = 0;
        lastDeltaY = 0;
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;

        if (isMoving && walkAnima != null) {
            currentFrame = walkAnima.getKeyFrame(animaTimer, true);
        } else if (idleAnima != null) {
            currentFrame = idleAnima.getKeyFrame(animaTimer, true);
        } else {
            batch.draw(texture, x, y);
            return;
        }

        if ((isFacingRight && currentFrame.isFlipX()) || (!isFacingRight && !currentFrame.isFlipX())) {
            currentFrame.flip(true, false);
        }

        batch.draw(currentFrame, x, y);
    }

    public void dispose() {
        texture.dispose();
        hitSound.dispose();
    }

    public Circle getBoundingCircle() {
        return boundingCircle;
    }

    public void takeDamage(int damage) {
        health -= damage;
        hitSound.play();
        isHurt = true;
        hurtAnimaTimer = hurtAnimaDur;
        if (health <= 0) {
            health = 0;
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    public boolean canAttackPlayer(Player player) {
        Circle playerCircle = player.getBoundingCircle();
        return Intersector.overlaps(boundingCircle, playerCircle);
    }

    public void attackPlayer(Player player) {
        if (lastAttackTime >= attackCD && canAttackPlayer(player)) {
            player.takeDamage(attackDamage);
            lastAttackTime = 0f;
        }
    }





    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getSpeed() {
        return speed;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public Texture getTexture() {
        return texture;
    }

    public float getAttackCD() {
        return attackCD;
    }
}
