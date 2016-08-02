package com.example.matthewrieger.currencyconverter.api;

import com.example.matthewrieger.currencyconverter.model.ConversionRate;
import retrofit.http.GET;
import retrofit.http.Path;

public interface ConversionAPI {
   @GET("/convert/{fromCurrency}/{toCurrency}.json")
   public ConversionRate convert (
           @Path("fromCurrency") String fromCurrency,
           @Path("toCurrency") String toCurrency
   );
}
