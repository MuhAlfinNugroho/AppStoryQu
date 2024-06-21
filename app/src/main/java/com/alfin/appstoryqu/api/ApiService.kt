package com.alfin.appstoryqu.api

import com.alfin.appstoryqu.Respon.ResponCerita
import com.alfin.appstoryqu.Respon.ResponDaftar
import com.alfin.appstoryqu.Respon.ResponDetailCerita
import com.alfin.appstoryqu.Respon.ResponMasuk
import com.alfin.appstoryqu.Respon.ResponPosting
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun signup(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): ResponDaftar

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): ResponMasuk

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("loadSize") loadSize: Int
    ): ResponCerita

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location : Int = 1,
    ): ResponCerita

    @GET("stories/{id}")
    suspend fun getStoryById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ResponDetailCerita

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): ResponPosting
}