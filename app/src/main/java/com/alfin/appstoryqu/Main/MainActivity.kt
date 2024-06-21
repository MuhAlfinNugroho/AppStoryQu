package com.alfin.appstoryqu.Main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfin.appstoryqu.Detail.DetailActivity
import com.alfin.appstoryqu.Peta.MapsActivity
import com.alfin.appstoryqu.Posting.PostingActivity
import com.alfin.appstoryqu.R
import com.alfin.appstoryqu.ViewModelFactory
import com.alfin.appstoryqu.Welcome.WelcomeActivity
import com.alfin.appstoryqu.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> { ViewModelFactory.getInstance(this) }
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ListCeritaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupMainMenu()

        adapter = ListCeritaAdapter { story ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("storyId", story.id)
            }
            startActivity(intent)
        }

        binding.rvStoriesList.layoutManager = LinearLayoutManager(this)
        binding.rvStoriesList.adapter = adapter

        observeSession()
        observeStoryList()
        binding.fabUpload.setOnClickListener {
            startActivity(Intent(this, PostingActivity::class.java))
        }
    }

    private fun setupViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupMainMenu() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    viewModel.logout()
                    true
                }
                R.id.action_maps -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun observeSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                viewModel.getStories(user.token).observe(this) { pagingData ->
                    lifecycleScope.launch {
                        adapter.submitData(pagingData)
                    }
                }
            }
        }
    }

    private fun observeStoryList() {
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadState ->
                showLoading(loadState.refresh is LoadState.Loading)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.rvStoriesList.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                viewModel.logout()
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
                true
            }
            R.id.action_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}