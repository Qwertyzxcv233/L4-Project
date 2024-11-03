package com.mygdx.game;

public class RangedEnemy extends Enemy {
    private float attackRange;
    private float timeSinceLastAttack = 0f;

    public RangedEnemy(String texturePath, float startX, float startY, float speed, int health, float attackRange) {
        super(texturePath, startX, startY, speed, health);
        this.attackRange = attackRange;
    }

    @Override
    public void update(float deltaTime, float playerX, float playerY) {
        float distance = (float) Math.sqrt((playerX - x) * (playerX - x) + (playerY - y) * (playerY - y));

        // 检查距离，确保敌人在攻击范围外移动，否则执行攻击
        if (distance > attackRange) {
            super.update(deltaTime, playerX, playerY); // 调用移动逻辑
        } else {
            attackPlayerIfPossible(deltaTime, playerX, playerY); // 在范围内进行攻击
        }
    }

    private void attackPlayerIfPossible(float deltaTime, float playerX, float playerY) {
        timeSinceLastAttack += deltaTime; // 累加冷却时间
        if (timeSinceLastAttack >= attackCooldown) {
            fireBullet(playerX, playerY); // 执行远程攻击
            timeSinceLastAttack = 0; // 重置冷却时间
        }
    }

    private void fireBullet(float targetX, float targetY) {
        // 创建一个新子弹对象
        System.out.println("Monster Fire");
        Bullet bullet = new Bullet("bullet.png", x, y, targetX, targetY, 500f);
        GDXGame.addBullet(bullet); // 将子弹添加到游戏的子弹列表中
    }
}
