package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;

public class Bullet {

    private float x, y;
    private float speed;
    private float directionX, directionY;
    private boolean isActive;
    private boolean isEnemyBullet;
    private int damage;
    private float startX, startY;
    private float maxDistance = 1000f;

    private Circle boundingCircle;

    private static Texture texture = GameAssetManager.getInstance().get("bullet.png", Texture.class);

    public Bullet(String texturePath, float startX, float startY, float targetX, float targetY, float speed, boolean isEnemyBullet, int damage) {
        this.texture = Bullet.texture;

        // 设置子弹的起始位置
        this.x = startX;
        this.y = startY;
        this.startX = startX;
        this.startY = startY;

        this.speed = speed;
        this.isActive = true;
        this.isEnemyBullet = isEnemyBullet;
        this.damage = damage;

        // 计算方向
        float length = (float) Math.sqrt((targetX - startX) * (targetX - startX) + (targetY - startY) * (targetY - startY));
        if (length != 0) {
            this.directionX = (targetX - startX) / length;
            this.directionY = (targetY - startY) / length;
        }


        this.boundingCircle = new Circle(x, y, 4);
    }

    public void update(float delta) {
        if (isActive) {
            x += directionX * speed * delta;
            y += directionY * speed * delta;
            boundingCircle.setPosition(x, y);

            float distanceFromStart = (float) Math.sqrt((x - startX) * (x - startX) + (y - startY) * (y - startY));
            if (distanceFromStart > maxDistance) {
                isActive = false;
            }
        }
    }

    public boolean isEnemyBullet() {
        return isEnemyBullet;
    }

    public void render(SpriteBatch batch) {
        if (isActive) {
            batch.draw(texture, x - texture.getWidth() / 2f, y - texture.getHeight() / 2f);
        }
    }

    public void dispose() {

    }

    public int getDamage() {
        return damage;
    }

    public boolean isActive() {
        return isActive;
    }


    public Circle getBoundingCircle() {
        return boundingCircle;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
