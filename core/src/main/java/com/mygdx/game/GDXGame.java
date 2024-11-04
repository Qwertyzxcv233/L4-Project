package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GDXGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Player player;
    private ArrayList<Enemy> enemies;
    private static ArrayList<Bullet> bullets = new ArrayList<>();
    private boolean gameOver;
    private BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        player = new Player("bucket.png", 50, 50, 300, 100, 10);
        enemies = new ArrayList<>();
        enemies.add(new MeleeEnemy("sl.png", 300, 300, 90f, 50));
        enemies.add(new RangedEnemy("sk.png", 350, 350, 75f, 40, 200f));// 移动速度（单位：像素/秒）
        gameOver = false;
        font = new BitmapFont();
    }
    @Override
    public void render() {
        // 清屏
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float delta = Gdx.graphics.getDeltaTime();
        // 更新玩家位置
        if (!gameOver) {
            // 更新玩家位置
            player.update(delta);
            // 更新敌人
            for (Enemy enemy : enemies) {
                enemy.update(delta, player.getX(), player.getY());
                enemy.attackPlayer(player);  // 让敌人攻击玩家
            }

            // 更新子弹
            for (Bullet bullet : bullets) {
                bullet.update(delta);
            }

            // 碰撞检测
            for (Bullet bullet : bullets) {
                if (bullet.isActive()) {
                    if (bullet.isEnemyBullet()) {
                        // 如果是敌人发射的子弹，则检查是否击中玩家
                        if (bullet.getBoundingBox().overlaps(player.getBoundingBox())) {
                            player.takeDamage(bullet.getDamage());
                            bullet.deactivate();
                        }
                    } else {
                        // 如果是玩家发射的子弹，则检查是否击中敌人
                        for (Enemy enemy : enemies) {
                            if (bullet.getBoundingBox().overlaps(enemy.getBoundingBox())) {
                                enemy.takeDamage(bullet.getDamage());
                                bullet.deactivate();
                                break;
                            }
                        }
                    }
                }
            }

            // 移除不活跃的子弹和死亡的敌人
            bullets.removeIf(bullet -> !bullet.isActive());
            enemies.removeIf(Enemy::isDead);

            // 检查玩家是否死亡
            if (!player.isAlive()) {
                gameOver = true;
            }
        }

        // 渲染玩家和其他游戏对象
        batch.begin();
        if (!gameOver) {
            player.render(batch);
            for (Enemy enemy : enemies) {
                enemy.render(batch);
            }
            for (Bullet bullet : bullets) {
                bullet.render(batch);
            }
        } else {
            // 显示游戏结束信息
            font.draw(batch, "Game Over! Press R to Restart", Gdx.graphics.getWidth() / 2f - 80, Gdx.graphics.getHeight() / 2f);
        }
        batch.end();

        // 处理游戏结束后的输入
        if (gameOver && Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            restart();
        }
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
        font.dispose();
    }

    public static void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    private void restart() {
        // 重新启动游戏
        player = new Player("bucket.png", 50, 50, 300, 100, 10);
        enemies = new ArrayList<>();
        enemies.add(new MeleeEnemy("sl.png", 100, 100, 100f, 50));
        enemies.add(new RangedEnemy("sk.png", 300, 300, 75f, 40, 500f));
        bullets.clear();
        gameOver = false;
    }

}




