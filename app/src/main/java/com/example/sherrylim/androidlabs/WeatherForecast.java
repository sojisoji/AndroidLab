package com.example.sherrylim.androidlabs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;


public class WeatherForecast extends Activity {

    protected static final String ACTIVITY_NAME="WeatherForecast";
    protected static final String URL_STRING = "http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=d99666875e0e51521f0040a3d97d0f6a&mode=xml&units=metric";
    protected static final String URL_IMAGE = "http://openweathermap.org/img/w/";
    private ProgressBar progress;
    private TextView currTempTxt;
    private TextView minTempTxt;
    private TextView maxTempTxt;
    private TextView windSpeedTxt;
    private ImageView weatherImage;
    private TextView targetLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        progress = findViewById(R.id.progressBar);
        progress.setVisibility(View.VISIBLE);

        currTempTxt = findViewById(R.id.currentTemp);
        minTempTxt = findViewById(R.id.minTemp);
        maxTempTxt = findViewById(R.id.maxTemp);
        weatherImage = findViewById(R.id.WeatherImg);
        windSpeedTxt = findViewById(R.id.windSpeed);
        new ForecastQuery().execute(null, null, null);
    }

    //innerclass that extends AsyncTask
    class ForecastQuery extends AsyncTask<String, Integer, String> {
        private String minTemp;
        private String maxTemp;
        private String currTemp;
        private String iconFile;
        private String windSpeed;
        private Bitmap bitmap;

        @Override
        protected String doInBackground(String... args){
            InputStream stream;

            // connecting to url and reading data input stream
            try {
                URL url = new URL(URL_STRING);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000); //in milliseconds
                conn.setConnectTimeout(15000); //in millisenconds
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                stream = conn.getInputStream();
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(stream, null);
                int eventType = parser.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT){
                    if(eventType != XmlPullParser.START_TAG){
                        eventType = parser.next();
                        continue;
                    }
                    else{
                        if(parser.getName().equals("temperature")) {
                            currTemp = parser.getAttributeValue(null, "value");
                            publishProgress(25);
                            minTemp = parser.getAttributeValue(null, "min");
                            publishProgress(50);
                            maxTemp = parser.getAttributeValue(null, "max");
                            publishProgress(75);
                        } else if(parser.getName().equals("speed")) {
                            windSpeed = parser.getAttributeValue(null, "value");
                            publishProgress(90);
                        } else if(parser.getName().equals("weather")){
                            iconFile = parser.getAttributeValue(null, "icon");
                        }
                    }
                    eventType = parser.next();
                }
                conn.disconnect();

                //download image through file or URL object
                if(fileExist(iconFile + ".png")){
                    Log.i(ACTIVITY_NAME, "Weather image exists, read file");
                    FileInputStream fis = null;
                    try {
                        fis = openFileInput(iconFile + ".png");
                    } catch (FileNotFoundException e){
                        e.printStackTrace();
                    }  bitmap = BitmapFactory.decodeStream(fis);

                }else {
                    Log.i(ACTIVITY_NAME, "Weather image does not exist, download URL");

                    URL imageUrl = new URL(URL_IMAGE + iconFile + ".png");
                    conn = (HttpURLConnection) imageUrl.openConnection();
                    conn.connect();
                    stream = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(stream);

                    FileOutputStream fos = openFileOutput(iconFile + ".png", Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos);
                    fos.flush();
                    fos.close();
                    conn.disconnect();
                }
                publishProgress(100);
            }catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer ... value){
            progress.setVisibility(View.VISIBLE);
            progress.setProgress(value[0]);
        }

        @Override
        protected void onPostExecute(String args){
            progress.setVisibility(View.INVISIBLE);
            currTempTxt.setText("Current Temperature: " + currTemp + "℃");
            minTempTxt.setText("Min Temperature: " + minTemp + "℃");
            maxTempTxt.setText("Max Temperature: " + maxTemp + "℃");
            windSpeedTxt.setText("Wind Speed: " +windSpeed + "m/s");
            weatherImage.setImageBitmap(bitmap);
        }

        public boolean fileExist(String name){
            File file = getBaseContext().getFileStreamPath(name);
            return file.exists();
        }
    }
}
