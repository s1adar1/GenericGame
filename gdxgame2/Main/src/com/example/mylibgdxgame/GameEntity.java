package com.example.mylibgdxgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created with IntelliJ IDEA.
 * User: sd
 * Date: 26.05.13
 * Time: 00:08
 */
public abstract class GameEntity {

    private Texture image;
    private Sprite sprite;
    private Body body;
    private int hitPoints;
    private World world;
    private ParticleEffect particleEffect;
    private boolean isAI;

    public GameEntity(String imagePath, int size, float startX, float startY, float rotation, World world, boolean isAI){
        image = new Texture(Gdx.files.internal(imagePath));
        sprite = new Sprite(image);
        sprite.setSize(size, size);
        sprite.setPosition(startX, startY);
        sprite.setRotation(rotation);

        this.hitPoints = 10;
        this.world = world;
        this.isAI = isAI;
    }

    public Sprite getSprite(){
        return sprite;
    }

    public Body getBody(){
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void createBody(World world) { /* */  }


    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public void reduceHitPoints(int reduceBy){
        hitPoints -= reduceBy;
    }

    public ParticleEffect getParticleEffect() {
        return particleEffect;
    }

    public void setParticleEffect(ParticleEffect particleEffect) {
        this.particleEffect = particleEffect;
    }

    public World getWorld() {
        return world;
    }

    public boolean isAI() {
        return isAI;
    }
}
