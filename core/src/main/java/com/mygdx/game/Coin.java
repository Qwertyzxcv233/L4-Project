package com.mygdx.game;


public class Coin extends Item {
    private int value;

    public Coin(float x, float y) {
        super("coin.png", x, y);
        this.value = 10;
    }

    @Override
    public void onCollect(Player player) {
        player.addCoins(value);
        System.out.println("You collected " + value + " coins. Total coins: " + player.getCoins());

    }
}
