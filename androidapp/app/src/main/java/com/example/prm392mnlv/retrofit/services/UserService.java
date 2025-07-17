package com.example.prm392mnlv.retrofit.services;

import com.example.prm392mnlv.data.dto.request.UserProfileUpdateRequest;
import com.example.prm392mnlv.data.dto.response.MessageResponse;
import com.example.prm392mnlv.data.dto.response.UserProfileResponse;
import com.example.prm392mnlv.retrofit.json.JsonPath;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface UserService {
    String SEGMENT = "users/";

    @JsonPath("data")
    @GET(SEGMENT + "profile")
    Call<UserProfileResponse> getUserProfile();

    @PUT(SEGMENT)
    Call<MessageResponse> updateUser(@Body UserProfileUpdateRequest userProfileUpdateRequest);
}
