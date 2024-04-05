package com.cryptware.whatsappimagepicker

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import android.Manifest
import com.cryptware.whatsappimagepicker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), FilePicker.OnImageSelectedListener {

    private lateinit var binding : ActivityMainBinding
    private var gridCount:Int = 3
    private val READ_EXT_STORAGE_PER_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGallery.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    READ_EXT_STORAGE_PER_CODE)
            } else {
                FilePicker.showAllImages(this, this, "img",gridCount)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            READ_EXT_STORAGE_PER_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    FilePicker.showAllImages(this, this, "img", gridCount)
                } else {
                }
                return
            }
        }
    }

    override fun onImageSelected(imagePath: String?) {
        Glide
            .with(this)
            .load(imagePath)
            .centerCrop()
            .into(binding.ivSelected)
    }
}