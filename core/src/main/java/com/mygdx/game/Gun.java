package com.mygdx.game;

public class Gun extends Weapon{
    public Gun(float attackRate, int damage) {
        super(attackRate, damage);
    }

    @Override
    public void attack(float startX, float startY, float targetX, float targetY) {
        System.out.println("Gun fires");
        // 实例化,表示子弹发射
        Bullet bullet= new Bullet("bullet.png", startX, startY, targetX, targetY, 500f);
        GDXGame.addBullet(bullet); // 将子弹添加到管理列表中
    }

}
