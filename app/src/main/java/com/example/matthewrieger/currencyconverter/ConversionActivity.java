package com.example.matthewrieger.currencyconverter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.matthewrieger.currencyconverter.service.ConversionService;
import com.example.matthewrieger.currencyconverter.model.ConversionRate;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


public class ConversionActivity extends AppCompatActivity {

    private static final String LOG_TAG = "[Convert Activity]";
    private static final String CURRENT_RATE = "CURRENT_RATE";

    private EditText fromCurrencyField;
    private EditText toCurrencyField;
    private EditText fromAmountField;
    private EditText toAmountField;
    private Button convertButton;

    private ConversionRate currentRate;

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
        Log.d(LOG_TAG, "Conversion response: "  + payload);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "Activity Created, hash:" + hashCode());
        setContentView(R.layout.activity_conversion);

        IntentFilter intentFilter = new IntentFilter(ConversionService.CONVERSION_RESULT_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(conversionReceiver, intentFilter);

        fromCurrencyField = (EditText) findViewById(R.id.from_currency);
        toCurrencyField = (EditText) findViewById(R.id.to_currency);
        fromAmountField = (EditText) findViewById(R.id.from_amount);
        toAmountField = (EditText) findViewById(R.id.to_amount);
        convertButton = (Button) findViewById(R.id.convert_button);

        if (savedInstanceState != null) {
            currentRate = (ConversionRate) savedInstanceState.getSerializable(CURRENT_RATE);
        } else {
            fromAmountField.setText("1.00");
            toAmountField.setText("1.00");
        }

        convertButton.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) {
                convert();
            }
        });
    }

    private void convert() {
        if(currenciesChanged()) {
            getRate();
        } else {
            calculateToAmount();
        }
    }

    private boolean currenciesChanged() {
        if (currentRate != null) {
            String from = fromCurrencyField.getText().toString().toLowerCase();
            String to = toCurrencyField.getText().toString().toLowerCase();
            if (from.equals(currentRate.from.toLowerCase()) && to.equals(currentRate.to.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    private void getRate() {
        String from = fromCurrencyField.getText().toString();
        String to = toCurrencyField.getText().toString();
        if (from != null && to != null && from.length() == 3 && to.length() == 3) {
            getRate(from, to);
        }
    }

    private void getRate(String from, String to) {
        Intent convertIntent = new Intent(this, ConversionService.class);
        convertIntent.putExtra(ConversionService.FROM, from);
        convertIntent.putExtra(ConversionService.TO, to);
        startService(convertIntent);
    }

    private void rateLoaded(ConversionRate newRate) {
        currentRate = newRate;
        calculateToAmount();
    }

    private void calculateToAmount() {
        if (currentRate != null) {
            Float toAmount = currentRate.convert(fromAmountField.getText().toString());
            String formattedToAmount = String.format("%.2f", toAmount);
            toAmountField.setText(formattedToAmount);
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(conversionReceiver);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
        outstate.putSerializable(CURRENT_RATE, currentRate);
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

    private BroadcastReceiver conversionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConversionRate result = (ConversionRate) intent.getSerializableExtra(ConversionService.CONVERSION_RESULT_EXTRA);
            rateLoaded(result);
        }
    };

}
