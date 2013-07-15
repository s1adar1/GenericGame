package com.example.mylibgdxgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Created with IntelliJ IDEA.
 * User: sd
 * Date: 23.05.13
 * Time: 18:58
 */
public class GameUtils {

    public static final int HEIGHT = 480;
    public static final int WIDTH = 800;

    public static final float BOX2D_TO_SCREEN_PIXELS_FACTOR = 25f;
    public static final float SCREEN_PIXELS_TO_BOX2D_FACTOR = 1/BOX2D_TO_SCREEN_PIXELS_FACTOR;

    private static final float WORLD_WIDTH = WIDTH * SCREEN_PIXELS_TO_BOX2D_FACTOR;
    private static final float WORLD_HEIGHT = HEIGHT * SCREEN_PIXELS_TO_BOX2D_FACTOR;

    public static final Sound DROP_SOUND = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
    public static final Sound GUN_SOUND = Gdx.audio.newSound(Gdx.files.internal("gunshot.wav"));
    public static Music rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

}
