package com.jean.productauthentifier.api;

import com.jean.productauthentifier.model.ProductAuthentifier;
import com.jean.productauthentifier.model.UserChangePassword;
import com.jean.productauthentifier.model.UserLogin;
import com.jean.productauthentifier.model.UserRegistration;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApiService {
    @POST("register-mobile-user")
    Call<String> registerNewUser(@Body UserRegistration userRegistration);

    @POST("login-mob")
    Call<String> userLogin(@Body UserLogin userLogin);

    @POST("change-pwd-mob")
    Call<String> userChangePassword(@Body UserChangePassword userChangePassword);

    @POST("authentify-product")
    Call<ProductAuthentifier> productAuthentifier(@Body ProductAuthentifier productAuthentifier);

}
