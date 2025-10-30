package hr.fer.tel.rassus.client.retrofit.api;

import hr.fer.tel.rassus.client.retrofit.request.RegisterSensorRequest;
import hr.fer.tel.rassus.client.retrofit.response.SensorResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface SensorApi {

    @POST("/sensors/register")
    Call<SensorResponse> registerSensor(@Body RegisterSensorRequest request);

    @GET("/sensors")
    Call<List<SensorResponse>> getAllSensors();

    @GET("/sensors/{id}/nearest-neighbour")
    Call<SensorResponse> getNearestNeighbour(@Path("id") String id);
}
