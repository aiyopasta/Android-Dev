package com.litlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StartupActivity extends AppCompatActivity {

    Button doneButton, addCourseButton;
    LinearLayout layout;
    UserData userData = new UserData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        doneButton = (Button)findViewById(R.id.done_button_id);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartupActivity.this, NavigationActivity.class);
                Toast.makeText(StartupActivity.this, "You can add or remove courses via the settings at any time.", Toast.LENGTH_LONG).show();

                for (int i = 0; i < layout.getChildCount() - 1; i++) {
                    userData.courseNames.add(((EditText) layout.getChildAt(i)).getText().toString());
                }

                intent.putExtra("USER_DATA", userData);
                startActivity(intent);
                finish();
            }
        });
        doneButton.setEnabled(false);

        layout = (LinearLayout)findViewById(R.id.courses_edittext_layout_id);

        addCourseButton = (Button)findViewById(R.id.add_course_button_id);
        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText temp = new EditText(StartupActivity.this);
                temp.setHint("ex. Academic English III");
                temp.setText("");
                layout.addView(temp, layout.getChildCount()-1);
            }
        });

        EditText firstEditText = ((EditText)findViewById(R.id.first_course_edittext_id));
        firstEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length()>0) {
                    doneButton.setEnabled(true);
                    addCourseButton.setEnabled(true);
                } else {
                    doneButton.setEnabled(false);
                    addCourseButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
