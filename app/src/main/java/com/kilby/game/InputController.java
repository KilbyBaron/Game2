package com.kilby.game;

import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;

import java.util.ArrayList;


public class InputController {

    InputController(int screenWidth, int screenHeight) {
    }


    public void handleInput(MotionEvent motionEvent,GameManager gm,
                            SoundManager sound){

        int pointerCount = motionEvent.getPointerCount();

        for (int i = 0; i < pointerCount; i++) {
            int x = (int) motionEvent.getX(i);
            int y = (int) motionEvent.getY(i);
            ArrayList<PointF> intersections;
            switch (motionEvent.getAction() &
                    MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:

                    //NEW BLOCK BREAKER CODE
                    gm.ball.launch(x,gm.screenHeight-y);

                    /*for (Box box : gm.boxes){
                        box.worldLocation.x = -1;
                        box.worldLocation.y = -1;
                    }*/

                    //gm.shape.hit(gm.ball.worldLocation, gm.ball.radius);


                    break;

                case MotionEvent.ACTION_UP:
                    break;


                case MotionEvent.ACTION_POINTER_DOWN:
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    break;
            }
        }

    }
}