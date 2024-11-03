package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {
    protected Texture texture;
    protected float x, y;
    protected float speed;
    protected int health;
    float attackCooldown = 2.0f;  // 攻击冷却时间，单位：秒
    private float timeSinceLastAttack = 0f;
    private int attackDamage = 10;

    public Enemy(String texturePath, float startX, float startY, float speed, int health) {
        this.texture = new Texture(texturePath);
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.health = health;
    }

    public void update(float delta, float playerX, float playerY) {
        timeSinceLastAttack += delta;
        moveTowardsPlayer(playerX, playerY, delta);
    }

    protected void moveTowardsPlayer(float playerX, float playerY, float deltaTime) {
        float directionX = playerX - x;
        float directionY = playerY - y;
        float length = (float) Math.sqrt(directionX * directionX + directionY * directionY);
        if (length != 0) {
            directionX /= length;
            directionY /= length;
            x += directionX * speed * deltaTime;
            y += directionY * speed * deltaTime;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public void dispose() {
        texture.dispose();
    }
    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            // 添加死亡动画逻辑
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    public boolean canAttackPlayer(Player player) {
        float distance = (float) Math.sqrt((player.getX() - x) * (player.getX() - x) + (player.getY() - y) * (player.getY() - y));
        return distance < 50f; // 攻击范围内，假设攻击距离为 50 像素
    }

    public void attackPlayer(Player player) {
        if (timeSinceLastAttack >= attackCooldown && canAttackPlayer(player)) {
            player.takeDamage(attackDamage);
            timeSinceLastAttack = 0f;  // 重置冷却时间
        }
    }
    public int getAttackPower() {
        return attackDamage;
    }
}
