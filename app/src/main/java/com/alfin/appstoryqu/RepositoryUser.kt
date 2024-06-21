package com.alfin.appstoryqu

import CeritaPagingCourse
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.alfin.appstoryqu.Respon.ListStoryItem
import com.alfin.appstoryqu.Respon.ResponDaftar
import com.alfin.appstoryqu.Respon.ResponDetailCerita
import com.alfin.appstoryqu.Respon.ResponMasuk
import com.alfin.appstoryqu.Respon.ResponPosting
import com.alfin.appstoryqu.Respon.Story
import com.alfin.appstoryqu.api.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class RepositoryUser private constructor(
    private val apiService: ApiService,
    private val userPreference: PreferenceUser
) {

    suspend fun saveSession(user: ModelUser) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<ModelUser> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun signup(name: String, email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val successResponse = apiService.signup(name, email, password)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ResponDaftar::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }

    fun login(email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val successResponse = apiService.login(email, password)
            val userModel = ModelUser(
                email = email,
                token = successResponse.loginResult.token,
                isLogin = true
            )
            saveSession(userModel)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ResponMasuk::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                CeritaPagingCourse("Bearer $token", apiService)
            }
        ).liveData
    }

    fun getStoryById(token: String, id: String): LiveData<Result<Story>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val successResponse: ResponDetailCerita =
                    apiService.getStoryById("Bearer $token", id)
                emit(Result.Success(successResponse.story))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    fun uploadStory(token: String, imageFile: File, description: String, location: Location?) =
        liveData {
            emit(Result.Loading)

            val descriptionBody = description.toRequestBody("text/plain".toMediaType())
            val imageRequestBody = imageFile.asRequestBody("image/jpeg".toMediaType())
            val imagePart = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                imageRequestBody
            )

            try {
                val response = if (location != null) {
                    val latitude = location.latitude.toString().toRequestBody("text/plain".toMediaType())
                    val longitude = location.longitude.toString().toRequestBody("text/plain".toMediaType())

                    apiService.uploadStory(
                        "Bearer $token",
                        imagePart,
                        descriptionBody,
                        latitude,
                        longitude
                    )
                } else {
                    apiService.uploadStory(
                        "Bearer $token",
                        imagePart,
                        descriptionBody
                    )
                }

                emit(Result.Success(response))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ResponPosting::class.java)
                emit(Result.Error(errorResponse.message))
            }
        }


    fun getStoryWithLocation(token: String): LiveData<Result<List<ListStoryItem>>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val response = apiService.getStoriesWithLocation("Bearer $token")
                val stories = response.listStory
                emit(Result.Success(stories))
            } catch (exception: Exception) {
                emit(Result.Error(exception.message.toString()))
            }
        }



    companion object {
        @Volatile
        private var instance: RepositoryUser? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: PreferenceUser
        ): RepositoryUser =
            instance ?: synchronized(this) {
                instance ?: RepositoryUser(apiService, userPreference)
            }.also { instance = it }
    }
}