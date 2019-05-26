package com.example.adityaabhyankar.flipnoteanimator;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity {

    PlaySurface playSurface;
    Bundle bundle;
    int size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playSurface = new PlaySurface(this);
        setContentView(playSurface);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        bundle = getIntent().getBundleExtra("PATHBUNDLE");
        size = getIntent().getIntExtra("SIZE", 0);
    }

    public class PlaySurface extends SurfaceView implements Runnable {

        SurfaceHolder holder;
        Paint paintProperty;
        volatile boolean running = false;
        Thread playThread;

        int currentFrame = 0;

        public PlaySurface(Context context) {
            super(context);

            holder = getHolder();
            paintProperty= new Paint();
            paintProperty.setStrokeWidth(10);
            paintProperty.setTextSize(30);
            paintProperty.setStyle(Paint.Style.STROKE);
            paintProperty.setStrokeJoin(Paint.Join.ROUND);
            paintProperty.setStrokeCap(Paint.Cap.ROUND);

            SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    running = true;
                    playThread = new Thread(PlaySurface.this);
                    playThread.start();

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }
            };

            holder.addCallback(callback);
        }

        @Override
        public void run() {
            while (running == true) {
                if (holder.getSurface().isValid() == false)
                    continue;

                Canvas canvas = holder.lockCanvas();

                canvas.drawRGB(255, 255, 255);

                ArrayList<String> strings = bundle.getStringArrayList("PATHDATA"+currentFrame);
                Path path = new Path();

                for (String string : strings) {
                    int firstNumEndIndex = string.indexOf(' ', 2);
                    int x = Integer.parseInt(string.substring(2, firstNumEndIndex));
                    int y = Integer.parseInt(string.substring(firstNumEndIndex+1));
                    if (string.charAt(0)=='m') {
                        path.moveTo(x,y);
                    } else if (string.charAt(0)=='l') {
                        path.lineTo(x,y);
                    }
                }

                canvas.drawPath(path, paintProperty);

                holder.unlockCanvasAndPost(canvas);

                try {
                    playThread.sleep(100);
                    currentFrame = (currentFrame + 1)%size;
                } catch (InterruptedException e) {}
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
    }
}
