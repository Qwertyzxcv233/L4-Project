package com.mygdx.game;

public class Heart extends Item{
    private int health;

    public Heart(float x, float y) {
        super("heart.png", x, y);
        this.health = 10;
    }

    @Override
    public void onCollect(Player player) {
        player.addHealth(health);
        System.out.println("HP + "+ health);
    }
}
