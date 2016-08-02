package com.example.matthewrieger.currencyconverter.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.matthewrieger.currencyconverter.api.ConversionAPI;
import com.example.matthewrieger.currencyconverter.model.ConversionRate;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import retrofit.RestAdapter;

public class ConversionService extends IntentService {
    public static final String CONVERSION_RESULT_ACTION = "CONVERSION_RESULT_ACTION";
    public static final String CONVERSION_RESULT_EXTRA = "CONVERSION_RESULT_EXTRA";
    public static final String FROM = "from";
    public static final String TO = "to";

    public static final String LOG_TAG = "[Conversion Service";

    public ConversionService() {
        super("Conversion Service");
    }

    @Override protected void onHandleIntent(Intent intent) {
        String fromCurrency = intent.getStringExtra(FROM);
        String toCurrency = intent.getStringExtra(TO);
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint("http://10.0.3.2:3000").build();
        ConversionAPI api = adapter.create(ConversionAPI.class);
        ConversionRate rate = api.convert(fromCurrency, toCurrency);

        Intent conversionResultIntent = new Intent(CONVERSION_RESULT_ACTION);
        conversionResultIntent.putExtra(CONVERSION_RESULT_EXTRA, rate);
        LocalBroadcastManager.getInstance(this).sendBroadcast(conversionResultIntent);
    }
}
