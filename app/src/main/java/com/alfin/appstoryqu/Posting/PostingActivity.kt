package com.alfin.appstoryqu.Posting

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alfin.appstoryqu.Main.MainActivity
import com.alfin.appstoryqu.R
import com.alfin.appstoryqu.ViewModelFactory
import com.alfin.appstoryqu.databinding.ActivityPostingBinding
import com.alfin.appstoryqu.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class PostingActivity : AppCompatActivity() {

    private val viewModel by viewModels<PostingViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityPostingBinding

    private var currentImageUri: Uri? = null
    private var location: Location? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permintaan izin dikabulkan", Toast.LENGTH_LONG).show()
                if (isGPSEnabled()) {
                    addLocationAndUpload()
                } else {
                    showEnableGPSDialog()
                }
            } else {
                Toast.makeText(this, "Permintaan izin ditolak", Toast.LENGTH_LONG).show()
                binding.uploadButton.isEnabled = true
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.title_posting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.layoutButton.galleryButton.setOnClickListener { startGallery() }
        binding.layoutButton.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { checkPermissionsAndUpload() }
    }

    private fun startGallery() {
        launcherGallery.launch("image/*")
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun checkPermissionsAndUpload() {
        val description = binding.descriptionEditText.text.toString().trim()

        if (description.isEmpty()) {
            binding.descriptionEditTextLayout.error = getString(R.string.description_empty)
            return
        } else {
            binding.descriptionEditTextLayout.error = null
        }

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            if (isGPSEnabled()) {
                addLocationAndUpload()
            } else {
                showEnableGPSDialog()
            }
        }
    }

    private fun addLocationAndUpload() {
        binding.uploadButton.isEnabled = false  // Prevent multiple clicks
        addLocation()
    }

    private fun uploadStory() {
        val description = binding.descriptionEditText.text.toString().trim()

        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()

            viewModel.getSession().observe(this) { user ->
                val token = user.token
                viewModel.uploadStory(token, imageFile, description, location).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                showLoading(true)
                            }
                            is Result.Success -> {
                                showToast(result.data.message)
                                resetUI()
                                showLoading(false)
                                // Start MainActivity with flags to clear task and make it a new task
                                val intent = Intent(this, MainActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                startActivity(intent)
                                finish()
                            }
                            is Result.Error -> {
                                showToast(result.error)
                                showLoading(false)
                            }

                            else -> {}
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.image_empty))
    }

    private fun resetUI() {
        binding.descriptionEditText.text?.clear()
        binding.previewImageView.setImageResource(R.drawable.ic_tambah_foto)
        currentImageUri = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun isGPSEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showEnableGPSDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.enable_gps))
            setMessage(getString(R.string.enable_gps_message))
            setPositiveButton(getString(R.string.lanjut)) { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            setNegativeButton(getString(R.string.tidaklanjut)) { _, _ -> }
            create()
            show()
        }
    }

    private fun addLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    location = loc
                    uploadStory()
                } else {
                    Toast.makeText(
                        this,
                        R.string.error_no_location,
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.uploadButton.isEnabled = true
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}