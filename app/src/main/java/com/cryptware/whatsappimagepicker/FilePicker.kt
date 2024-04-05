package com.cryptware.whatsappimagepicker

import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

object FilePicker {

    private var selectedURI = ""
    private lateinit var resList: List<String>
    private lateinit var albumList: Map<String, List<Uri>>
    private var backType = 0 //0-> album 1 -> allImages

    fun showAllImages(context: Context, listener: OnImageSelectedListener, resType: String, gridCount: Int){
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_gallery)
        dialog.setCancelable(true)

        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val rvAlbums = dialog.findViewById<RecyclerView>(R.id.rv_album)
        val rvImages = dialog.findViewById<RecyclerView>(R.id.rv_images)
        val tvClose = dialog.findViewById<TextView>(R.id.tv_close)
        val tvSelect = dialog.findViewById<TextView>(R.id.tv_select)
        val tvAlbum = dialog.findViewById<TextView>(R.id.tv_album)
        val ivBack = dialog.findViewById<ImageView>(R.id.iv_back)
        val ivSelectedImage = dialog.findViewById<ImageView>(R.id.iv_selected_image)

        when (resType) {
            "img" -> {
                //get image albums
                albumList = fetchGalleryImages(context)
                //resList = getAllImages(context)
            }
            "vid" -> {
                //get all videos
            }
            else -> {
                //default set all images
                //resList = getAllImages(context)
                albumList = fetchGalleryImages(context)
            }
        }

        //set layout manager to recycle view
        rvAlbums.layoutManager = GridLayoutManager(context, gridCount)
        rvImages.layoutManager = GridLayoutManager(context, gridCount)
        val albumAdapter = AlbumAdapter(context, albumList, getScreenWidth(context), object : AlbumAdapter.ItemClickListener{
            override fun onItemClick(position: Int, images: List<Uri>?, albumName: String) {
                ivBack.visibility = View.VISIBLE
                tvClose.visibility = View.INVISIBLE
                ivSelectedImage.visibility = View.VISIBLE
                rvAlbums.visibility = View.GONE
                rvImages.visibility = View.VISIBLE
                tvSelect.visibility = View.GONE
                tvAlbum.text = albumName
                backType = 0

                val imageAdapter = ResoItemAdapter(context,
                    images!!, getScreenWidth(context), object : ResoItemAdapter.ItemClickListener{
                    override fun onItemClick(position: Int, uri: String) {
                        ivBack.visibility = View.VISIBLE
                        tvClose.visibility = View.INVISIBLE
                        ivSelectedImage.visibility = View.VISIBLE
                        rvAlbums.visibility = View.GONE
                        rvImages.visibility = View.GONE
                        tvSelect.visibility = View.VISIBLE
                        backType = 1
                        selectedURI = uri
                        Glide
                            .with(context)
                            .load(uri)
                            .centerCrop()
                            .into(ivSelectedImage)
                    }
                })
                rvImages.adapter = imageAdapter
            }
        })
        rvAlbums.adapter = albumAdapter
        dialog.show()

        tvClose.setOnClickListener {
            dialog.cancel()
        }

        ivBack.setOnClickListener {
            if (backType == 0) {
                ivBack.visibility = View.GONE
                tvClose.visibility = View.VISIBLE
                ivSelectedImage.visibility = View.GONE
                rvAlbums.visibility = View.VISIBLE
                rvImages.visibility = View.GONE
                tvSelect.visibility = View.GONE
                tvAlbum.text = "Album"
            }
            else{
                ivBack.visibility = View.VISIBLE
                tvClose.visibility = View.GONE
                ivSelectedImage.visibility = View.GONE
                rvAlbums.visibility = View.GONE
                rvImages.visibility = View.VISIBLE
                tvSelect.visibility = View.GONE
                backType = 0
            }
        }

        tvSelect.setOnClickListener {
            dialog.cancel()
            listener.onImageSelected(selectedURI)
        }
    }

    //get all images from galley
    private fun getAllImages(context: Context): List<String> {
        val resList = mutableListOf<String>()
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID)
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val imagePath = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()){
                var id = cursor.getLong(imagePath)
                var contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                resList.add(contentUri.toString())
            }
        }
        return resList
    }

    private fun fetchGalleryImages(context: Context): Map<String, List<Uri>> {
        val galleryMap = mutableMapOf<String, MutableList<Uri>>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val selection = "${MediaStore.Images.Media.DATA} like '%/DCIM/%'"

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val folderColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val folderName = cursor.getString(folderColumn)
                val contentUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())

                if (galleryMap.containsKey(folderName)) {
                    galleryMap[folderName]?.add(contentUri)
                } else {
                    val imagesList = mutableListOf<Uri>()
                    imagesList.add(contentUri)
                    galleryMap[folderName] = imagesList
                }
            }
        }
        return galleryMap
    }

    //get screen width
    private fun getScreenWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    //selected image listener
    interface OnImageSelectedListener {
        fun onImageSelected(imagePath: String?)
    }
}