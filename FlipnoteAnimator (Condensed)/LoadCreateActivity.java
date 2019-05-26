package com.example.adityaabhyankar.flipnoteanimator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class LoadCreateActivity extends AppCompatActivity {

    ImageView createNewButton;

    ArrayList<Integer> resultCodes = new ArrayList<>();
    ArrayList<Button> buttons = new ArrayList<>();

    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_create);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        createNewButton = (ImageView)findViewById(R.id.create_new_button_id);
        createNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoadCreateActivity.this, Workbench.class);
                intent.putExtra("NEW", true);
                startActivityForResult(intent, 00000);
            }
        });

        Toast.makeText(this, "Welcome back! Click on a previously saved file to edit a Flipnote or on the plus button to create a new one.", Toast.LENGTH_LONG).show();

        tableLayout = (TableLayout)findViewById(R.id.table_layout_id);

        SharedPreferences prefs = this.getSharedPreferences("com.example.adityaabhyankar.flipnoteanimator", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Iterator it = prefs.getAll().keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (key.contains("PATHDATA")) {
                resultCodes.add(Integer.parseInt(key.substring(0, 5)));
                final Button button = new Button(this);
                buttons.add(button);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LoadCreateActivity.this, Workbench.class);
                        intent.putExtra("NEW", false);
                        intent.putExtra("RESULTCODE", resultCodes.get(buttons.indexOf(button)));
                        Log.d("REQUEST CODE", ""+resultCodes.get(buttons.indexOf(button)));
                        startActivityForResult(intent, resultCodes.get(buttons.indexOf(button)));
                    }
                });

                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT);
                button.setLayoutParams(lp);
                button.setTextSize(20f);
                button.setText("Saved_"+resultCodes.size());

                ((TableRow)tableLayout.getChildAt(resultCodes.size()/5)).addView(button);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (!resultCodes.contains(resultCode)) {

            final Button button = new Button(this);
            resultCodes.add(resultCode);
            buttons.add(button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoadCreateActivity.this, Workbench.class);
                    intent.putExtra("NEW", false);
                    intent.putExtra("RESULTCODE", resultCodes.get(buttons.indexOf(button)));
                    Log.d("REQUEST CODE", ""+resultCodes.get(buttons.indexOf(button)));
                    startActivityForResult(intent, resultCodes.get(buttons.indexOf(button)));
                }
            });

            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT);
            button.setLayoutParams(lp);
            button.setTextSize(20f);
            button.setText("Saved_"+resultCodes.size());

            ((TableRow)tableLayout.getChildAt(resultCodes.size()/5)).addView(button);
        }
    }
}
