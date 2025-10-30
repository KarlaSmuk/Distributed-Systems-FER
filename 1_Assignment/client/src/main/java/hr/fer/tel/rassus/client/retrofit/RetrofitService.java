package hr.fer.tel.rassus.client.retrofit;

import retrofit2.Retrofit;

public class RetrofitService {

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://localhost:8090")
            //.addConverterFactory()
            .build();
}
