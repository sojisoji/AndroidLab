package com.example.sherrylim.androidlabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class WeatherForcast extends Activity {
    protected static final String ACTIVITY_NAME = "WeatherForcast";
    ProgressBar progressBar;
    TextView currentText;
    TextView maxText;
    TextView minText;
    TextView windText;
    ImageView imageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(ACTIVITY_NAME, "In onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forcast);

        progressBar =(ProgressBar)findViewById(R.id.progress);
        currentText = (TextView)findViewById(R.id.currentTemp);
        maxText= (TextView)findViewById(R.id.maxTemp);
        minText= (TextView)findViewById(R.id.minTemp);
        windText= (TextView)findViewById(R.id.wind);
        imageText= (ImageView)findViewById(R.id.current);
        ForecastQuery query = new ForecastQuery();
        query.execute("http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=d99666875e0e51521f0040a3d97d0f6a&mode=xml&units=metric");
    }


    private class ForecastQuery extends AsyncTask<String, Integer, String>{

        private String wind;
        private String min;
        private String max;
        private String current;
        private String iconName;
        Bitmap icon;


        protected String doInBackground(String ...args) {
            try {
                for(String siteUrl: args) {
                    URL url = new URL(siteUrl);

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream iStream = urlConnection.getInputStream();

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(false);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput( iStream  , "UTF-8");

//start reading:

                    //While you're not at the end of the document:
                        while(xpp.next() != XmlPullParser.END_DOCUMENT) {

                        //Are you currently at a Start Tag?
                        if (xpp.getEventType() == XmlPullParser.START_TAG) {

String name = xpp.getName();
                            //Is it the "AMessage" tag?
                            if (xpp.getName().equals("temperature")) {
                                current = xpp.getAttributeValue(null, "value");
                                Log.i("XML Message:", current);
                                publishProgress(25);
                                min = xpp.getAttributeValue(null, "min");
                                Log.i("XML Message:", min);
                                publishProgress(50);
                                max = xpp.getAttributeValue(null, "max");
                                Log.i("XML Message:", max);
                                publishProgress(75);
                            }
                            //Is it the Weather tag?


                                if (xpp.getName().equals("speed")) {
                                    wind = xpp.getAttributeValue(null, "value");
                                    Log.i("XML Message:", wind);
                                    publishProgress(125);
                                }


                            if (xpp.getName().equals("weather")) {
                                iconName = xpp.getAttributeValue(null, "icon");
                                Log.i("XML Message:", iconName);
                                String iconFile = iconName + ".png";
                                if (fileExistance(iconFile)) {
                                    FileInputStream fis = null;
                                    try {
                                        fis = openFileInput(iconFile);
                                        icon = BitmapFactory.decodeStream(fis);

                                    }
                                    catch (FileNotFoundException e) {    e.printStackTrace();  }


                                    Log.i(ACTIVITY_NAME, "Image exists");



                                } else {
                                    URL iconUrl = new URL("http://openweathermap.org/img/w/" + iconName + ".png");
                                    icon  = HttpUtils.getImage(iconUrl);
                                    FileOutputStream outputStream = openFileOutput(iconName + ".png", Context.MODE_PRIVATE);
                                    icon.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                    outputStream.flush();
                                    outputStream.close();
                                    Log.i(ACTIVITY_NAME, "Add new image");

                                }
                                publishProgress(100);
                            }

                            // Go to the next XML event

                        }
                    }
                    }

            }catch (Exception mfe)
            {
                Log.e("Error", mfe.getMessage());
            }
            //Send a string to the GUI thread through onPostExecute
            return "Finished";

        }
        public boolean fileExistance(String fname){
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();   }


        public void onProgressUpdate(Integer ... data)
        {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(data[0]);
        }

        //gui thread
        public void onPostExecute(String result)
        {
            //now you can post your result to the GUI
            currentText.setText("Current Temperature: "+current);
            maxText.setText("Max Temperature: "+max);
            minText.setText("Min Temperature: "+min);
            windText.setText("Wind Speed: "+wind);
            imageText.setImageBitmap(icon);

            progressBar.setVisibility(View.INVISIBLE);


        }


    }
}
