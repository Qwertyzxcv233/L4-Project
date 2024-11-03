package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GDXGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Player player;
    private ArrayList<Enemy> enemies;
    private static ArrayList<Bullet> bullets = new ArrayList<>();

    @Override
    public void create() {
        batch = new SpriteBatch();
        player = new Player("bucket.png", 50, 50, 300, 100, 10);
        enemies = new ArrayList<>();
        enemies.add(new MeleeEnemy("Slime.png", 100, 100, 100f, 50));
        enemies.add(new RangedEnemy("Skeleton.png", 300, 300, 75f, 40, 200f));// 移动速度（单位：像素/秒）
    }

    @Override
    public void render() {
        // 清屏
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float delta = Gdx.graphics.getDeltaTime();
        // 更新玩家位置
        player.update(delta);
        // 更新敌人
        for (Enemy enemy : enemies) {
            enemy.update(delta, player.getX(), player.getY());
        }

        // 更新子弹
        for (Bullet bullet : bullets) {
            bullet.update(delta);
        }

        // 碰撞检测
        for (Bullet bullet : bullets) {
            if (bullet.isActive()) {
                for (Enemy enemy : enemies) {
                    if (bullet.getBoundingBox().overlaps(enemy.getBoundingBox())) {
                        enemy.takeDamage(player.getAttackPower());
                        bullet.deactivate();
                        break;
                    }
                }
            }
        }

        // 移除不活跃的子弹和死亡的敌人
        bullets.removeIf(bullet -> !bullet.isActive());
        enemies.removeIf(Enemy::isDead);

        // 渲染玩家
        batch.begin();
        player.render(batch);

        for (Enemy enemy : enemies) {
            enemy.render(batch);
        }
        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
        for (Bullet bullet : bullets) {
            bullet.dispose();
        }
    }

    public static void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

}




