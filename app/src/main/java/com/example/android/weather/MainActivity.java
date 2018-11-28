package com.example.android.weather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    TextView weatherforcastTv;
    Button forcastButton;
    EditText citynameEdt;
    String baseURL ="http://api.openweathermap.org/data/2.5/weather?q=";
    String cityName ="";
    String key = "&APPID=2e241e38f435beefa728d1950c4a4407";
    public void forcastWeather (View view){
        cityName = citynameEdt.getText().toString();

        try {
            String encodedCityName = URLEncoder.encode(cityName, "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute(baseURL+encodedCityName+key).get();
            //InputMethodManage here is used to hide the keyboard.
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(citynameEdt.getWindowToken(), 0);
        }catch (Exception e){
           Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_SHORT).show();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection connection;
            String result= "";
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            StringBuilder forcast = new StringBuilder();
            try{
                JSONObject root = new JSONObject(s);
                JSONArray weatherarray = root.getJSONArray("weather");
                JSONObject weather = (JSONObject) weatherarray.get(0);
                String main = weather.getString("main");
                forcast.append("Main "+main).append("\n");
                String description = weather.getString("description");
                forcast.append("Description "+description).append("\n");
                weatherforcastTv.setText(forcast);
            } catch (Exception e){
                Toast.makeText(getApplicationContext(),"could not find weather", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherforcastTv= (TextView) findViewById(R.id.weatherforcastTv);
         citynameEdt= (EditText) findViewById(R.id.locatinEditText);
        forcastButton = (Button) findViewById(R.id.searchBtn);
    }
}
