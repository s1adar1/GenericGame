package com.example.mylibgdxgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created with IntelliJ IDEA.
 * User: sd
 * Date: 06.05.13
 * Time: 19:07
 */
public class Crate {

    private Texture crateImage;
    private Sprite crateSprite;
    private Vector3 direction;
    private Body body;


    public Crate(String imagePath, int size, float startX, float startY){
        crateImage = new Texture(Gdx.files.internal(imagePath));
        crateSprite = new Sprite(crateImage);
        crateSprite.setSize(size, size);
        crateSprite.setPosition(startX, startY);
        crateSprite.rotate90(true);
        this.direction = new Vector3();
    }

    public Vector3 getDirection(){
        return direction;
    }

    public void setDirection(Vector3 direction) {
        this.direction = direction;
    }

    public void setDirectionX(float x){
        this.direction.x = x;
    }

    public void setDirectionY(float y){
        this.direction.y = y;
    }

    public Sprite getCrateSprite(){
        return crateSprite;
    }

    public void setStartPosition(float x, float y){
        crateSprite.setPosition(x, y);
    }

    public void updatePosition(){
        float bulletX = crateSprite.getX() + direction.x * 400 * Gdx.graphics.getDeltaTime();
        float bulletY = crateSprite.getY() + direction.y * 400 * Gdx.graphics.getDeltaTime();
        crateSprite.setPosition(bulletX, bulletY);

    }

    public void updatePositionFromBody(){
        Vector2 position = body.getPosition();
        crateSprite.setPosition(position.x * GameScreen.BOX2D_TO_SCREEN_PIXELS_FACTOR - crateSprite.getWidth()/2,
                                position.y * GameScreen.BOX2D_TO_SCREEN_PIXELS_FACTOR - crateSprite.getHeight()/2);
    }

    public Body getBody(){
        return body;
    }

    public void createBody(World world) {

        //body definition
        BodyDef bd = new BodyDef();
        bd.position.set(crateSprite.getX() * GameScreen.SCREEN_PIXELS_TO_BOX2D_FACTOR, crateSprite.getY() * GameScreen.SCREEN_PIXELS_TO_BOX2D_FACTOR);
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.allowSleep = true;
        bd.linearDamping = 0.7f;
        bd.fixedRotation = true;

        PolygonShape ps = new PolygonShape();
        ps.setAsBox(crateSprite.getWidth()/2* GameScreen.SCREEN_PIXELS_TO_BOX2D_FACTOR, crateSprite.getWidth()/2 * GameScreen.SCREEN_PIXELS_TO_BOX2D_FACTOR);

        //define fixture of the body.
        FixtureDef fd = new FixtureDef();
        fd.shape = ps;
        fd.density = 0.9f;
        fd.friction = 0.7f;
        fd.restitution = 0.4f;

        fd.filter.categoryBits = 1;
        fd.filter.maskBits = 0x3;

        //create the body and add fixture to it
        body =  world.createBody(bd);
        body.createFixture(fd);

    }

}
