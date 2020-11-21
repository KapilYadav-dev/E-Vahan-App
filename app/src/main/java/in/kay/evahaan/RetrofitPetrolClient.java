package in.kay.evahaan;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitPetrolClient {
    public static final String BASE_URL = "YOUR_BASE_URL";
    public static RetrofitPetrolClient mInstance;
    public Retrofit retrofit;

    public RetrofitPetrolClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitPetrolClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitPetrolClient();
        }
        return mInstance;
    }

    public Api getApi() {
        return retrofit.create(Api.class);
    }
}
