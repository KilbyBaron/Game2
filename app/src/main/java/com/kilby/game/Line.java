package com.kilby.game;

import android.graphics.PointF;

public class Line {
    PointF v1;
    PointF v2;

    public Line(PointF a, PointF b){
        v1 = a;
        v2 = b;
    }

    public Line(){
        v1 = null;
        v2 = null;
    }
}
