package com.alfin.appstoryqu.Respon

import com.google.gson.annotations.SerializedName

data class ResponDaftar(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)