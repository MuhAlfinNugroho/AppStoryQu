package com.alfin.appstoryqu

data class ModelUser(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)