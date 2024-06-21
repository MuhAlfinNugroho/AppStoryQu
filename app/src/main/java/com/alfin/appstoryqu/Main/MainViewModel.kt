package com.alfin.appstoryqu.Main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.alfin.appstoryqu.ModelUser
import com.alfin.appstoryqu.RepositoryUser
import com.alfin.appstoryqu.Respon.ListStoryItem
import kotlinx.coroutines.launch
import androidx.paging.cachedIn

class MainViewModel(private val userRepository: RepositoryUser) : ViewModel() {

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return userRepository.getStories(token).cachedIn(viewModelScope)
    }

    fun getSession(): LiveData<ModelUser> {
        return userRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

}