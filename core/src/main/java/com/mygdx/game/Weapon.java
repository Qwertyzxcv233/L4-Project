package com.mygdx.game;

public abstract class Weapon {
    protected float attackRate;  // 攻击频率
    protected int damage;        // 伤害

    public Weapon(float attackRate, int damage) {
        this.attackRate = attackRate;
        this.damage = damage;
    }

    public abstract void attack(float startX, float startY, float targetX, float targetY);
}
