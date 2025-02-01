package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;

public class Item {
    private Texture texture;
    private float x, y;
    private Circle boundingCircle;
    private boolean isCollected;

    public Item(String texturePath, float x, float y) {

        this.texture = GameAssetManager.getInstance().get(texturePath, Texture.class);
        this.x = x;
        this.y = y;
        this.boundingCircle = new Circle(x + texture.getWidth() / 4, y + texture.getHeight() / 4, Math.min(texture.getWidth(), texture.getHeight()) / 4);
        this.isCollected = false;
    }

    public void render(SpriteBatch batch) {
        if (!isCollected) {
            batch.draw(texture, x, y);
        }
    }

    public void update(float delta, Player player) {
        if (!isCollected && boundingCircle.overlaps(player.getBoundingCircle())) {
            onCollect(player);
            isCollected = true;
            dispose();
        }
    }


    public void onCollect(Player player) {
        isCollected = true;
        System.out.println("Item collected!");
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void dispose() {

    }

    public Circle getBoundingCircle() {
        return boundingCircle;
    }
}
