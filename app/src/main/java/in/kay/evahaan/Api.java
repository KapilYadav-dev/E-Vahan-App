package in.kay.evahaan;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface Api {
    @Headers("api-key:" + "YOUR-API-KEY")
    @GET("{dl_num}")
    Call<ResponseBody> search(
            @Path("dl_num") String dl_num);

    @GET("{state}")
    Call<List<ResponseModel>> petrol(
            @Path("state") String state);

}
