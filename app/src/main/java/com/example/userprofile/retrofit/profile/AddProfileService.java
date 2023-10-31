package com.example.userprofile.retrofit.profile;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AddProfileService {

    @POST("UserTEMPs")
    Call<ProfileResponses> newUser(@Body RequestBody requestBody);

}
