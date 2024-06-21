package com.alfin.appstoryqu

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alfin.appstoryqu.Daftar.DaftarViewModel
import com.alfin.appstoryqu.Main.MainViewModel
import com.alfin.appstoryqu.Masuk.MasukViewModel
import com.alfin.appstoryqu.Detail.DetailViewModel
import com.alfin.appstoryqu.Peta.MapsViewModel
import com.alfin.appstoryqu.Posting.PostingViewModel

class ViewModelFactory(private val userRepository: RepositoryUser) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(MasukViewModel::class.java) -> { // Untuk Masuk
                MasukViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(DaftarViewModel::class.java) -> { // Untuk Daftar
                DaftarViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> { // Tambahkan untuk DetailViewModel
                DetailViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(PostingViewModel::class.java) -> { // Untuk Posting
                PostingViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}
