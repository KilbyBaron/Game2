package com.kilby.game;

import android.app.Activity;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends Activity {

    private GLSurfaceView asteroidsView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();

        // Load the resolution into a Point object
        Point resolution = new Point();
        display.getSize(resolution);

        System.out.println(resolution);

        asteroidsView = new SurfaceView
                (this, resolution.x, resolution.y);

        setContentView(asteroidsView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        asteroidsView.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

        asteroidsView.onResume();


    }
}