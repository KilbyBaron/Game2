package com.kilby.game;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.orthoM;

public class GameRenderer implements GLSurfaceView.Renderer {

    // Are we debugging at the moment

    boolean debugging = true;

    // For monitoring and controlling the frames per second

    long frameCounter = 0;
    long averageFPS = 0;
    private long fps;

    // For converting each game world coordinate
    // into a GL space coordinate (-1,-1 to 1,1)
    // for drawing on the screen

    private final float[] viewportMatrix = new float[16];

    // A class to help manage our game objects
    // current state.

    private GameManager gm;
    private InputController ic;
    private SoundManager sm;


    // For capturing various PointF details without
    // creating new objects in the speed critical areas

    PointF handyPointF;
    PointF handyPointF2;

    public GameRenderer(GameManager gameManager,
                        SoundManager soundManager, InputController inputController) {

        gm = gameManager;
        sm = soundManager;
        ic = inputController;

        handyPointF = new PointF();
        handyPointF2 = new PointF();

    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

        // The color that will be used to clear the
        // screen each frame in onDrawFrame()
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Get GLManager to compile and link the shaders into an object
        GLManager.buildProgram();

        createObjects();

    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {

        // Make full screen
        glViewport(0, 0, width, height);

        /*
            Initialize our viewport matrix by passing in the starting
            range of the game world that will be mapped, by OpenGL to
            the screen. We will dynamically amend this as the player
            moves around.

            The arguments to setup the viewport matrix:
            our array,
            starting index in array,
            min x, max x,
            min y, max y,
            min z, max z)
        */

        orthoM(viewportMatrix, 0, 0,
                gm.metresToShowX, 0,
                gm.metresToShowY, 0f, 1f);
    }

    private void createObjects() {
        // Create our game objects

        //-------------------------------------------
        // Box Breaker vars
        //-------------------------------------------

        int w = gm.screenWidth/4;
        int h = gm.screenHeight/4;
        float[] shapeVerts =  new float[]{
                -w/2,h/2,0,
                w/2,h/2,0,

                w/2,h/2,0,
                w/2,-h/2,0,

                w/2,-h/2,0,
                -w/2,-h/2,0,

                -w/2,-h/2,0,
                -w/2,h/2,0,
        };

        gm.shape = new Shape3( gm.screenWidth/2, gm.screenHeight/2, shapeVerts);

        gm.ball = new Ball(20,gm.screenWidth/2, 30);

        for (int i=0;i<gm.boxes.length;i++){
            gm.boxes[i] = new Box(gm.mapWidth, gm.mapHeight, -10,-10);
        }

        //-------------------------------------------


        // Some stars
        gm.stars = new Star[gm.numStars];
        for (int i = 0; i < gm.numStars; i++) {

            // Pass in the map size so the stars no where to spawn
            gm.stars[i] = new Star(gm.mapWidth, gm.mapHeight);
        }

    }

    @Override
    public void onDrawFrame(GL10 glUnused) {

        long startFrameTime = System.currentTimeMillis();

        if (gm.isPlaying()) {
            update(fps);
        }

        draw();

        // Calculate the fps this frame
        // We can then use the result to
        // time animations and more.
        long timeThisFrame = System.currentTimeMillis() - startFrameTime;
        if (timeThisFrame >= 1) {
            fps = 1000 / timeThisFrame;
        }

        // Output the average frames per second to the console
        if (debugging) {
            frameCounter++;
            averageFPS = averageFPS + fps;
            if (frameCounter > 100) {
                averageFPS = averageFPS / frameCounter;
                frameCounter = 0;
                //Log.e("averageFPS:", "" + averageFPS);
            }
        }
    }

    private void update(long fps) {

        // All objects are in their new locations
        // Start collision detection


        for (Box box : gm.boxes){
            box.update(fps);
            gm.boundaryCheck(box);

        }

        gm.boundaryCheck(gm.ball);
        if (gm.shape.checkInside(gm.ball.worldLocation.x - gm.shape.worldLocation.x, gm.ball.worldLocation.y- gm.shape.worldLocation.y)){
            for (Box box : gm.boxes){
                box.hit(gm.ball.worldLocation);
            }
            gm.shape.hit(gm.ball.worldLocation, gm.ball.blastRadius);
            gm.ball.reset(gm.screenWidth/2, 30);


        }

        gm.ball.update(fps);

    }

    private void draw() {

        // Where is the ship?
        //handyPointF = gm.ship.getWorldLocation();

        //DO NOT CENTRE ON SHIP (NEW CODE)
        handyPointF = gm.getCenterScreen();

        // Modify the viewport matrix orthographic projection
        // based on the ship location
        orthoM(viewportMatrix, 0,
                handyPointF.x - gm.metresToShowX / 2,
                handyPointF.x + gm.metresToShowX / 2,
                handyPointF.y - gm.metresToShowY / 2,
                handyPointF.y + gm.metresToShowY / 2,
                0f, 1f);

        // Clear the screen
        glClear(GL_COLOR_BUFFER_BIT);

        // Start drawing!

        // Some stars

        for (int i = 0; i < gm.numStars; i++) {

            // Draw the star if it is active
            if(gm.stars[i].isActive()) {
                gm.stars[i].draw(viewportMatrix);
            }
        }


        gm.shape.draw(viewportMatrix);
        gm.ball.draw(viewportMatrix);

        for (Box box : gm.boxes){
            box.draw(viewportMatrix);
        }
    }

}// End class


