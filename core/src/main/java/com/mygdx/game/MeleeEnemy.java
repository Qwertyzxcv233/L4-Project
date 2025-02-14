package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.Random;

public class MeleeEnemy extends Enemy {
    private boolean isDashing = false;
    private boolean isStun = false;
    private float dashTimer = 0;
    private float stunTimer = 0;
    private float dashDur = 0.6f;
    private float dashAcc = 2.5f;
    private float dashCD = 2.0f;
    private float stunDur = 2.0f;
    private float dashCDTimer = 0;
    private float dashPreDelay = 0;
    private float dashPreDelayTime = 0.1f;
    private Random random = new Random();
    private float strafeTimer = 0;
    private Animation<TextureRegion> stunAnima;
    private Animation<TextureRegion> hurtAnima;
    private boolean isHurt = false;
    private float hurtAnimaTimer = 0f;
    private float hurtAnimaDur = 0.4f;



    public MeleeEnemy(Texture texture, float startX, float startY, float speed, int health) {
        super(texture, startX, startY, 60f, health);
        initializeAnimation();
    }

    private void initializeAnimation() {
        TextureRegion[][] frames = TextureRegion.split(this.texture, 32, 32);
        TextureRegion[] walkFrames = new TextureRegion[6];
        TextureRegion[] stunFrames = new TextureRegion[3];
        TextureRegion[] hurtFrames = new TextureRegion[2];

        for (int i = 0; i < 6; i++) {
            walkFrames[i] = frames[5][i];
        }

        for (int i = 0; i < 3; i++) {
            stunFrames[i] = frames[2][i];
        }

        for (int i = 0; i < 2; i++) {
            hurtFrames[i] = frames[9][i];
        }


        walkAnima = new Animation<>(0.2f, walkFrames);
        stunAnima = new Animation<>(0.2f, stunFrames);
        hurtAnima = new Animation<>(0.1f, hurtFrames);

    }

    @Override
    public void update(float deltaTime, float playerX, float playerY) {
        float distance = (float) Math.sqrt((playerX - getX()) * (playerX - getX()) + (playerY - getY()) * (playerY - getY()));
        if (isHurt) {
            hurtAnimaTimer -= deltaTime;
            if (hurtAnimaTimer <= 0) {
                isHurt = false;
            }
            return;
        }
        if (isStun) {
            stunTimer -= deltaTime;
            if (stunTimer <= 0) {
                isStun = false;
            }
            isMoving = false;
            animaTimer += deltaTime;
            super.update(deltaTime, playerX, playerY);
            return;
        }




        dashCDTimer -= deltaTime;


        if (distance < 30 && dashCDTimer <= 0 && !isDashing && dashPreDelay <= 0) {
            dashPreDelay = dashPreDelayTime;
        }

        if (dashPreDelay > 0) {
            dashPreDelay -= deltaTime;
            if (dashPreDelay <= 0) {
                isDashing = true;
                dashTimer = dashDur;
                dashCDTimer = dashCD;
            }
        }

        if (isDashing) {
            dashTimer -= deltaTime;
            if (dashTimer <= 0) {
                isDashing = false;
                isStun = true;
                stunTimer = stunDur;
                return;
            }
        }


        if (distance > 100) {
            strafeTimer += deltaTime;
            if (strafeTimer > 0.5f) {
                float randomX = random.nextBoolean() ? 1 : -1;
                setX(getX() + randomX * 50 * deltaTime);
                strafeTimer = 0;
            }
        }

        float modifiedSpeed = isDashing ? getSpeed() * dashAcc : getSpeed();
        moveTowardsPlayer(playerX, playerY, deltaTime, modifiedSpeed);
        super.update(deltaTime, playerX, playerY);
    }

    private void moveTowardsPlayer(float playerX, float playerY, float deltaTime, float modifiedSpeed) {
        float directionX = playerX - getX();
        float directionY = playerY - getY();
        float length = (float) Math.sqrt(directionX * directionX + directionY * directionY);

        if (length != 0) {
            directionX /= length;
            directionY /= length;
            setX(getX() + directionX * modifiedSpeed * deltaTime);
            setY(getY() + directionY * modifiedSpeed * deltaTime);
        }
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        isHurt = true;
        hurtAnimaTimer = hurtAnimaDur;
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;

        if (isHurt && hurtAnima != null) {
            currentFrame = hurtAnima.getKeyFrame(hurtAnimaTimer, false);
        } else if (isStun && stunAnima != null) {
            currentFrame = stunAnima.getKeyFrame(animaTimer, true);
        } else if (isMoving && walkAnima != null) {
            currentFrame = walkAnima.getKeyFrame(animaTimer, true);
        } else if (idleAnima != null) {
            currentFrame = idleAnima.getKeyFrame(animaTimer, true);
        } else {
            batch.draw(texture, getX(), getY());
            return;
        }

        if ((isFacingRight && currentFrame.isFlipX()) || (!isFacingRight && !currentFrame.isFlipX())) {
            currentFrame.flip(true, false);
        }

        batch.draw(currentFrame, getX(), getY());
    }
}
