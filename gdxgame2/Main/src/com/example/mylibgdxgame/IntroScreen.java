package com.example.mylibgdxgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created with IntelliJ IDEA.
 * User: sd
 * Date: 05.04.13
 * Time: 19:55
 * To change this template use File | Settings | File Templates.
 */
public class IntroScreen implements Screen {

    private SpriteBatch spriteBatch;
    private Texture splsh;
    private Game game;

    public IntroScreen(Game game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        spriteBatch.draw(splsh, 0, 0);
        spriteBatch.end();

        if(Gdx.input.justTouched())
            game.setScreen(new GameScreen());
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        splsh = new Texture(Gdx.files.internal("introscreen.png"));
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
