package com.example.userprofile.retrofit.profile;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ModifyProfileService {

    @PUT("UserTEMPs/{id}")
    Call<Void> modifyuser(@Path("id") int id, @Body Map<String, Object> data);

}
