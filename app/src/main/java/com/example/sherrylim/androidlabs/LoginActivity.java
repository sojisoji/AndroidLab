package com.example.sherrylim.androidlabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
    protected static final String ACTIVITY_NAME = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button button1 = (Button)findViewById(R.id.login_button);
        EditText textField = (EditText)findViewById(R.id.edittext);


        SharedPreferences prefs = getSharedPreferences("DefaultEmail", Context.MODE_PRIVATE);


        String emailAddress = prefs.getString("DefaultEmail", "email@domain.com");
        textField.setText(emailAddress);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("DefaultEmail", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit(); //edit the file
                EditText textField = (EditText)findViewById(R.id.edittext);
                edit.putString("DefaultEmail", textField.getText().toString()); //ran one more time
                edit.commit();
                Intent Intent = new Intent(LoginActivity.this,
                        StartActivity.class);

                startActivity(Intent);
            }
        });



        Log.i(ACTIVITY_NAME, "In onCreate()");
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.i(ACTIVITY_NAME, "In onResume()");
    }
    @Override
    protected void onStart(){
        super.onStart();
        Log.i(ACTIVITY_NAME, "In onStart()");
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.i(ACTIVITY_NAME, "In onPause()");
    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.i(ACTIVITY_NAME, "In onStop()");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i(ACTIVITY_NAME, "In onDestroy()");
    }
}
