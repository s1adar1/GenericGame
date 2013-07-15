package com.example.mylibgdxgame;

/**
 *   Copyright 2011 David Kirchner dpk@dpk.net
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * TiledMapHelper can simplify your game's tiled map operations. You can find
 * some sample code using this class at my blog:
 *
 * http://dpk.net/2011/05/08/libgdx-box2d-tiled-maps-full-working-example-part-2/
 *
 * Note: This code does have some limitations. It only supports single-layered
 * maps.
 *
 * This code is based on TiledMapTest.java found at:
 * http://code.google.com/p/libgdx/
 */

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.tiled.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

public class MapUtil {

    private FileHandle packFileDirectory;

    private OrthographicCamera camera;

    private TileAtlas tileAtlas;
    private TileMapRenderer tileMapRenderer;

    private TiledMap map;


    private static final int[] layersList = { 0 };
    private World world;


    /**
     * Renders the part of the map that should be visible to the user.
     */
    public void render() {
        tileMapRenderer.getProjectionMatrix().set(camera.combined);

        Vector3 tmp = new Vector3();
        tmp.set(0, 0, 0);
        camera.unproject(tmp);

        tileMapRenderer.render((int) tmp.x, (int) tmp.y,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), layersList);
    }

    /**
     * Get the height of the map in pixels
     *
     * @return y
     */
    public int getHeight() {
        return map.height * map.tileHeight;
    }

    /**
     * Get the width of the map in pixels
     *
     * @return x
     */
    public int getWidth() {
        return map.width * map.tileWidth;
    }

    /**
     * Get the map, useful for iterating over the set of tiles found within
     *
     * @return TiledMap
     */
    public TiledMap getMap() {
        return map;
    }

    /**
     * Calls dispose on all disposable resources held by this object.
     */
    public void dispose() {
        tileAtlas.dispose();
        tileMapRenderer.dispose();
    }

    /**
     * Sets the directory that holds the game's pack files and tile sets.
     *
     * @param packDirectory
     */
    public void setPackerDirectory(String packDirectory) {
        packFileDirectory = Gdx.files.internal(packDirectory);
    }

    /**
     * Loads the requested tmx map file in to the helper.
     *
     * @param tmxFile
     */
    public void loadMap(String tmxFile) {
        if (packFileDirectory == null) {
            throw new IllegalStateException("loadMap() called out of sequence");
        }

        map = TiledLoader.createMap(Gdx.files.internal(tmxFile));
        tileAtlas = new TileAtlas(map, packFileDirectory);
        tileMapRenderer = new TileMapRenderer(map, tileAtlas, 16, 16);
        //createBodies(map.layers.get(1));
    }

    public void createBodies(TiledLayer tiledLayer){
        int[][] tiles = tiledLayer.tiles;
        for (int i = 0; i < tiles.length; i++){
            for(int j = 0; j < tiles[i].length ; j++){
                if(tiles[i][j] > 0){
                    BodyDef newBodyDef = new BodyDef();
                    newBodyDef.position.set((j * 32)* GameScreen.SCREEN_PIXELS_TO_BOX2D_FACTOR,
                            (i * 32)* GameScreen.SCREEN_PIXELS_TO_BOX2D_FACTOR);
                    newBodyDef.angle = 0;
                    newBodyDef.active = true;
                    newBodyDef.allowSleep = true;
                    //  newBodyDef.angularDamping = random.nextFloat();
                    // newBodyDef.angularVelocity = random.nextFloat()*10f;
                    newBodyDef.awake = true;
                    newBodyDef.bullet = false;
                    // newBodyDef.fixedRotation = false;
                    //newBodyDef.gravityScale = 1;
                    // newBodyDef.linearDamping = random.nextFloat()*1f;
                    // newBodyDef.linearVelocity.set(0.0f, 0.0f);

                    newBodyDef.type = BodyDef.BodyType.StaticBody;
                    Body body = world.createBody(newBodyDef);

                    FixtureDef fixDef = new FixtureDef();
                    fixDef.density = 100f;  //tyngde
                    fixDef.friction = 0.9f; // slidefactor ved kollisjon , body med lavest bestemmer
                    fixDef.restitution = 0.9f; //bounciness  ved kollisjon, body med lavest bestemmer

                    //Dette bør du google - viktig for performance.
                    //Basically - enhver body gies 1 katogori som er 1, 2, 4, 8, 16
                    //Enhver body gies en mask bit. Bodies kolliderer bare med andre bodies
                    //som har kategori-bit lik mask bit.
                    //F.eks:

                    //For å kollidre med denne må første bit i mask være satt.
                    //F.eks: en body med mask = 0x1 vil kollidere, en body med mask 0x2 vil ikke
                    fixDef.filter.categoryBits = 1;
                    fixDef.filter.maskBits = 0x7FFF; //Collide with everything -> binært 11111111111...

                    PolygonShape shape = new PolygonShape();
                    fixDef.shape = shape;
                    shape.setAsBox(32 * GameScreen.SCREEN_PIXELS_TO_BOX2D_FACTOR, 32 * GameScreen.SCREEN_PIXELS_TO_BOX2D_FACTOR);
                    body.createFixture(fixDef);
                    shape.dispose();

                }
            }
        }

    }

    /**
     * Prepares the helper's camera object for use.
     *
     * @param screenWidth
     * @param screenHeight
     */
    public void prepareCamera(int screenWidth, int screenHeight) {
        camera = new OrthographicCamera(screenWidth, screenHeight);

        camera.position.set(0, 0, 0);
    }

    /**
     * Returns the camera object created for viewing the loaded map.
     *
     * @return OrthographicCamera
     */
    public OrthographicCamera getCamera() {
        if (camera == null) {
            throw new IllegalStateException(
                    "getCamera() called out of sequence");
        }
        return camera;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
