package com.mygdx.game;

public abstract class Weapon {
    protected float attackRate;
    protected int damage;

    public Weapon(float attackRate, int damage) {
        this.attackRate = attackRate;
        this.damage = damage;
    }

    public abstract void attack(float startX, float startY, float targetX, float targetY);
}
