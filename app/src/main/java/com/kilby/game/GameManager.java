package com.kilby.game;

import android.graphics.Point;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class GameManager {



    //-------------------------------------------
    // Box Breaker vars
    //-------------------------------------------

    Shape3 shape;
    Ball ball;
    Box[] boxes  = new Box[40];

    //-------------------------------------------

    int mapWidth = 600;
    int mapHeight = 600;
    private boolean playing = true;

    // Our first game object
    Star[] stars;
    int numStars = 200;

    int screenWidth;
    int screenHeight;

    // How many metres of our virtual world
    // we will show on screen at any time.
    int metresToShowX = 600;
    int metresToShowY = 600;

    public GameManager(int x, int y){

        screenWidth = x;
        screenHeight = y;
        mapWidth = screenWidth;
        mapHeight = screenHeight;
        metresToShowX = screenWidth;
        metresToShowY = screenHeight;

    }

    public boolean isPlaying(){
        return playing;
    }

    public PointF getCenterScreen(){
        PointF center = new PointF();
        center.x = screenWidth/2;
        center.y = screenHeight/2;
        return center;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void boundaryCheck(GameObject object){

        float x = object.worldLocation.x;;
        float y= object.worldLocation.y;

        switch (object.getType()){

            case BALL:
                if (x > screenWidth || x < 0)
                    object.setxVelocity(-1*object.getxVelocity());
                if (y > screenHeight || y < 0)
                    object.setyVelocity(-1*object.getyVelocity());
                break;

            case BOX:
                if (x > screenWidth || x < 0 || y > screenHeight || y < 0) {
                    object.deactivate();
                    object.setWorldLocation(-10,-10);
                }

                break;

        }

    };



}