package com.mygdx.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class GameAssetManager {
    private static GameAssetManager instance;
    private AssetManager manager;

    private GameAssetManager() {
        manager = new AssetManager();
    }

    public static GameAssetManager getInstance() {
        if (instance == null) {
            instance = new GameAssetManager();
        }
        return instance;
    }

    public void loadGameAssets() {
        // Load textures
        manager.load("Player.png", Texture.class);
        manager.load("slime.png", Texture.class);
        manager.load("Skeleton.png", Texture.class);
        manager.load("bullet.png", Texture.class);
        manager.load("coin.png", Texture.class);
        manager.load("heart.png", Texture.class);
        manager.load("x.png", Texture.class);
        // Load sounds
        manager.load("shoot.mp3", Sound.class);
        manager.load("hurt.mp3", Sound.class);

        // Load music
        manager.load("bgm.mp3", Music.class);

        // Wait until all assets are loaded
        manager.finishLoading();
    }

    public void disposeAll() {
        manager.dispose();
        instance = null;
    }

    public <T> T get(String fileName, Class<T> type) {
        return manager.get(fileName, type);
    }
}
