package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class HUD {
    private Player player;
    private BitmapFont font;
    private Texture heartTexture;
    private Texture coinTexture;
    private OrthographicCamera camera;

    private String waveText = "";
    private float waveTextTimer = 0f;
    private final float waveDur = 3f;

    public HUD(Player player) {
        this.player = player;
        heartTexture = new Texture(Gdx.files.internal("heart.png"));
        coinTexture = new Texture(Gdx.files.internal("coin.png"));
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    }
    public void update(float delta) {
        if (waveTextTimer > 0) {
            waveTextTimer -= delta;
        }
    }

    public void showWaveMessage(int currentWave, int totalWaves) {
        waveText = "WAVE " + currentWave + "/" + totalWaves ;
        waveTextTimer = waveDur;
    }

    public void render(SpriteBatch batch) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        float heartX = 10;
        float heartY = Gdx.graphics.getHeight() - heartTexture.getHeight() - 10;
        batch.draw(heartTexture, heartX, heartY);
        font.draw(batch, String.valueOf(player.getHealth()), heartX + heartTexture.getWidth() + 5, heartY + heartTexture.getHeight()/2 + 5);
        float coinX = Gdx.graphics.getWidth() - coinTexture.getWidth() - 10;
        float coinY = Gdx.graphics.getHeight() - coinTexture.getHeight() - 10;
        batch.draw(coinTexture, coinX, coinY);
        font.draw(batch, String.valueOf(player.getCoins()), coinX - 30, coinY + coinTexture.getHeight()/2 + 5);

        if (waveTextTimer > 0) {
            float alpha;
            if (waveTextTimer > waveDur / 2) {
                alpha = 1 - (waveTextTimer - waveDur / 2) / (waveDur / 2);
            } else {
                alpha = waveTextTimer / (waveDur / 2);
            }
            Color originalColor = new Color(font.getColor());
            font.setColor(1, 1, 1, alpha);
            font.draw(batch, waveText, (Gdx.graphics.getWidth() - 200) / 2, Gdx.graphics.getHeight() / 2);
            font.setColor(originalColor);
        }
    }

    public void dispose() {
        heartTexture.dispose();
        coinTexture.dispose();
        font.dispose();
    }
}
