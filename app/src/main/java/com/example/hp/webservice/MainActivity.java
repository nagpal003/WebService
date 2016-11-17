package com.example.hp.webservice;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {

    private static final String DEBUG_TAG = "HttpExample";
    private EditText ed;
    private TextView tx;
    private Button btn;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ed = (EditText)findViewById(R.id.editText2);
        tx = (TextView)findViewById(R.id.textView);
        btn = (Button)findViewById(R.id.btn_fetch_weather);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                String url = ed.getText().toString();
                ConnectivityManager connectivityManager =
                        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected())
                       {
                           new DownloadWebpageTask().execute(url);
                       }
                else
                       {
                           tx.setText("@string/d");
                       }
            }
        });
    }
    private class DownloadWebpageTask extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... params) {
            try
            {
                return downloadUrl(params[0]);
            }catch(Exception e)
            {
                return "Unable to make connection.Try again.";
            }
        }
//Given a url it establishes the connection and retrieves the web page content as a InputStream which it returns in form of a string
        private String downloadUrl(String url) throws IOException {
            InputStream is = null;
            int cnt = 100;

            try
            {
                URL input = new URL(url);
                HttpURLConnection connection = (HttpURLConnection)input.openConnection();
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                connection.connect();
                //Querying started
                int response = connection.getResponseCode();
                Log.d(DEBUG_TAG,"Response is :" + response);
                is = connection.getInputStream();

                //Convert the inputSteam into a string
                String contentAsString = readIt(is,cnt);
                return contentAsString;
            }finally {
                if(is != null)
                {
                    is.close();
                }
            }
        }

        private String readIt(InputStream stream, int cntr) throws IOException,UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream,"UTF-8");
            char[] buff = new char[cntr];
            return new String(buff);
        }

        //OnPreExecute will display the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result)
        {
            tx.setText(result);
        }
    }
}