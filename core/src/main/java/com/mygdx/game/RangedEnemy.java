package com.mygdx.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class RangedEnemy extends Enemy {
    private float attackRange;
    private float timeSinceLastAttack = 0f;
    private float attackCooldown = 2.0f;
    private Sound shootSound;

    public RangedEnemy(Texture texture, float startX, float startY, float speed, int health, float attackRange) {
        super(texture, startX, startY, speed, health);
        this.attackRange = attackRange;
        initializeAnimation();
        shootSound = GameAssetManager.getInstance().get("shoot.mp3", Sound.class);
    }


    private void initializeAnimation() {
        TextureRegion[][] frames = TextureRegion.split(this.texture, 32, 32);
        TextureRegion[] walkFrames = new TextureRegion[6]; // 6帧
        TextureRegion[] idleFrames = new TextureRegion[6];
        TextureRegion[] hurtFrames = new TextureRegion[3];
        for (int i = 0; i < 6; i++) {
            walkFrames[i] = frames[4][i];
            idleFrames[i] = frames[1][i];
        }


        walkAnimation = new Animation<>(0.1f, walkFrames);
        idleAnimation = new Animation<>(0.1f, idleFrames);
    }

    @Override
    public void update(float deltaTime, float playerX, float playerY) {
        float distance = (float) Math.sqrt((playerX - getX()) * (playerX - getX()) + (playerY - getY()) * (playerY - getY()));

        if (distance > attackRange) {
            super.update(deltaTime, playerX, playerY);
        } else {
            timeSinceLastAttack += deltaTime;
            animationTimer += deltaTime;

            if (timeSinceLastAttack >= attackCooldown) {
                fireBullet(playerX, playerY);
                timeSinceLastAttack = 0;
            }
            isMoving = false;
        }
    }

    private void fireBullet(float targetX, float targetY) {
        // 创建一个新子弹对象
        System.out.println("Monster Fire");
        shootSound.play(0.5f);
        Bullet bullet = new Bullet("bullet.png", getX(), getY(), targetX, targetY, 300f, true, 10);
        GDXGame.addBullet(bullet);
    }
}
