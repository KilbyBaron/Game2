package com.kilby.game;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class SurfaceView extends GLSurfaceView{

    GameManager gm;
    private InputController ic;
    private SoundManager sm;

    public SurfaceView(Context context, int screenX, int screenY) {
        super(context);
        sm = new SoundManager();
        sm.loadSound(context);
        gm = new GameManager(screenX, screenY);
        ic = new InputController(screenX, screenY);
        // Which version of OpenGl we are using
        setEGLContextClientVersion(2);

        // Attach our renderer to the GLSurfaceView
        setRenderer(new GameRenderer(gm, sm, ic));

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        ic.handleInput(motionEvent, gm, sm);
        return true;
    }

}