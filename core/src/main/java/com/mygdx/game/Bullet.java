package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Bullet {
    private Texture texture;
    private float x, y;
    private float speed;
    private float directionX, directionY;
    private boolean isActive;

    public Bullet(String texturePath, float startX, float startY, float targetX, float targetY, float speed) {
        if (texture == null) {
            texture = new Texture("bullet.png");
        }
        this.texture = new Texture(texturePath);
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.isActive = true;

        // 计算方向
        float length = (float) Math.sqrt((targetX - startX) * (targetX - startX) + (targetY - startY) * (targetY - startY));
        if (length != 0) {
            this.directionX = (targetX - startX) / length;
            this.directionY = (targetY - startY) / length;
        }
    }

    public void update(float delta) {
        if (isActive) {
            x += directionX * speed * delta;
            y += directionY * speed * delta;

            // 简单的边界检测，超出屏幕则标记为不活跃
            if (x < 0 || x > Gdx.graphics.getWidth() || y < 0 || y > Gdx.graphics.getHeight()) {
                isActive = false;
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (isActive) {
            batch.draw(texture, x, y);
        }
    }

    public void dispose() {
        texture.dispose();
    }

    public boolean isActive() {
        return isActive;
    }
    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public void deactivate() {
        this.isActive = false;
    }
}
