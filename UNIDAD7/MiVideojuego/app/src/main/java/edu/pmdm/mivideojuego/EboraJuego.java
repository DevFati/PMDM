package edu.pmdm.mivideojuego;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EboraJuego extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    public int max=0;


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

        getHolder().addCallback(this);

    }

    public void renderizar(Canvas canvas){
        if(canvas!=null){
            Paint mypaint=new Paint();
        }
    }
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
}