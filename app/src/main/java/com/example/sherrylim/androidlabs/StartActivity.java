package com.example.sherrylim.androidlabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static com.example.sherrylim.androidlabs.LoginActivity.ACTIVITY_NAME;

public class StartActivity extends Activity {
    Button btn, btn2,btn3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        fdv();
        btn = findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(StartActivity.this, ListItemsActivity.class);


                int REQUEST_CODE = 50;
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        /* this is lab 4
         */
        btn2 = findViewById(R.id.ChatButton);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(ACTIVITY_NAME, "User clicked Start Chat");
                Intent intent = new Intent(StartActivity.this, ChatWindow.class);
                startActivity(intent);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, WeatherForecast.class);
                startActivity(intent);
            }
        });


    }// end in on create

    public void fdv(){
        btn2 = findViewById(R.id.ChatButton);
        btn = findViewById(R.id.button);
        btn3= findViewById(R.id.WeatherForecastBtn);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 50) {
            String messagePassed = data.getStringExtra("Response")+" ListItemsActivity passed: My information to share";
            Log.i(ACTIVITY_NAME, "Returned to StartActivity.onActivityResult");
            Toast.makeText(this,messagePassed,
                    Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onStart(){
        super.onStart();
        Log.i("ACTIVITY_NAME","IN onStart()");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i("ACTIVITY_NAME","IN onResume()");

    }


    @Override
    public void onPause(){
        super.onPause();
        Log.i("ACTIVITY_NAME","IN onPause()");

    }


    @Override
    public void onStop(){
        super.onStop();
        Log.i("ACTIVITY_NAME","IN onStop()");

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i("ACTIVITY_NAME","IN  onDestroy()");

    }

}