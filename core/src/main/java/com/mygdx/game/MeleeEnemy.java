package com.mygdx.game;



public class MeleeEnemy extends Enemy {
    public MeleeEnemy(String texturePath, float startX, float startY, float speed, int health) {
        super(texturePath, startX, startY, speed, health);
    }

    @Override
    public void update(float deltaTime, float playerX, float playerY) {
        super.update(deltaTime, playerX, playerY);
        // 近战敌人的行为在这里添加
    }
}
