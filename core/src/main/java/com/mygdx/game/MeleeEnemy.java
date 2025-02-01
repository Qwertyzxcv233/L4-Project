package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.Random;

public class MeleeEnemy extends Enemy {
    private boolean isDashing = false;
    private boolean isStunned = false;  // 冲刺后原地停顿状态
    private float dashTimer = 0;
    private float stunTimer = 0;
    private final float DASH_DURATION = 0.6f;
    private final float DASH_SPEED_MULTIPLIER = 2.5f;
    private final float DASH_COOLDOWN = 2.0f;
    private final float STUN_DURATION = 2.0f;
    private float dashCooldownTimer = 0;
    private float dashPreDelay = 0;
    private final float DASH_PRE_DELAY_TIME = 0.1f;
    private Random random = new Random();
    private float strafeTimer = 0;
    private Animation<TextureRegion> stunAnimation;
    private Animation<TextureRegion> hurtAnimation;
    private boolean isHurt = false;
    private float hurtAnimationTimer = 0f;
    private final float HURT_ANIMATION_DURATION = 0.4f;



    public MeleeEnemy(Texture texture, float startX, float startY, float speed, int health) {
        super(texture, startX, startY, 50f, health);
        initializeAnimation();
    }

    private void initializeAnimation() {
        TextureRegion[][] frames = TextureRegion.split(this.texture, 32, 32);
        TextureRegion[] walkFrames = new TextureRegion[6];//6帧
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


        walkAnimation = new Animation<>(0.2f, walkFrames);
        stunAnimation = new Animation<>(0.2f, stunFrames);
        hurtAnimation = new Animation<>(0.2f, hurtFrames);

    }

    @Override
    public void update(float deltaTime, float playerX, float playerY) {
        float distance = (float) Math.sqrt((playerX - getX()) * (playerX - getX()) + (playerY - getY()) * (playerY - getY()));

        if (isStunned) {
            stunTimer -= deltaTime;
            if (stunTimer <= 0) {
                isStunned = false;
            }
            isMoving = false;
            animationTimer += deltaTime;
            super.update(deltaTime, playerX, playerY);
            return;
        }

        if (isHurt) {
            hurtAnimationTimer -= deltaTime;
            if (hurtAnimationTimer <= 0) {
                isHurt = false;
            }
            return;
        }


        dashCooldownTimer -= deltaTime;


        if (distance < 30 && dashCooldownTimer <= 0 && !isDashing && dashPreDelay <= 0) {
            dashPreDelay = DASH_PRE_DELAY_TIME;
        }

        if (dashPreDelay > 0) {
            dashPreDelay -= deltaTime;
            if (dashPreDelay <= 0) {
                isDashing = true;
                dashTimer = DASH_DURATION;
                dashCooldownTimer = DASH_COOLDOWN;
            }
        }

        if (isDashing) {
            dashTimer -= deltaTime;
            if (dashTimer <= 0) {
                isDashing = false;
                isStunned = true;
                stunTimer = STUN_DURATION;
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

        float modifiedSpeed = isDashing ? getSpeed() * DASH_SPEED_MULTIPLIER : getSpeed();
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
        hurtAnimationTimer = HURT_ANIMATION_DURATION;
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;

        if (isHurt && hurtAnimation != null) {
            currentFrame = hurtAnimation.getKeyFrame(hurtAnimationTimer, false);
        } else if (isStunned && stunAnimation != null) {
            currentFrame = stunAnimation.getKeyFrame(animationTimer, true);
        } else if (isMoving && walkAnimation != null) {
            currentFrame = walkAnimation.getKeyFrame(animationTimer, true);
        } else if (idleAnimation != null) {
            currentFrame = idleAnimation.getKeyFrame(animationTimer, true);
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
