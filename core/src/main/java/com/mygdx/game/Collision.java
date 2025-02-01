package com.mygdx.game;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

public class Collision {

    private Room currentRoom;
    public Collision(Room room) {
        this.currentRoom = room;
    }
    public void setRoom(Room room) {
        this.currentRoom = room;
    }
    private int collisionFrameCounter = 0;


    public void updateCollisions(List<Bullet> bullets, List<Enemy> enemies, Player player) {
        handlePlayerCollision(player);

        for (Bullet bullet : bullets) {
            if (bullet.isActive()) {
                handleBulletCollision(bullet, player);
            }
        }

        handleBulletEnemyCollision(bullets, enemies);

        for (Enemy enemy : enemies) {
            handleEnemyCollision(enemy);
        }

        handleEnemyAvoidance(enemies);
    }

    public boolean isCollidingWithWalls(Circle circle) {
        for (Rectangle rect : currentRoom.getCollisionRectangles()) {
            if (Intersector.overlaps(circle, rect)) {
                return true;
            }
        }
        return false;
    }

    public void handlePlayerCollision(Player player) {
        if (isCollidingWithWalls(player.getBoundingCircle())) {
            player.preventMovement();
        }
    }

    public void handleEnemyCollision(Enemy enemy) {
        if (isCollidingWithWalls(enemy.getBoundingCircle())) {
            enemy.preventMovement();
        }
    }

    public void handleBulletCollision(Bullet bullet, Player player) {
        if (isCollidingWithWalls(bullet.getBoundingCircle())) {
            bullet.deactivate();
        } else if (bullet.isEnemyBullet() && Intersector.overlaps(bullet.getBoundingCircle(), player.getBoundingCircle())) {
            player.takeDamage(bullet.getDamage());
            bullet.deactivate();
        }
    }

    public void handleBulletEnemyCollision(List<Bullet> bullets, List<Enemy> enemies) {
        collisionFrameCounter++;
        if (collisionFrameCounter % 3 != 0) return;  // **每3帧检测一次**

        for (Bullet bullet : bullets) {
            if (!bullet.isActive()) continue;

            for (Enemy enemy : enemies) {
                if (!enemy.isAlive()) continue;
                if (Intersector.overlaps(bullet.getBoundingCircle(), enemy.getBoundingCircle())) {
                    enemy.takeDamage(bullet.getDamage());
                    bullet.deactivate();
                    break;
                }
            }
        }
    }

    private void handleEnemyAvoidance(List<Enemy> enemies) {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e1 = enemies.get(i);
            for (int j = i + 1; j < enemies.size(); j++) {
                Enemy e2 = enemies.get(j);

                if (e1 == e2 || !e1.isAlive() || !e2.isAlive()) {
                    continue;
                }

                Circle c1 = e1.getBoundingCircle();
                Circle c2 = e2.getBoundingCircle();

                if (Intersector.overlaps(c1, c2)) {
                    resolveEnemyCollision(e1, e2);
                }
            }
        }
    }

    public void resolveEnemyCollision(Enemy e1, Enemy e2) {
        float dx = e2.getX() - e1.getX();
        float dy = e2.getY() - e1.getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) {
            distance = 0.01f; // 避免除以零
        }

        float overlap = e1.getBoundingCircle().radius + e2.getBoundingCircle().radius - distance;

        if (overlap > 0) {
            float pushAmount = overlap / 2;
            float moveX = (dx / distance) * pushAmount;
            float moveY = (dy / distance) * pushAmount;

            e1.setX(e1.getX() - moveX);
            e1.setY(e1.getY() - moveY);
            e2.setX(e2.getX() + moveX);
            e2.setY(e2.getY() + moveY);
        }
    }
}
