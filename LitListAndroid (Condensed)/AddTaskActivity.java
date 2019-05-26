package com.litlist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {

    EditText titleEditText;
    Spinner courseSelectionSpinner;
    EditText dateEditText;
    Button doneButton;
    RadioGroup workSizeRadioGroup;

    TaskData taskData;

    String title = "Untitled", courseName = "";
    static Date dueDate = null;
    int workSize = 1;

    Bundle bundle;
    Intent resultIntent = new Intent();

    ArrayList<String> courses = NavigationActivity.userData.courseNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        titleEditText = (EditText)findViewById(R.id.title_edit_text_id);
        courseSelectionSpinner = (Spinner)findViewById(R.id.course_spinner_id);
        dateEditText = (EditText) findViewById(R.id.date_edittext_id);
        doneButton = (Button)findViewById(R.id.done_button_id);
        workSizeRadioGroup = (RadioGroup)findViewById(R.id.worksize_radiogroup_id);

        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                title = "" + s;
                if (title.length()>0)
                    toolbar.setTitle(title);
                else toolbar.setTitle("Add New Task");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        courseSelectionSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, courses));
        courseSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                courseName = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dateEditText.setTextIsSelectable(true);
        dateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DialogFragment dueDatePickerFrag = new DatePickerFragment();
                    dueDatePickerFrag.show(getFragmentManager(), "Date Picker");
                }
            }
        });

        workSizeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton1: workSize = 1; break;
                    case R.id.radioButton2: workSize = 2; break;
                    case R.id.radioButton3: workSize = 3; break;
                    case R.id.radioButton4: workSize = 4; break;
                }
            }
        });
        workSizeRadioGroup.check(R.id.radioButton1);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dueDate==null) {
                    Toast.makeText(AddTaskActivity.this, "Please pick a due date", Toast.LENGTH_SHORT).show();
                } else {
                    taskData = new TaskData(title, courseName, dueDate, workSize);
                    bundle = new Bundle();
                    bundle.putSerializable("DATA", taskData);

                    resultIntent.putExtra("DATA_BUNDLE", bundle);
                    resultIntent.putExtra("INDEX", getIntent().getIntExtra("INDEX", -1));
                    finish();
                }
            }
        });

        if (getIntent().hasExtra("TITLE")) {
            titleEditText.setText(getIntent().getStringExtra("TITLE"));
            courseSelectionSpinner.setSelection(courses.indexOf(getIntent().getStringExtra("COURSE")));
            dateEditText.setText(getIntent().getStringExtra("DATESTRING"));
            dueDate = (Date)getIntent().getBundleExtra("DATEBUNDLE").getSerializable("DATE");
            workSize = getIntent().getIntExtra("WORKSIZE", 0);
            workSizeRadioGroup.check(workSize==1 ? R.id.radioButton1 : workSize==2 ? R.id.radioButton2 : workSize==3 ? R.id.radioButton3 : R.id.radioButton4);
            toolbar.setTitle("Edit Task");
            doneButton.setText("Update Task");
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //Use the current date as the default date in the date picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            month++;
            String monthString
                    = (month==1) ? "January"
                    : (month==2) ? "February"
                    : (month==3) ? "March"
                    : (month==4) ? "April"
                    : (month==5) ? "May"
                    : (month==6) ? "June"
                    : (month==7) ? "July"
                    : (month==8) ? "August"
                    : (month==9) ? "September"
                    : (month==10) ? "October"
                    : (month==11) ? "November"
                    : "December";

            String date = monthString + " " + dayOfMonth + ", " + year;

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            Date d = null;
            try {
                d = formatter.parse(year + "/" + month + "/" + dayOfMonth);//catch exception
            } catch (ParseException e) {e.printStackTrace();}

            AddTaskActivity.dueDate = d;
            ((EditText)getActivity().findViewById(R.id.date_edittext_id)).setText(date);
            getActivity().findViewById(R.id.root_layout_id).requestFocus();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void finish() {
        setResult(bundle!=null ? RESULT_OK : RESULT_CANCELED, resultIntent);
        super.finish();
    }
}
