package edu.neu.cs5520.alphaobserver.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JSONPlaceholder {
    // https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=IBM&apikey=42Z7B1OTYEEJQCOT
    @GET("query")
    Call<StockSearchResult> getStockSearchResult(@Query("function") String function,
                                                 @Query("keywords") String keywords,
                                                 @Query("apikey") String apikey);

    // https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=IBM&apikey=42Z7B1OTYEEJQCOT
    @GET("query")
    Call<StockQuoteResult> getStockQuoteResult(@Query("function") String function,
                                                 @Query("symbol") String symbol,
                                                 @Query("apikey") String apikey);
}
