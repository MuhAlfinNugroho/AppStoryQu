package com.alfin.appstoryqu.Peta

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.alfin.appstoryqu.ModelUser
import com.alfin.appstoryqu.RepositoryUser
import com.alfin.appstoryqu.Result
import com.alfin.appstoryqu.Respon.ListStoryItem
class MapsViewModel(private val userRepository: RepositoryUser) : ViewModel() {

    private val _storyListWithLocation = MediatorLiveData<Result<List<ListStoryItem>>>()
    val storyListWithLocation: LiveData<Result<List<ListStoryItem>>> = _storyListWithLocation

    fun getSession(): LiveData<ModelUser> {
        return userRepository.getSession().asLiveData()
    }

    fun getStoriesWithLocation(token: String) {
        val liveData = userRepository.getStoryWithLocation(token)
        _storyListWithLocation.addSource(liveData) { result ->
            _storyListWithLocation.value = result
        }
    }

    companion object {
        private const val TAG = "MapsViewModel"
    }
}