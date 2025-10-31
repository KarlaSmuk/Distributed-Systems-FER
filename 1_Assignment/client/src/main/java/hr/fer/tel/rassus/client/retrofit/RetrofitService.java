package hr.fer.tel.rassus.client.retrofit;

import hr.fer.tel.rassus.client.retrofit.api.ReadingApi;
import hr.fer.tel.rassus.client.retrofit.api.SensorApi;
import lombok.Data;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Data
public class RetrofitService {

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://localhost:8090")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public SensorApi getSensorApi() {
        return this.retrofit.create(SensorApi.class);
    }

    public ReadingApi getReadingApi() {
        return this.retrofit.create(ReadingApi.class);
    }
}
