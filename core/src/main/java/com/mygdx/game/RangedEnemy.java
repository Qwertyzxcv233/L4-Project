package com.mygdx.game;

public class RangedEnemy extends Enemy {
    private float attackRange;

    public RangedEnemy(String texturePath, float startX, float startY, float speed, int health, float attackRange) {
        super(texturePath, startX, startY, speed, health);
        this.attackRange = attackRange;
    }

    @Override
    public void update(float deltaTime, float playerX, float playerY) {
        float distance = (float) Math.sqrt((playerX - x) * (playerX - x) + (playerY - y) * (playerY - y));
        if (distance > attackRange) {
            super.update(deltaTime, playerX, playerY);
        }
        // 添加远程攻击逻辑
    }
}
