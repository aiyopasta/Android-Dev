package com.example.adityaabhyankar.flipnoteanimator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class Workbench extends AppCompatActivity {

    int code = 00000;

    DrawSurface currentSurface;

    ArrayList<DrawSurface> drawSurfaces = new ArrayList<DrawSurface>();

    ViewAnimator viewAnimator;
    int currentSurfaceIndex = 0;
    int totalViews = 0;

    ImageButton nextButton,
            prevButton,
            addButton,
            deleteButton,
            playButton;

    Button saveButton;

    TranslateAnimation anim2;

    SeekBar seekBar;

    SharedPreferences prefs;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 12345) {
            Toast.makeText(this, "Returned for more?", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workbench);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        prefs = this.getSharedPreferences("com.example.adityaabhyankar.flipnoteanimator", Context.MODE_PRIVATE);

        viewAnimator = (ViewAnimator)findViewById(R.id.view_animator_id);

        if (!getIntent().getBooleanExtra("NEW", false)) {

            code = getIntent().getIntExtra("RESULTCODE", -1);
            totalViews = prefs.getInt(code + "FRAMES", -1);
            for (int i=0;i<totalViews;i++) {
                DrawSurface drawSurface = new DrawSurface(this);
                drawSurfaces.add(drawSurface);
                viewAnimator.addView(drawSurface);
            }

            Iterator it = prefs.getAll().keySet().iterator();
            while (it.hasNext()) {
                String key = (String)it.next();

                if (key.contains("" + code) && !key.contains("FRAMES")) {
                    Log.d("CHARAT", key);

                    try {

                    JSONArray jsonArray = new JSONArray(prefs.getString(key, "[]"));

                    ArrayList<String> pathData = new ArrayList<>();
                    for (int i=0;i<jsonArray.length();i++) {
                        pathData.add((String)jsonArray.get(i));
                    }

                    try {
                        drawSurfaces.get(Integer.parseInt("" + key.substring(13))).pathData = pathData;
                    } catch (IndexOutOfBoundsException ex) {Log.d("OUTOFBOUNDS", ""+key);}

                    } catch (JSONException e) {}
                }
            }

        } else {
            totalViews = 1;
            for (int i=0;i<totalViews;i++) {
                DrawSurface drawSurface = new DrawSurface(this);
                drawSurfaces.add(drawSurface);
                viewAnimator.addView(drawSurface);
            }
        }

        currentSurface = drawSurfaces.get(currentSurfaceIndex);

        nextButton = (ImageButton)findViewById(R.id.next_button_id);
        prevButton = (ImageButton)findViewById(R.id.prev_button_id);

        seekBar = (SeekBar)findViewById(R.id.frame_seek_bar_id);
        seekBar.setMax(totalViews-1);
        seekBar.setProgress(0);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentSurfaceIndex = progress;
                currentSurface = drawSurfaces.get(currentSurfaceIndex);
                viewAnimator.setDisplayedChild(progress);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
        });

        addButton = (ImageButton)findViewById(R.id.imageButton2);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setMax(totalViews++);
                DrawSurface drawSurface = new DrawSurface(Workbench.this);
                drawSurfaces.add(drawSurface);
                viewAnimator.addView(drawSurface);
            }
        });

        deleteButton = (ImageButton)findViewById(R.id.imageButton3);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (code!=00000) {
                    Iterator it = prefs.getAll().keySet().iterator();
                    while (it.hasNext()) {
                        String key = (String) it.next();

                        if (key.contains(""+code)) {
                            prefs.edit().remove(key);
                        }
                    }
                }

                currentSurfaceIndex = (currentSurfaceIndex + 1)%totalViews;
                currentSurface = drawSurfaces.get(currentSurfaceIndex);
                viewAnimator.showNext();
                int prevSurfaceIndex = (totalViews + currentSurfaceIndex - 1)%totalViews;
                viewAnimator.removeView(drawSurfaces.remove(prevSurfaceIndex));
                totalViews--;
                seekBar.setMax(totalViews-1);
            }
        });

        playButton = (ImageButton) findViewById(R.id.imageButton4);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playIntent = new Intent(Workbench.this, PlayActivity.class);

                Bundle bundle = new Bundle();

                int l=0;
                for (DrawSurface ds : drawSurfaces) {
                    bundle.putStringArrayList("PATHDATA"+l, ds.pathData);
                    l++;
                }

                playIntent.putExtra("PATHBUNDLE", bundle);
                playIntent.putExtra("SIZE", drawSurfaces.size());

                startActivityForResult(playIntent, 12345);
            }
        });

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View view = v;

                float dx = 2.0f;
                if (view.getId()==R.id.next_button_id)
                    dx*=-1;

                TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, dx, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                anim2 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -dx, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);

                anim.setDuration(250);
                anim2.setDuration(250);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        switch (view.getId()) {
                            case R.id.next_button_id:
                                currentSurfaceIndex = (currentSurfaceIndex + 1)%totalViews;
                                currentSurface = drawSurfaces.get(currentSurfaceIndex);
                                viewAnimator.showNext();
                                break;
                            case R.id.prev_button_id:
                                currentSurfaceIndex = (totalViews + currentSurfaceIndex - 1)%totalViews;
                                currentSurface = drawSurfaces.get(currentSurfaceIndex);
                                viewAnimator.showPrevious();
                                break;
                        }

                        seekBar.setProgress(currentSurfaceIndex);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                viewAnimator.startAnimation(anim);
            }
        };

        nextButton.setOnClickListener(clickListener);
        nextButton.setImageResource(R.drawable.next_arrow);
        nextButton.setBackgroundColor(Color.WHITE);
        prevButton.setOnClickListener(clickListener);
        prevButton.setImageResource(R.drawable.prev_arrow);
        prevButton.setBackgroundColor(Color.WHITE);

        saveButton = (Button)findViewById(R.id.save_button_id);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor prefsEditor = prefs.edit();

                if (code==00000) {
                    String resultCodeString = "";

                    while (resultCodeString.length()<5)
                        resultCodeString+=(new Random().nextInt(6));

                    code = Integer.parseInt(resultCodeString);
                }

                int l=0;
                for (DrawSurface ds : drawSurfaces) {
                    JSONArray jsonArray = new JSONArray(ds.pathData);
                    prefsEditor.putString(code + "PATHDATA"+l, jsonArray.toString());
                    l++;
                }

                prefsEditor.putInt(code + "FRAMES", totalViews);

                prefsEditor.apply();
                finish();
            }
        });
    }

    public class DrawSurface extends SurfaceView implements SensorEventListener {

        SurfaceHolder holder;
        Paint paintProperty;

        int screenWidth;
        int screenHeight;

        Path path = new Path();
        ArrayList<String> pathData = new ArrayList<String>();

        public DrawSurface(Context context) {
            super(context);

            holder=getHolder();

            SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    updateScreen();
                    try {
                        viewAnimator.startAnimation(anim2);
                    } catch (Exception ex){}
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    Log.d("A SURFACE IS DESTROYED", "yo");
                }
            };

            holder.addCallback(callback);

            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth=sizeOfScreen.x;
            screenHeight=sizeOfScreen.y;

            paintProperty= new Paint();
            paintProperty.setStrokeWidth(10);
            paintProperty.setTextSize(30);
            paintProperty.setStyle(Paint.Style.STROKE);
            paintProperty.setStrokeJoin(Paint.Join.ROUND);
            paintProperty.setStrokeCap(Paint.Cap.ROUND);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            if (event.getAction()==MotionEvent.ACTION_DOWN) {
                path.moveTo(event.getX(), event.getY());
                pathData.add("m " + (int)event.getX() + " " + (int)event.getY());
            }

            if (event.getAction()==DragEvent.ACTION_DRAG_LOCATION) {
                path.lineTo(event.getX(), event.getY());
                pathData.add("l " + (int)event.getX() + " " + (int)event.getY());
                updateScreen();
            }

            return true;
        }

        public void updateScreen() {

            if (pathData.size()>0 && path.isEmpty()) {
                for (String string : pathData) {
                    int firstNumEndIndex = string.indexOf(' ', 2);
                    int x = Integer.parseInt(string.substring(2, firstNumEndIndex));
                    int y = Integer.parseInt(string.substring(firstNumEndIndex+1));
                    if (string.charAt(0)=='m') {
                        path.moveTo(x,y);
                    } else if (string.charAt(0)=='l') {
                        path.lineTo(x,y);
                    }
                }
                Log.d("TRANSFEREDPATHDATA", ""+pathData);
            }

            Canvas canvas = holder.lockCanvas();
            paintProperty.setStrokeWidth(1);
            paintProperty.setColor(Color.BLACK);
            canvas.drawText("Frame: " + (currentSurfaceIndex+1), 10, 40, paintProperty);
            paintProperty.setStrokeWidth(10);

            try {
                if (currentSurfaceIndex-1>0) {
                    Path prevPrevPath = drawSurfaces.get(currentSurfaceIndex-2).path;
                    Paint projPaint = new Paint();
                    projPaint.setStrokeWidth(10);
                    projPaint.setStyle(Paint.Style.STROKE);
                    projPaint.setStrokeJoin(Paint.Join.ROUND);
                    projPaint.setStrokeCap(Paint.Cap.ROUND);
                    projPaint.setColor(Color.LTGRAY);
                    canvas.drawPath(prevPrevPath, projPaint);
                }
                if (currentSurfaceIndex>0) {
                    Path prevPath = drawSurfaces.get(currentSurfaceIndex-1).path;
                    Paint projPaint = new Paint();
                    projPaint.setStrokeWidth(10);
                    projPaint.setStyle(Paint.Style.STROKE);
                    projPaint.setStrokeJoin(Paint.Join.ROUND);
                    projPaint.setStrokeCap(Paint.Cap.ROUND);
                    projPaint.setColor(Color.GRAY);
                    canvas.drawPath(prevPath, projPaint);
                }

                canvas.drawPath(path, paintProperty);

                holder.unlockCanvasAndPost(canvas);
            } catch (Exception ex){ex.printStackTrace();Toast.makeText(Workbench.this, "Error in updating screen", Toast.LENGTH_SHORT).show();}
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public void onSensorChanged(SensorEvent event) {

        }
    }

    @Override
    public void finish() {

        for (DrawSurface ds : drawSurfaces) {
            Log.d("PATHDATA", ""+ds.pathData);
        }

        setResult(code);
        super.finish();
    }
}