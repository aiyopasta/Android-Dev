package com.litlist;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import static android.app.Activity.RESULT_OK;

public class AgendaFragment extends Fragment{

    View view;
    ArrayList<TaskData> data;
    RecyclerView recyclerView;
    UserData userData;

    int productivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_agenda, container, false);

        /*if (userData==null) { //To prevent crashing when switching fragments
            userData = ((NavigationActivity)getActivity()).userData = (UserData)getActivity().getIntent().getSerializableExtra("USER_DATA");
            data = userData.tasks;

            addDefaults();
        }*/

        UserData forNewUser = (UserData)getActivity().getIntent().getSerializableExtra("USER_DATA");
        if (forNewUser==null) {
            //This is an active user returning to fragment
            userData = ((NavigationActivity)getActivity()).userData;
            data = userData.tasks;
        } else if (userData==null){
            //This is a new user
            userData = ((NavigationActivity)getActivity()).userData = forNewUser;
            data = userData.tasks;
            addDefaults();
        }

        recyclerView = (RecyclerView)view.findViewById(R.id.rec_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new AgendaFragment.CardViewAdapter(data));

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int index = viewHolder.getAdapterPosition();
                data.remove(index);
                recyclerView.getAdapter().notifyDataSetChanged();

                userFeedback();
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    public void addDefaults() {
        data.add(new TaskData("Some Project", "AP Physics C", new Date(), 1));
        data.add(new TaskData("A Test", "AP Calculus BC", new Date(), 2));
        data.add(new TaskData("Compsci Project", "Photoshop II", new Date(), 3));
        data.add(new TaskData("Lorem Epsum", "English III", new Date(), 4));

        userData.courseNames.add("AP Physics C");
        userData.courseNames.add("AP Calculus BC");
        userData.courseNames.add("Photoshop II");
        userData.courseNames.add("English III");
    }

    //Fragment Stuff

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 12345) {
            TaskData newData = (TaskData)data.getBundleExtra("DATA_BUNDLE").getSerializable("DATA");
            this.data.add(newData);
            recyclerView.getAdapter().notifyDataSetChanged();
        } else if (resultCode == RESULT_OK && requestCode == 54321) {
            TaskData updateData = (TaskData)data.getBundleExtra("DATA_BUNDLE").getSerializable("DATA");
            this.data.set(data.getIntExtra("INDEX", -1), updateData);
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void userFeedback() {
        final Dialog dialog = new Dialog(getActivity()) {
            @Override
            public boolean onTouchEvent(@NonNull MotionEvent event) {
                //Only so that users cannot click outside dialog box to dismiss it.
                return true;
            }
        };
        dialog.setContentView(R.layout._feedback_dialog_layout);
        dialog.setTitle("Productivity Meter");

        SeekBar meter = (SeekBar)dialog.findViewById(R.id.productivity_seekbar_id);
        meter.setMax(4);
        meter.setProgress(0);
        productivity = 1;
        meter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                productivity = progress+1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button doneButton = (Button)dialog.findViewById(R.id.dialog_done_button_id);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "The productivity level was " + productivity + ".", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        Button cancelButton = (Button)dialog.findViewById(R.id.dialog_cancel_button_id);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Task was unfinished and removed.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public class CardViewAdapter extends RecyclerView.Adapter<AgendaFragment.CardViewAdapter.CardViewHolder> {

        private ArrayList<TaskData> taskDataList;

        public CardViewAdapter(ArrayList<TaskData> dataList) {
            taskDataList = dataList;
        }

        public AgendaFragment.CardViewAdapter.CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout._carditem_layout, viewGroup, false);

            return new AgendaFragment.CardViewAdapter.CardViewHolder(itemView);
        }

        public class CardViewHolder extends RecyclerView.ViewHolder {
            TextView titleTV, courseTV, dateTV;
            CardView cv;

            public CardViewHolder(View v) {
                super(v);
                titleTV = (TextView)v.findViewById(R.id.task_title_tv);
                courseTV = (TextView)v.findViewById(R.id.course_tv);
                dateTV = (TextView)v.findViewById(R.id.date_tv);

                cv = (CardView)v.findViewById(R.id.cardview);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cv.setElevation(9);
                }

                cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), AddTaskActivity.class);
                        intent.putExtra("TITLE", titleTV.getText());
                        intent.putExtra("COURSE", (courseTV.getText()+"").substring(1));
                        intent.putExtra("DATESTRING", (dateTV.getText()+"").substring(4));
                        intent.putExtra("WORKSIZE", (int)Math.round(Color.red(((ColorDrawable)cv.getBackground()).getColor())/255.0*4.0));
                        Bundle b = new Bundle();
                        b.putSerializable("DATE", taskDataList.get(getAdapterPosition()).dueDate);
                        intent.putExtra("DATEBUNDLE", b);
                        intent.putExtra("INDEX", getAdapterPosition());
                        startActivityForResult(intent, 54321);
                    }
                });
            }
        }

        @Override
        public void onBindViewHolder(AgendaFragment.CardViewAdapter.CardViewHolder holder, int i) {
            TaskData cd = taskDataList.get(i);
            holder.titleTV.setText(cd.taskTitle);
            holder.courseTV.setText(cd.COURSEPREFIX + cd.courseName);
            holder.dateTV.setText(cd.DATEPREFIX + (new SimpleDateFormat("MM-dd-yyyy").format(cd.dueDate)));

            double redshift = (((double)cd.workSize)/4.0)*255.0,
                    greenshift = 255.0 - redshift;
            holder.cv.setBackgroundColor(Color.argb(200, (int)redshift, (int)greenshift, 0));
        }

        @Override
        public int getItemCount() {
            return taskDataList.size();
        }

        public void notifyAdapterDatasetChanged() {
            userData = ((NavigationActivity)getActivity()).userData;
            data = userData.tasks;
            notifyDataSetChanged();
        }
    }
}
