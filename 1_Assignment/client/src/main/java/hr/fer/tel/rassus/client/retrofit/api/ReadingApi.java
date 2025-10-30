package hr.fer.tel.rassus.client.retrofit.api;

import hr.fer.tel.rassus.client.retrofit.request.CreateReadingRequest;
import hr.fer.tel.rassus.client.retrofit.response.ReadingResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface ReadingApi {

    @POST("/readings/sensor/{sensorId}")
    Call<ReadingResponse> createReading(@Path("sensorId") String sensorId, @Body CreateReadingRequest request);

    @GET("/readings/sensor/{sensorId}")
    Call<List<ReadingResponse>> getReadings(@Path("sensorId") String sensorId);
}
