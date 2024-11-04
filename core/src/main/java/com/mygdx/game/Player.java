package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    private Texture texture;
    private float x, y;
    private float speed;
    private int health;
    private int attackPower;
    private Weapon weapon;
    private boolean isAlive;


    public Player(String texturePath, float startX, float startY, float speed, int health, int attackPower) {
        this.texture = new Texture(texturePath);
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.health = health;
        this.attackPower = attackPower;
        this.weapon = new Gun(1.0f, 20); // 给玩家初始化装备一把手枪
        this.isAlive = true;
    }

    public void update(float delta) {
        if (!isAlive) return;
        Input(delta);
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            weapon.attack(x, y, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        }
    }

    private void Input(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += speed * delta;
        }
    }

    public void render(SpriteBatch batch) {
        if (isAlive) {
            batch.draw(texture, x, y);
        }
    }

    public void takeDamage(int damage) {
        if (!isAlive) return;
        health -= damage;
        System.out.println("Player Health: " + health);
        if (health <= 0) {
            health = 0;
            isAlive = false;
            System.out.println("Player is dead!");
        }
    }

    public void dispose() {
        texture.dispose();
    }

    // Getters and setters for player attributes
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY(){
        return this.y;
    }
    public void setY(float y){
        this.y = y;
    }
    public boolean isAlive() {
        return isAlive;
    }
    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

}
