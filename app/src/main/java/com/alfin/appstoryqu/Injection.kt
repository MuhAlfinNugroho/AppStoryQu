package com.alfin.appstoryqu

import android.content.Context
import com.alfin.appstoryqu.api.ApiConfig

object Injection {
    fun provideRepository(context: Context): RepositoryUser {
        val pref = PreferenceUser.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return RepositoryUser.getInstance(apiService, pref)
    }
}