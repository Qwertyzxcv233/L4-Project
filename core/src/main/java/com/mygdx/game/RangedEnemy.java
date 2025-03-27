package com.mygdx.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class RangedEnemy extends Enemy {
    private float attackRange;
    private float lastAttackTime = 0f;
    private float attackCD = 2.0f;
    private Sound shootSound;
    private Animation<TextureRegion> hurtAnima;
    private boolean isHurt = false;
    private float hurtAnimaTimer = 0f;
    private float hurtAnimaDur = 0.4f;
    private float moveCD = 4.0f;
    private float lastMovetime = 0f;
    private float moveDur = 1.5f;
    private float moveTimer = 0f;
    private float targetX, targetY;
    private int attackDamage;

    public RangedEnemy(Texture texture, float startX, float startY, float speed, int health, float attackRange) {
        super(texture, startX, startY, speed, health);
        this.attackRange = attackRange;
        initializeAnimation();
        shootSound = GameAssetManager.getInstance().get("shoot.mp3", Sound.class);

    }


    private void initializeAnimation() {
        TextureRegion[][] frames = TextureRegion.split(this.texture, 32, 32);
        TextureRegion[] walkFrames = new TextureRegion[6];
        TextureRegion[] idleFrames = new TextureRegion[6];
        TextureRegion[] hurtFrames = new TextureRegion[3];
        for (int i = 0; i < 6; i++) {
            walkFrames[i] = frames[4][i];
            idleFrames[i] = frames[1][i];
        }
        for (int i = 0; i < 3; i++) {
            hurtFrames[i] = frames[5][i];
        }


        walkAnima = new Animation<>(0.2f, walkFrames);
        idleAnima = new Animation<>(0.2f, idleFrames);
        hurtAnima = new Animation<>(0.1f, hurtFrames);
    }

    @Override
    public void update(float delta, float playerX, float playerY) {
        if (isHurt) {
            hurtAnimaTimer -= delta;
            if (hurtAnimaTimer <= 0) {
                isHurt = false;
            }
            return;
        }

        float distance = (float) Math.sqrt((playerX - getX()) * (playerX - getX()) + (playerY - getY()) * (playerY - getY()));

        lastMovetime += delta;
        if (lastMovetime >= moveCD && moveTimer <= 0) {
            calculateNewPosition();
            lastMovetime = 0;
            moveTimer = moveDur;
        }

        if (moveTimer > 0) {
            moveTimer -= delta;
            float moveSpeed = this.getSpeed() * 1.2f;
            float angle = (float) Math.atan2(targetY - getY(), targetX - getX());
            setX(getX() + (float) Math.cos(angle) * moveSpeed * delta);
            setY(getY() + (float) Math.sin(angle)* moveSpeed *delta);
            isMoving = true;
        } else {
            isMoving = false;
        }

        if (distance > attackRange) {
            super.update(delta, playerX, playerY);
        } else {
            lastAttackTime += delta;
            animaTimer += delta;

            if (lastAttackTime >= attackCD) {
                fireBullet(playerX, playerY);
                lastAttackTime = 0;
            }
            isMoving = false;
        }
        getBoundingCircle().setPosition(getX() + 16, getY() + 16);
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        isHurt = true;
        hurtAnimaTimer = hurtAnimaDur;
    }

    private void fireBullet(float targetX, float targetY) {
        shootSound.play(0.5f);
        Bullet bullet = new Bullet("bullet.png", getX(), getY(), targetX, targetY, 300f, true, super.getAttackDamage());
        GDXGame.addBullet(bullet);
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;

        if (isHurt && hurtAnima != null) {
            currentFrame = hurtAnima.getKeyFrame(hurtAnimaTimer, false);
        } else if (isMoving) {
            currentFrame = walkAnima.getKeyFrame(animaTimer, true);
        } else {
            currentFrame = idleAnima.getKeyFrame(animaTimer, true);
        }

        if ((isFacingRight && currentFrame.isFlipX()) || (!isFacingRight && !currentFrame.isFlipX())) {
            currentFrame.flip(true, false);
        }
        batch.draw(currentFrame, getX(), getY());
    }

    private void calculateNewPosition() {
        float moveDistance = 70;
        float randomAngle = (float) (Math.random() * 2 * Math.PI);
        targetX = getX() + (float) (Math.cos(randomAngle) * moveDistance);
        targetY = getY() + (float) (Math.sin(randomAngle) * moveDistance);


    }
}
