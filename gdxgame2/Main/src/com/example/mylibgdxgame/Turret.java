package com.example.mylibgdxgame;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: sd
 * Date: 25.05.13
 * Time: 23:27
 */
public class Turret extends GameEntity implements Armed {

    private Set<Bullet> firedBullets = new HashSet<Bullet>();
    private Date timeSinceLastBullet = new Date();
    private double lastAngle = 10.0;
    Vector2 target;

    private ParticleEffect particleEffect;

    public Turret(String imagePath, int size, float startX, float startY, float rotation, World world, boolean isAI){
        super(imagePath, size, startX, startY, rotation, world, isAI);
        setHitPoints(10);
    }

    public void createBody(World world) {

        //body definition
        BodyDef bd = new BodyDef();
        bd.position.set(getSprite().getX() * GameUtils.SCREEN_PIXELS_TO_BOX2D_FACTOR, getSprite().getY() * GameUtils.SCREEN_PIXELS_TO_BOX2D_FACTOR);
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.allowSleep = true;
        bd.fixedRotation = true;

        //define shape of the body.
        PolygonShape ps = new PolygonShape();
        ps.setAsBox(getSprite().getWidth()/2 * GameUtils.SCREEN_PIXELS_TO_BOX2D_FACTOR, getSprite().getHeight()/2 * GameUtils.SCREEN_PIXELS_TO_BOX2D_FACTOR);

        //define fixture of the body.
        FixtureDef fd = new FixtureDef();
        fd.shape = ps;
        fd.density = 500f;     //tyngde
        fd.friction = 0.9f;    // slidefactor ved kollisjon , body med lavest bestemmer
        fd.restitution = 0.9f;    //bounciness  ved kollisjon, body med lavest bestemmer

        fd.filter.categoryBits = 8;
        fd.filter.maskBits = 0x5;


        //create the body and add fixture to it
        setBody(world.createBody(bd));
        getBody().createFixture(fd);
        getBody().setUserData(this);

    }

    public void shootBulletIfPossible(){
        if(lastAngle < 0.05 && new Date().getTime() - timeSinceLastBullet.getTime() > 150){
            shootBullet();
        }
        handleBullets();  // clean up any bullets that are off screen
    }

    private void shootBullet() {
        Bullet bullet = new Bullet("bullet.png", 8, getSprite().getX(), getSprite().getY(), getSprite().getRotation(), getBody().getLinearVelocity(), this);
        bullet.createBody(getWorld());
        firedBullets.add(bullet);
        GameUtils.GUN_SOUND.play();
        timeSinceLastBullet = new Date();
    }

    private void handleBullets() {
        Iterator<Bullet> iterator = firedBullets.iterator();
        while(iterator.hasNext()){
            Bullet bullet = iterator.next();
            if(bullet.getBulletSprite().getX() < 0 ||
                    bullet.getBulletSprite().getX() > GameUtils.WIDTH ||
                    bullet.getBulletSprite().getY() < 0 ||
                    bullet.getBulletSprite().getY() > GameUtils.HEIGHT){
                getWorld().destroyBody(bullet.getBody());
                iterator.remove();
            }
        }
    }

    @Override
    public Set<Bullet> getFiredBullets(){
        return firedBullets;
    }

    public void rotateTowardsPlayer(Vector2 position) {
        double a, b, c;

        Vector2 steeringPoint = new Vector2();
        steeringPoint.x = getBody().getPosition().x +  10;
        steeringPoint.y = getBody().getPosition().y +  10;


        double A = Math.sqrt(Math.pow(getBody().getPosition().x - position.x, 2) +
                Math.pow(getBody().getPosition().y - position.y, 2));


        double B =  Math.sqrt(Math.pow(getBody().getPosition().x - steeringPoint.x, 2) +
                Math.pow(getBody().getPosition().y - steeringPoint.y, 2));

        double C = Math.sqrt(Math.pow(position.x - steeringPoint.x, 2) +
                Math.pow(position.y - steeringPoint.y, 2));

        if(A > B && A > C){
            a = B;
            b = C;
            c = A;
        }
        else if(C > B && C > A){
            a = B;
            b = A;
            c = C;
        }
        else {
            a = C;
            b = A;
            c = B;
        }


        double x1 = Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2);
        double x2 = 2 * a * b;
        double X = Math.acos(x1 / x2);
        double XDegrees = Math.toDegrees(X);

        double Y = (a * Math.sin(X))/c;
        double YDegrees = Math.toDegrees(Y);

        double ZDegrees = 180 - (XDegrees + YDegrees);
        double Z = Math.PI - (X + Y);

        //System.out.println("ANGLE: " + X);
        //System.out.println("ANGLE IN DEGREES  " + degrees);

        System.out.println("X = " + X );
        System.out.println("Y = " + Y );
        System.out.println("Z = " + Z );

        if(C > A && C > B){
            if(X < 2*Math.PI && X > - 2*Math.PI){
                getSprite().rotate((float) X);
                lastAngle = X;
            }
        } else {
            if(Y < 2*Math.PI && Y > - 2*Math.PI){
                if(Y  >  0.05 || Y < -0.05){
                    if(X+Y+Z > Math.PI) getSprite().rotate((float) ((float) getSprite().getY() -Y));
                    else getSprite().rotate((float) Y);
                }
                lastAngle = Y;
            }
        }
    }

    public ParticleEffect getParticleEffect() {
        return particleEffect;
    }

    public void setParticleEffect(ParticleEffect particleEffect) {
        this.particleEffect = particleEffect;
    }


}
