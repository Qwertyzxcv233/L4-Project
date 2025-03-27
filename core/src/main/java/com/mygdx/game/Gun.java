package com.mygdx.game;

public class Gun extends Weapon{
    public Gun(float attackRate, int damage) {
        super(attackRate, damage);
    }

    @Override
    public void attack(float startX, float startY, float targetX, float targetY) {
        System.out.println("Gun fires");
        Bullet bullet= new Bullet("bullet.png", startX, startY, targetX, targetY, 300f,false,damage);
        GDXGame.addBullet(bullet);
    }
    public void setDamage(int damage) {
        this.damage = damage;
    }

}

