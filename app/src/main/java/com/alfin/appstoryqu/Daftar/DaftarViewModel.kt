package com.alfin.appstoryqu.Daftar

import androidx.lifecycle.ViewModel
import com.alfin.appstoryqu.RepositoryUser

class DaftarViewModel (private val userRepository: RepositoryUser) : ViewModel() {

    fun signup(name: String, email: String, password: String) = userRepository.signup(name, email, password)
}