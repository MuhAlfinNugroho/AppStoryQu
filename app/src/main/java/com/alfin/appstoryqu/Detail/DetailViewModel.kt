package com.alfin.appstoryqu.Detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.alfin.appstoryqu.ModelUser
import com.alfin.appstoryqu.RepositoryUser
import com.alfin.appstoryqu.Respon.Story
import com.alfin.appstoryqu.Result

class DetailViewModel(private val userRepository: RepositoryUser) : ViewModel() {

    fun getSession(): LiveData<ModelUser> {
        return userRepository.getSession().asLiveData()
    }

    fun getStoryById(token: String, id: String): LiveData<Result<Story>> {
        return userRepository.getStoryById(token, id)
    }
}