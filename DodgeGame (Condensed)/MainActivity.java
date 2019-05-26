package com.example.adityaabhyankar.dodgegame;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Space;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    GameSurface gameSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameSurface = new GameSurface(this);
        setContentView(gameSurface);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onPause(){
        super.onPause();
        gameSurface.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        gameSurface.resume();
    }

    public class GameSurface extends SurfaceView implements Runnable, SensorEventListener {

        Thread gameThread;
        SurfaceHolder holder;

        Bitmap player;

        ArrayList<Bitmap> enemies = new ArrayList<Bitmap>();
        HashMap<Bitmap, Point> locs = new HashMap<Bitmap, Point>();

        int screenWidth, screenHeight;
        final int BUFFER;
        volatile boolean running = false;  // "volatile" makes it accessible through all threads.

        int x, y;
        int score = 0;

        long hitTime = 0;

        Paint paintProperty;

        SoundPool soundPool;
        int hitID, dodgeID, backgroundID;

        MediaPlayer playa;

        public GameSurface(Context context) {
            super(context);

            holder = getHolder();

            playa = MediaPlayer.create(MainActivity.this, R.raw.pacmantrap);
            playa.start();

            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(1);
            soundPool = builder.build();
            soundPool.play(backgroundID, 0.99f, 0.99f, 1, -1, 1);

            backgroundID = soundPool.load(MainActivity.this, R.raw.pacmantrap, 1);

            player = BitmapFactory.decodeResource(getResources(),R.drawable.pacman);

            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth=sizeOfScreen.x;
            screenHeight=sizeOfScreen.y;

            x = screenWidth/2 - player.getWidth()/2;
            BUFFER = 200;
            y = screenHeight - BUFFER - player.getHeight();

            paintProperty= new Paint();
            paintProperty.setTextSize(100);

            SensorManager manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
            Sensor mySensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            manager.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_GAME);
        }

        @Override
        public void run() {
            while (running == true){
                if (holder.getSurface().isValid() == false)
                    continue;

                //MAKE CHANGES

                if ((System.currentTimeMillis()-hitTime)>2000) {
                    player = BitmapFactory.decodeResource(getResources(), R.drawable.pacman);
                }

                if (enemies.size()<1) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ghost1 + new Random().nextInt(3));
                    enemies.add(bitmap);
                    locs.put(bitmap, new Point(new Random().nextInt(screenWidth), -bitmap.getHeight()));
                }

                /*System.out.println(coins.size()==0);
                if (coins.size()==0 || ((coins.size()>0) && locs.get(coins.get(coins.size()-2)).y>10)) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    coins.add(bitmap);
                    if (coins.size()==0)
                        locs.put(bitmap, new Point(new Random().nextInt(screenWidth), 0));
                    else locs.put(bitmap, new Point(500, 0));
                }*/

                for (int e=0;e<enemies.size();e++) {
                    Bitmap enemy = enemies.get(e);
                    int displY = 20;
                    for (int pix=1;pix<Math.abs(displY);pix++)
                        try {
                            if (isInBounds(enemy, 0, (int) Math.signum(displY)) && !didCollide(enemy)) {
                                //Log.d("YO", "is in bounds");
                                Point loc = locs.get(enemy);
                                loc.set(loc.x, loc.y + (int) Math.signum(displY));
                                locs.put(enemy, loc);
                            } else {
                                if (!isInBounds(enemy, 0, (int) Math.signum(displY)))
                                    score+=10;
                                else {
                                    hitTime = System.currentTimeMillis();
                                    player = BitmapFactory.decodeResource(getResources(), R.drawable.pacman_hurt_trans);
                                }

                                enemies.remove(enemy);
                                locs.remove(enemy);
                                e--;
                            }
                        } catch (Exception ex) {}
                }

                //END MAKE CHANGES

                Canvas canvas= holder.lockCanvas();

                //PAINT ---------------------------------------------------

                //SAMPLE CODE:
                canvas.drawRGB(0,0,255);

                for (Bitmap enemy : enemies) {
                    canvas.drawBitmap(enemy, locs.get(enemy).x, locs.get(enemy).y, null);
                }

                /*for (Bitmap coin : coins) {
                    canvas.drawBitmap(coin, locs.get(coin).x, locs.get(coin).y, null);
                }*/

                //canvas.drawBitmap( myImage,100+value,200,null);

                canvas.drawBitmap(player, x, y, null);

                canvas.drawText("Score: " + score,50,200,paintProperty);

                //END PAINT -----------------------------------------------
                holder.unlockCanvasAndPost(canvas);
            }
        }

        public void resume(){
            running=true;
            gameThread=new Thread(this);
            gameThread.start();
        }

        public void pause() {
            running = false;
            while (true) {
                try {
                    gameThread.join();
                } catch (InterruptedException e) {
                }
            }
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            int displX = -Math.round(event.values[0])*7;

            //Log.d("YOOO", isInBounds(player, displX, 0)+"");

            for (int pix=1;pix<Math.abs(displX);pix++)
                if (isInBounds(player, (int)Math.signum(displX), 0)) {
                    x+=(int)Math.signum(displX);
                }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        public boolean isInBounds(Bitmap b, int displX, int displY) {
            int tempX, tempY;
            if (b==player) {
                tempX = x + displX;
                tempY = y + displY;
            } else {
                tempX = locs.get(b).x + displX;
                tempY = locs.get(b).y + displY;
            }
            return tempX>0 && tempX+b.getWidth()<screenWidth && tempY+b.getHeight()<=screenHeight+30;
        }

        public boolean didCollide(Bitmap enemy) {

           final int WINDOW = 100;

           Rect enemyRekt = new Rect(locs.get(enemy).x+(WINDOW/2), locs.get(enemy).y+WINDOW, locs.get(enemy).x+enemy.getWidth()-(WINDOW/2), locs.get(enemy).y+enemy.getHeight()-WINDOW),
                    userRect = new Rect(x, y, x+player.getWidth(), y + player.getHeight());

           return enemyRekt.intersect(userRect);
        }
    }
}
