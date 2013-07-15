package com.example.mylibgdxgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created with IntelliJ IDEA.
 * User: sd
 * Date: 01.05.13
 * Time: 16:50
 */
public class Bullet {

    private Texture bulletImage;
    private Sprite bulletSprite;
    private Body body;
    private Armed shooter;


    public Bullet(String imagePath, int size, float startX, float startY, float rotation, Vector2 linearVelocity, Armed shooter){
        bulletImage = new Texture(Gdx.files.internal(imagePath));
        bulletSprite = new Sprite(bulletImage);
        bulletSprite.setSize(size, size);

        float x = (float) Math.cos(Math.toRadians(rotation));
        float y = (float) Math.sin(Math.toRadians(rotation));

        bulletSprite.setPosition(startX + x*50, startY + y*50);
        bulletSprite.setRotation(rotation);
        bulletSprite.rotate90(true);
        this.shooter = shooter;
    }

    public Sprite getBulletSprite(){
        return bulletSprite;
    }

    public void updatePositionFromBody(){
        Vector2 position = body.getPosition();
        System.out.println("BULLET X : "+ position.x);
        System.out.println("BULLET Y : "+ position.y);
        bulletSprite.setPosition(position.x * GameScreen.BOX2D_TO_SCREEN_PIXELS_FACTOR - bulletSprite.getWidth()/2,
                                 position.y * GameScreen.BOX2D_TO_SCREEN_PIXELS_FACTOR - bulletSprite.getHeight()/2);
    }

    public Vector2 getVelocity(){
        return body.getLinearVelocity();
    }

    public Body getBody(){
        return body;
    }

    public void createBody(World world) {

        //body definition
        BodyDef bd = new BodyDef();
        bd.position.set(bulletSprite.getX() * GameScreen.SCREEN_PIXELS_TO_BOX2D_FACTOR, bulletSprite.getY() * GameScreen.SCREEN_PIXELS_TO_BOX2D_FACTOR);
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.allowSleep = true;
        bd.bullet = true;
        bd.linearDamping = 0.2f;

        //define shape of the body.
        PolygonShape ps = new PolygonShape();
        ps.setAsBox(bulletSprite.getWidth()/2 * GameScreen.SCREEN_PIXELS_TO_BOX2D_FACTOR, bulletSprite.getHeight()/2 * GameScreen.SCREEN_PIXELS_TO_BOX2D_FACTOR);

        //define fixture of the body.
        FixtureDef fd = new FixtureDef();
        fd.shape = ps;
        fd.density = 0.5f;     //tyngde
        fd.friction = 0.3f;    // slidefactor ved kollisjon , body med lavest bestemmer
        fd.restitution = 0.1f;    //bounciness  ved kollisjon, body med lavest bestemmer


        //Basically - enhver body gies 1 katogori som er 1, 2, 4, 8, 16
        //Enhver body gies en mask bit. Bodies kolliderer bare med andre bodies
        //som har kategori-bit lik mask bit.
        //F.eks:
        //For å kollidre med denne må første bit i mask være satt.
        //F.eks: en body med mask = 0x1 vil kollidere, en body med mask 0x2 vil ikke
         fd.filter.categoryBits = 4;
         fd.filter.maskBits = 0x3;

        //create the body and add fixture to it
        body =  world.createBody(bd);
        body.createFixture(fd);
        body.setUserData(this);

        Vector2 point = body.getWorldPoint(body.getWorldCenter());

        float x = (float) Math.cos(Math.toRadians(bulletSprite.getRotation()));
        float y = (float) Math.sin(Math.toRadians(bulletSprite.getRotation()));

        body.applyLinearImpulse(x, y, bulletSprite.getX(), bulletSprite.getY());
        //body.applyForceToCenter(x, y);
    }

    public Armed getShooter() {
        return shooter;
    }
}
