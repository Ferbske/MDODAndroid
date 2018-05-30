package com.project.avans.mdodandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.project.avans.mdodandroid.applicationLogic.api.NetworkManager;
import com.project.avans.mdodandroid.applicationLogic.api.VolleyListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class MainActivity extends AppCompatActivity {
    public static String Token;
    EditText email;
    EditText password;
    TextView resultTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkManager.getInstance(this);
        setContentView(R.layout.activity_main);

        //removes the title from the title bar in mainactivity
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        email = (EditText) findViewById(R.id.editText_Email);
        password = (EditText) findViewById(R.id.editText_Password);
        resultTextView = (TextView) findViewById(R.id.textview_Status);

        Button btn = (Button) findViewById(R.id.button_Login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(
                        email.getText().toString(),
                        password.getText().toString()
                );
            }
        });

        TextView registerTextView = (TextView) findViewById(R.id.textView_register);
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    private void login(String username, String password) {

        NetworkManager.getInstance().loginClient(username, password, new VolleyListener<String>()
        {
            @Override
            public void getResult(String result)
            {
                if (!result.isEmpty())
                {
                    //do what you need with the result...
                    Log.i("VOLLEY_GETRESULT", "Result:" + result);

                    try {
                        JSONObject object = (JSONObject) new JSONTokener(result).nextValue();

                        Token = object.getString("token");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                            Log.d("the token", Token);
                            Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            startActivity(intent);

                } else {

                    resultTextView.setText(R.string.inValidCredentials);
                }
            }
        });
        
    }
}
