package com.project.avans.mdodandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.project.avans.mdodandroid.adapters.GoalAdapter.AsyncGoal;
import com.project.avans.mdodandroid.adapters.GoalAdapter.GoalAdapter;
import com.project.avans.mdodandroid.adapters.GoalAdapter.GoalListener;
import com.project.avans.mdodandroid.adapters.GoalAdapter.OnAlertBoxAvailable;
import com.project.avans.mdodandroid.adapters.GoalAdapter.onGoalClick;
import com.project.avans.mdodandroid.applicationLogic.api.NetworkManager;
import com.project.avans.mdodandroid.applicationLogic.api.VolleyListener;
import com.project.avans.mdodandroid.object_classes.Goal;

import java.util.ArrayList;

public class MyPersonalGoalsActivity extends AppCompatActivity implements DialogInterface.OnShowListener, GoalListener, OnAlertBoxAvailable {
    private View updateDialogView;
    private ArrayList<Goal> goalList = new ArrayList<>();
    private GoalAdapter goalAdapter = null;
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_personal_goals);

        Button add = findViewById(R.id.button_goals_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "";
                showUpdateDialog();
            }
        });

        //TEST DATA API
        String url = "https://mdod.herokuapp.com/api/v1/goal";
//        "https://mdod.herokuapp.com/api/v1/goal"

        String[] urls = new String[] {url};
        AsyncGoal task = new AsyncGoal((GoalListener) this);
        task.execute(urls);

        ListView goalListView = findViewById(R.id.listView);
        goalAdapter = new GoalAdapter(getLayoutInflater(), goalList);
        goalListView.setAdapter(goalAdapter);
        goalAdapter.notifyDataSetChanged();
        goalListView.setOnItemClickListener(new onGoalClick(getApplicationContext(), getLayoutInflater(), this));
    }

    private void showUpdateDialog() {


        AlertDialog alertDialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        if (type.equals("")) {
            builder.setTitle(getResources().getString(R.string.newGoal));
        } else if (type.equals("goal")){
            builder.setTitle("change goal");
        }

        View view;

        view = inflater.inflate(R.layout.dialog_updateprofile, null);
        builder.setView(view);

        updateDialogView = view;
        builder.setPositiveButton(getResources().getString(R.string.saveChanges), null);
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        alertDialog = builder.create();
        alertDialog.setOnShowListener(this);

        alertDialog.show();
    }

    @Override
    public void onShow(final DialogInterface dialog) {
        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TextView incorrectFieldTextView = updateDialogView.findViewById(R.id.dialogUpdateProfile_textViewIncorrectField);
                final TextView updateDialogGenericEditText = updateDialogView.findViewById(R.id.dialogUpdateProfile_editText);

                String field = String.valueOf(updateDialogGenericEditText.getText());
                Log.i("DialogUpdateProfile", "Value of field: " + field);

                if (field.equals("")) {
                    incorrectFieldTextView.setText(getResources().getString(R.string.userSettingsFieldInvalid));
                } else {

                    if (type.equals("")) {
                        NetworkManager.getInstance().postGoal(updateDialogGenericEditText.getText().toString(),  new VolleyListener<String>(){
                            @Override
                            public void getResult(String result)
                            {
                                if (!result.isEmpty())
                                {
                                    dialog.dismiss();
                                    goalList.add(new Goal(updateDialogGenericEditText.getText().toString()));
                                    goalAdapter.notifyDataSetChanged();
                                } else {
                                }
                            }

                        });

                    } else if (type.equals("goal")){
                    }

                }


            }
        });
    }

    @Override
    public void onAlertBoxAvailable(Goal goal) {
        type = "goal";
        showUpdateDialog();
    }

    @Override
    public void onGoalListener(Goal goal) {
        goalList.add(goal);
        goalAdapter.notifyDataSetChanged();
    }

    //adds custom menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent i;
        switch(id){
            case R.id.menu_user_settings:
                i = new Intent(getApplicationContext(), UserSettingsActivity.class);
                startActivity(i);
                break;
            case R.id.menu_logout:
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
