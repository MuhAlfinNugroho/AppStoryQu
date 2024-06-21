package com.alfin.appstoryqu.Detail

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alfin.appstoryqu.R
import com.alfin.appstoryqu.Respon.Story
import com.alfin.appstoryqu.databinding.ActivityDetailBinding
import com.alfin.appstoryqu.Result
import com.alfin.appstoryqu.ViewModelFactory
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {

    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.title_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val storyId = intent.getStringExtra("storyId")

        viewModel.getSession().observe(this) { user ->
            val token = user.token
            if (storyId != null) {
                viewModel.getStoryById(token, storyId).observe(this) {
                    when (it) {
                        is Result.Loading -> showLoading(true)
                        is Result.Success -> {
                            val storyItem = it.data
                            setDetailData(storyItem)
                            showLoading(false)
                        }
                        is Result.Error -> showLoading(false)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setDetailData(storyItem: Story) {
        binding.apply {
            Glide
                .with(this@DetailActivity)
                .load(storyItem.photoUrl)
                .fitCenter()
                .into(imgItemPhotoDetail)

            // Set the username label
            tvUsernameLabel.text = "Username:"
            // Set the actual name
            tvItemNameDetail.text = storyItem.name
            // Set the description label
            tvDescriptionLabel.text = "Deskripsi:"
            // Set the actual description
            tvItemDescriptionDetail.text = storyItem.description
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            imgItemPhotoDetail.visibility = if (isLoading) View.GONE else View.VISIBLE
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}