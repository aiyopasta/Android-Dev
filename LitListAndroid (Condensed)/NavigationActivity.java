package com.litlist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    FloatingActionButton fab;
    AgendaFragment agendaFragment = new AgendaFragment();
    SettingsFragment settingsFragment = new SettingsFragment();
    NavigationView navigationView;
    static DrawerLayout drawer;

    static TextView acctNameTextView, acctEmailTextView;
    static ImageView acctPicImageView;

    GoogleApiClient gac;
    GoogleSignInOptions gso;
    GoogleSignInAccount acct;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    ProgressDialog progressDialog;

    static UserData userData;
    boolean importedSavedData = false;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        SharedPreferences prefs = getSharedPreferences("com.litlist", Context.MODE_PRIVATE);

        if (prefs.getAll().containsKey("HAS_SAVED_DATA")) {
            this.userData = new UserData();

            ArrayList<String> courseNames = new ArrayList<>(prefs.getStringSet("courseNames", null));
            long id = prefs.getLong("id", -1);
            String username = prefs.getString("username", "null");

            Iterator it = prefs.getAll().entrySet().iterator();

            TaskData[] taskData = new TaskData[prefs.getInt("taskcount", -1)];
            for (int i=0;i<taskData.length;i++) taskData[i] = new TaskData();

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                String key = ((String)pair.getKey());

                if (key.endsWith("TASK")) {
                    int index = Integer.parseInt("" + key.charAt(key.length()-5));

                    switch (key.substring(0, 2)) {
                        case "ta" :
                            taskData[index].taskTitle = (String)pair.getValue();
                            break;
                        case "co" :
                            taskData[index].courseName = (String)pair.getValue();
                            break;
                        case "da" :
                            taskData[index].dueDate = new Date((String)pair.getValue());
                            break;
                        case "wo" :
                            taskData[index].workSize = (int)pair.getValue();
                            break;
                    }
                }

                it.remove();
            }

            this.userData.courseNames = courseNames;
            this.userData.tasks = new ArrayList<TaskData>(Arrays.asList(taskData));
            this.userData.id = id;
            this.userData.username = username;

            importedSavedData = true;
        }

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user==null) {
                    //User is signing out
                } else {
                    //User is signing in
                }
            }
        };

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                acctPicImageView = (ImageView)findViewById(R.id.navbar_accountpic_id);
                acctPicImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (acct==null)
                            googleSignin();
                    }
                });
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);
        getFragmentManager().beginTransaction().replace(R.id.placeholder_frame, agendaFragment).commit();
        getFragmentManager().executePendingTransactions();

        fab = (FloatingActionButton) findViewById(R.id.fab_id);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationActivity.this.getFragmentManager().beginTransaction().replace(R.id.placeholder_frame, agendaFragment).commit();
                NavigationActivity.this.getFragmentManager().executePendingTransactions();

                NavigationActivity.this.navigationView.getMenu().getItem(0).setChecked(true);

                Intent intent = new Intent(NavigationActivity.this, AddTaskActivity.class);
                agendaFragment.startActivityForResult(intent, 12345);
            }
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("911954893165-jq3iqa6ft73qcd0g4247tcs84qgs5l1a.apps.googleusercontent.com").requestEmail().build();
        gac = new GoogleApiClient.Builder(this).enableAutoManage((AppCompatActivity)this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    //Google Sign-in

    private void googleSignin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(gac);
        startActivityForResult(signInIntent, 9001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (requestCode == 9001) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                acct = result.getSignInAccount();
                handleSigninResult(acct);
            } else {
                System.out.println(result);
                Snackbar.make(drawer, "Something went wrong...", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void handleSigninResult(final GoogleSignInAccount acct) {
        System.out.println(acct.getIdToken());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            final FirebaseUser user = firebaseAuth.getCurrentUser();

                            Snackbar.make(drawer, user.getDisplayName() + " successfully signed in.", Snackbar.LENGTH_SHORT).show();

                            NavigationActivity.drawer.openDrawer(GravityCompat.START);
                            TextView tv1 = NavigationActivity.acctNameTextView = (TextView)findViewById(R.id.navbar_accountname_id);
                            TextView tv2 = NavigationActivity.acctEmailTextView = (TextView)findViewById(R.id.navbar_emailacct_id);
                            ImageView im = NavigationActivity.acctPicImageView = (ImageView)findViewById(R.id.navbar_accountpic_id);

                            tv1.setText(user.getDisplayName());
                            tv2.setText(user.getEmail());

                            im.setMaxHeight(im.getHeight());
                            im.setMaxWidth(im.getWidth());
                            Picasso.with(getApplicationContext()).load(user.getPhotoUrl()).into(im);
                            userData.username = user.getDisplayName();
                            System.out.println("username: " + userData.username);

                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        Object o = ((UserData)dataSnapshot.getValue(UserData.class)).tasks;
                                        userData = dataSnapshot.child(user.getUid()).getValue(UserData.class);

                                        //Doesn't Work For When Data is Erased.

                                        NavigationActivity.this.getFragmentManager().beginTransaction().replace(R.id.placeholder_frame, settingsFragment).commit();
                                        NavigationActivity.this.getFragmentManager().executePendingTransactions();
                                        ((AgendaFragment.CardViewAdapter)agendaFragment.recyclerView.getAdapter()).notifyAdapterDatasetChanged();
                                        NavigationActivity.this.getFragmentManager().beginTransaction().replace(R.id.placeholder_frame, agendaFragment).commit();
                                        NavigationActivity.this.getFragmentManager().executePendingTransactions();
                                        drawer.closeDrawer(GravityCompat.START);

                                        importedSavedData = true;
                                    } catch (NullPointerException nex){nex.printStackTrace();}
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(NavigationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            NavigationActivity.this.acct = null;
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Navigation Stuff

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu._dotted_menu_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.app.FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_agenda) {
            fragmentManager.beginTransaction().replace(R.id.placeholder_frame, agendaFragment).commit();
        } else if (id == R.id.nav_settings) {
            fragmentManager.beginTransaction().replace(R.id.placeholder_frame, settingsFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        progressDialog.hide();
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("com.litlist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putStringSet("courseNames", new HashSet<String>(userData.courseNames));
        editor.putLong("id", userData.id);
        editor.putString("username", userData.username);

        editor.putInt("taskcount", userData.tasks.size());

        final String TASKPREFIX = "TASK";

        //Wipe old task data
        Iterator it = prefs.getAll().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            if (pair.getKey().toString().contains(TASKPREFIX)) {
                editor.remove(pair.getKey().toString());
            }
            it.remove();
        }

        //Save New Task Data
        int i=0;
        for (TaskData t : userData.tasks) {
            editor.putString("taskTitle" + i + TASKPREFIX, t.taskTitle);
            editor.putString("courseName" + i + TASKPREFIX, t.courseName);
            editor.putString("dateString" + i + TASKPREFIX, t.dueDate.toString());
            editor.putInt("workSize" + i + TASKPREFIX, t.workSize);
            i++;
        }

        editor.putBoolean("HAS_SAVED_DATA", true).commit();
        editor.commit();

        if (acct!=null) {
            databaseReference.child(user.getUid()).setValue(userData);
            System.out.println("Tasks: " + userData.tasks);
        }

        if (authStateListener!=null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }

        importedSavedData = false;
    }
}
