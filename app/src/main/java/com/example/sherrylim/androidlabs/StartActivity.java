package com.example.sherrylim.androidlabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity extends Activity {
    protected static final String ACTIVITY_NAME = "StartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(ACTIVITY_NAME, "In onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button button1 = findViewById(R.id.button1);

        button1.setOnClickListener(e -> {
                Intent secondIntent = new Intent(StartActivity.this,
                        ListItemsActivity.class);
                startActivityForResult(secondIntent, 50);

        });


        Button button2 = findViewById(R.id.button2);

        button2.setOnClickListener(e -> {

                Log.i(ACTIVITY_NAME, "User clicked Start Chat");
                Intent thirdIntent = new Intent(StartActivity.this,
                        ChatWindow.class);
                startActivity(thirdIntent);

        });
        Button button3 = findViewById(R.id.button3);

        button3.setOnClickListener(e -> {
            Intent fourthIntent = new Intent(StartActivity.this,
                    WeatherForcast.class);
            startActivity(fourthIntent);

        });


    }
    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data){
        if(requestCode == 50 && responseCode == Activity.RESULT_OK){
            String messagePassed = data.getStringExtra("Response");
            CharSequence text = "ListItemsActivity passed: "+ messagePassed;
            int duration = Toast.LENGTH_LONG; //= Toast.LENGTH_LONG if Off

            Toast toast = Toast.makeText(getApplicationContext(), text, duration); //this is the ListActivity

            toast.show(); //display your message box
            Log.i(ACTIVITY_NAME, "Returned to StartActivity.onActivityResult");
        }
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
