package com.example.matthewrieger.currencyconverter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.OkHttpClient;


public class ConversionActivity extends AppCompatActivity {

    private static final String LOG_TAG = "[Convert Activity]";

    private AsyncTask<String, Void, String> loadConversionTask = new AsyncTask<String, Void, String>() {
        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            OkHttpClient client = new OkHttpClient();
            Request conversionRequest = new Request.Builder().url(urlString).build();
            Response response = null;

            try {
                response = client.newCall(conversionRequest).execute();
                String body = response.body().string();
                return body;
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }

            return null;
        }

        @Override protected void onPostExecute(String payload) {
            conversionComplete(payload);
        }
    };

    private void conversionComplete(String payload) {
        Log.d(LOG_TAG, "Conversion response: " + payload);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        String url = "http://10.0.3.2:3000/convert/USD/EUR.json";
        loadConversionTask.execute(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversion, menu);
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

}
