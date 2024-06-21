package com.alfin.appstoryqu.Masuk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfin.appstoryqu.ModelUser
import com.alfin.appstoryqu.RepositoryUser
import kotlinx.coroutines.launch

class MasukViewModel(private val userRepository: RepositoryUser) : ViewModel() {
    fun saveSession(user: ModelUser) {
        viewModelScope.launch {
            userRepository.saveSession(user)
        }
    }
    fun login(email: String, password: String) = userRepository.login(email, password)
}