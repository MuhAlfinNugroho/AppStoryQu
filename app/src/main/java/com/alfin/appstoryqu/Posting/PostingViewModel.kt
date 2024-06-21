package com.alfin.appstoryqu.Posting

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.alfin.appstoryqu.ModelUser
import com.alfin.appstoryqu.RepositoryUser
import java.io.File

class PostingViewModel(private val userRepository: RepositoryUser) : ViewModel() {

    fun getSession(): LiveData<ModelUser> {
        return userRepository.getSession().asLiveData()
    }

    fun uploadStory(token: String, file: File, description: String, location: Location?) =
        userRepository.uploadStory(token, file, description, location)

}