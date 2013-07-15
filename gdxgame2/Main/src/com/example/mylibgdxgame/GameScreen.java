package com.example.mylibgdxgame;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: sd
 * Date: 05.04.13
 */
public class GameScreen implements Screen {

    SpriteBatch batch;

    Plane playerPlane;

    Plane hostilePlane;

    Set<Plane> hostilePlanes = new HashSet<Plane>();
    Set<Bullet> firedBullets = new HashSet<Bullet>();
    Set<Turret> turrets = new HashSet<Turret>();

    private MapUtil tiledMapHelper;

    private long lastRender;

    private int lastTouchedX;
    private int lastTouchedY;

    private World world;

    //Tilpasses behov. Box2d opprerer med "meter" og objekter bør være mellom 0-10 meter.
    public static float BOX2D_TO_SCREEN_PIXELS_FACTOR = 25f;
    public static float SCREEN_PIXELS_TO_BOX2D_FACTOR = 1/BOX2D_TO_SCREEN_PIXELS_FACTOR;

    public static float WIDTH = 800;
    public static  float HEIGHT = 480;
    static final float BOX_STEP=1/60f;

    private float timeStep = 1.0f / 60.f;
    private int velocityIterations = 6;
    private int positionIterations = 2;

    Box2DDebugRenderer box2DDebugRenderer;

    private Matrix4 debugProjection = new Matrix4();

    //Crate crate;
    Random random = new Random();

    Vector2 zeroVelocity;

    private Texture image;
    //private final RayHandler rayHandler;

    public GameScreen(){

        Vector2 gravity = new Vector2();
        gravity.set(0f, 0f);
        boolean doSleep = true;
        world = new World(gravity, doSleep);

        tiledMapHelper = new MapUtil();
        tiledMapHelper.setWorld(world);
        tiledMapHelper.setPackerDirectory("data2/packer");
        tiledMapHelper.loadMap("data2/world/level1/level.tmx");
        tiledMapHelper.prepareCamera((int)WIDTH, (int)HEIGHT);

        lastRender = System.nanoTime();

        // start the playback of the music immediately
        //rainMusic.setLooping(true);
        //rainMusic.play();

        batch = new SpriteBatch();

        playerPlane = new Plane("plane.png",32,80,80, 0, world, false);
        playerPlane.createBody(world);



        //spawnHostilePlane();
        //spawnHostilePlane();
        //spawnHostilePlane();
        //spawnHostilePlane();
        //spawnHostilePlane();

        spawnTurret();

        //p.load(Gdx.files.internal("particles/testexplosion"), Gdx.files.internal("explosion")); //files.internal loads from the "assets" folder
        //p.setPosition(200,200);
        //p.start();

        image = new Texture(Gdx.files.internal("flash.png"));

        box2DDebugRenderer = new Box2DDebugRenderer();

        zeroVelocity = new Vector2(0.1f,0.1f);

        world.setContactListener(getHitListener());

        RayHandler.useDiffuseLight(true);
        //rayHandler = new RayHandler(world);
        //playerPlane.setRayHandler(rayHandler);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        //updateDirection();

        renderMap();

        batch.setProjectionMatrix(tiledMapHelper.getCamera().combined);

        debugProjection.set(tiledMapHelper.getCamera().combined).scale(GameUtils.BOX2D_TO_SCREEN_PIXELS_FACTOR,
                GameUtils.BOX2D_TO_SCREEN_PIXELS_FACTOR,
                1f);
        box2DDebugRenderer.render(world, debugProjection);

        batch.begin();

        world.step(timeStep, velocityIterations, positionIterations);

        //crate.getCrateSprite().draw(batch);
        //crate.updatePositionFromBody();

        if(playerPlane != null){
            playerPlane.getSprite().draw(batch);
            playerPlane.updatePositionFromBody();
            if(lastTouchedX > 0 && lastTouchedY > 0){
            playerPlane.setHeading(new Vector2(lastTouchedX * GameUtils.SCREEN_PIXELS_TO_BOX2D_FACTOR, lastTouchedY * GameUtils.SCREEN_PIXELS_TO_BOX2D_FACTOR));
                lastTouchedX = 0;
                lastTouchedY = 0;
            }
            for(Bullet bullet : playerPlane.getFiredBullets()){
                bullet.getBulletSprite().draw(batch);
                bullet.updatePositionFromBody();
            }
        }

        for(Turret turret : turrets){
            turret.getSprite().draw(batch);
            turret.rotateTowardsPlayer(playerPlane.getBody().getPosition());
            turret.shootBulletIfPossible();
            for(Bullet bullet : turret.getFiredBullets()){
                bullet.getBulletSprite().draw(batch);
                bullet.updatePositionFromBody();
            }
        }

        Iterator<Plane> iterator = hostilePlanes.iterator();
        while(iterator.hasNext()) {
            Plane plane = iterator.next();

            if(plane.getHitPoints() <= 0){
                if(plane.getParticleEffect() == null){
                    ParticleEffect particleEffect = new ParticleEffect();
                    particleEffect.load(Gdx.files.internal("particles/explosion2"), Gdx.files.internal("particles")); //files.internal loads from the "assets" folder
                    particleEffect.setPosition(plane.getSprite().getX(),plane.getSprite().getY());
                    plane.setParticleEffect(particleEffect);
                    plane.getParticleEffect().start();
                    world.destroyBody(plane.getBody());
                }
                if(!plane.getParticleEffect().isComplete()){
                    plane.getParticleEffect().update(delta);
                    plane.getParticleEffect().draw(batch);
                } else {
                    iterator.remove();
                }
            } else {
                plane.getSprite().draw(batch);
                plane.updatePositionRelativeToBody(playerPlane.getBody().getPosition());
                plane.shootBulletIfPossible();

                for(Bullet bullet : plane.getFiredBullets()){
                    bullet.getBulletSprite().draw(batch);
                    bullet.updatePositionFromBody();
                }
            }
        }


        batch.end();

       // rayHandler.setCombinedMatrix(tiledMapHelper.getCamera().combined);
       // rayHandler.updateAndRender();

        handleInput();
        handlePlanes();

    }

    private void handlePlanes() {
                  /*
        if(playerPlane != null){
            if(playerPlane.getHitPoints() <= 0){
            world.destroyBody(playerPlane.getBody());
            playerPlane = null;
            }
        }       */

        /*
        if(hostilePlane != null && hostilePlane.getHitPoints() <= 0) {
            world.destroyBody(hostilePlane.getBody());
            hostilePlane = null;
        }   */
    }

    private void renderMap() {

        long now = System.nanoTime();

        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        /*
        if (Gdx.input.justTouched()) {
            lastTouchedX = Gdx.input.getX();
            lastTouchedY = Gdx.input.getY();
        } else if (Gdx.input.isTouched()) {
            tiledMapHelper.getCamera().position.x += lastTouchedX
                    - Gdx.input.getX();

            /**
             * Camera y is opposite of Gdx.input y, so the subtraction is
             * swapped.
             *//*
            tiledMapHelper.getCamera().position.y += Gdx.input.getY()
                    - lastTouchedY;

            lastTouchedX = Gdx.input.getX();
            lastTouchedY = Gdx.input.getY();
        }  */

        /**
         * Ensure that the camera is only showing the map, nothing outside.
         */
        if (tiledMapHelper.getCamera().position.x < WIDTH / 2) {
            tiledMapHelper.getCamera().position.x = WIDTH / 2;
        }
        if (tiledMapHelper.getCamera().position.x >= tiledMapHelper.getWidth()
                - WIDTH / 2) {
            tiledMapHelper.getCamera().position.x = tiledMapHelper.getWidth()
                    - WIDTH / 2;
        }

        if (tiledMapHelper.getCamera().position.y < HEIGHT / 2) {
            tiledMapHelper.getCamera().position.y = HEIGHT / 2;
        }
        if (tiledMapHelper.getCamera().position.y >= tiledMapHelper.getHeight()
                - HEIGHT / 2) {
            tiledMapHelper.getCamera().position.y = tiledMapHelper.getHeight()
                    - HEIGHT / 2;
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

    private void handleInput() {

        if (Gdx.input.justTouched()) {
            lastTouchedX = Gdx.input.getX();
            lastTouchedY = Gdx.input.getY();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))  playerPlane.getSprite().rotate(-3f); //playerPlane.getBody().applyAngularImpulse(-0.05f);//playerPlane.getBody().applyLinearImpulse(new Vector2(0.5f, 0), playerPlane.getBody().getPosition());// playerPlane.getPlaneSprite().rotate(-3f);
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))  playerPlane.getSprite().rotate(3f);//playerPlane.getBody().applyAngularImpulse(0.05f);//playerPlane.getPlaneSprite().rotate(3f);
        //if(Gdx.input.isKeyPressed(Input.Keys.UP))    // playerSprite.setPosition(playerSprite.getX() + playerDirection.x * 100 * Gdx.graphics.getDeltaTime(), playerSprite.getY() + playerDirection.y * 100 * Gdx.graphics.getDeltaTime());
        //if(Gdx.input.isKeyPressed(Input.Keys.DOWN))  // playerSprite.setPosition(playerSprite.getX() - playerDirection.x * 50 * Gdx.graphics.getDeltaTime(), playerSprite.getY() - playerDirection.y * 50 * Gdx.graphics.getDeltaTime());
        if(Gdx.input.isKeyPressed(Input.Keys.A)) playerPlane.shootBulletIfPossible();
    }

    private void spawnHostilePlane(){
        Plane plane = new Plane("hostilePlane.png",32,random.nextFloat() * GameUtils.WIDTH / 2,random.nextFloat() * GameUtils.HEIGHT, 0, world, true);
        plane.createBody(world);
        hostilePlanes.add(plane);
    }

    private void spawnTurret(){
        Turret turret = new Turret("turret_13.png",32,random.nextFloat() * GameUtils.WIDTH / 2,random.nextFloat() * GameUtils.HEIGHT, 0, world, true);
        turret.createBody(world);
        turrets.add(turret);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
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

    private HitListener getHitListener(){
        return new HitListener();
    }

    class HitListener implements ContactListener {
        @Override
        public void beginContact(Contact contact) {
            //todo
            System.out.println("HIT");
            Object A = contact.getFixtureA().getBody().getUserData();
            Object B = contact.getFixtureB().getBody().getUserData();

            if(A instanceof Plane && B instanceof Bullet){
                ((Plane) A).reduceHitPoints(1);
                ((Bullet) B).getShooter().getFiredBullets().remove(B);
                //firedBullets.remove(B);

            }

            else if(A instanceof Bullet && B instanceof Plane){
                ((Plane) B).reduceHitPoints(1);
                ((Bullet) A).getShooter().getFiredBullets().remove(A);
                //firedBullets.remove(A);
            }
        }

        @Override
        public void endContact(Contact contact) {
            //todo
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {
            //todo
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {
            //todo
        }
    }

}
