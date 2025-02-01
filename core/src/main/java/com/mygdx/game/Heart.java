package com.mygdx.game;

public class Heart extends Item{
    private int healthAmount;

    public Heart(float x, float y) {
        super("heart.png", x, y);
        this.healthAmount = 10;
    }

    @Override
    public void onCollect(Player player) {
        player.addHealth(healthAmount);
        System.out.println("HP + "+ healthAmount);
    }
}
