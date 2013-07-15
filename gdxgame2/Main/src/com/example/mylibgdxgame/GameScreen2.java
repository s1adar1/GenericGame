package com.example.mylibgdxgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;

/**
 * Created with IntelliJ IDEA.
 * User: sd
 * Date: 08.04.13
 * Time: 18:55
 * To change this template use File | Settings | File Templates.
 */
public class GameScreen2 implements Screen {

    /**
     * The time the last frame was rendered, used for throttling framerate
     */
    private long lastRender;

    private MapUtil tiledMapHelper;

    /**
     * The screen coordinates of where a drag event began, used when updating
     * the camera position.
     */
    private int lastTouchedX;
    private int lastTouchedY;

    /**
     * The screen's width and height. This may not match that computed by
     * libgdx's gdx.graphics.getWidth() / getHeight() on devices that make use
     * of on-screen menu buttons.
     */
    private int screenWidth;
    private int screenHeight;

    public GameScreen2() {
        super();

        // Defer until create() when Gdx is initialized.
        screenWidth = -1;
        screenHeight = -1;
    }

    @Override
    public void render(float delta) {
        long now = System.nanoTime();

        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.justTouched()) {
            lastTouchedX = Gdx.input.getX();
            lastTouchedY = Gdx.input.getY();
        } else if (Gdx.input.isTouched()) {
            tiledMapHelper.getCamera().position.x += lastTouchedX
                    - Gdx.input.getX();

            /**
             * Camera y is opposite of Gdx.input y, so the subtraction is
             * swapped.
             */
            tiledMapHelper.getCamera().position.y += Gdx.input.getY()
                    - lastTouchedY;

            lastTouchedX = Gdx.input.getX();
            lastTouchedY = Gdx.input.getY();
        }

        /**
         * Ensure that the camera is only showing the map, nothing outside.
         */
        if (tiledMapHelper.getCamera().position.x < screenWidth / 2) {
            tiledMapHelper.getCamera().position.x = screenWidth / 2;
        }
        if (tiledMapHelper.getCamera().position.x >= tiledMapHelper.getWidth()
                - screenWidth / 2) {
            tiledMapHelper.getCamera().position.x = tiledMapHelper.getWidth()
                    - screenWidth / 2;
        }

        if (tiledMapHelper.getCamera().position.y < screenHeight / 2) {
            tiledMapHelper.getCamera().position.y = screenHeight / 2;
        }
        if (tiledMapHelper.getCamera().position.y >= tiledMapHelper.getHeight()
                - screenHeight / 2) {
            tiledMapHelper.getCamera().position.y = tiledMapHelper.getHeight()
                    - screenHeight / 2;
        }

        tiledMapHelper.getCamera().update();

        tiledMapHelper.render();

        now = System.nanoTime();
        if (now - lastRender < 30000000) { // 30 ms, ~33FPS
            try {
                Thread.sleep(30 - (now - lastRender) / 1000000);
            } catch (InterruptedException e) {
            }
        }

        lastRender = now;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {

        /**
         * If the viewport's size is not yet known, determine it here.
         */
        if (screenWidth == -1) {
            screenWidth = Gdx.graphics.getWidth();
            screenHeight = Gdx.graphics.getHeight();
        }

        tiledMapHelper = new MapUtil();

        tiledMapHelper.setPackerDirectory("data2/packer");

        tiledMapHelper.loadMap("data2/world/level1/level.tmx");
                                                                  /*
        tiledMapHelper.setPackerDirectory("data/packer");

        tiledMapHelper.loadMap("data/world/level1/level.tmx");
                                                                       */
        tiledMapHelper.prepareCamera(screenWidth, screenHeight);

        lastRender = System.nanoTime();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
