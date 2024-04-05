package com.cryptware.whatsappimagepicker

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AlbumAdapter (
    private val context: Context,
    private val data: Map<String, List<Uri>>,
    private val width: Int,
    private val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.album_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
        layoutParams.width = width/3
        holder.itemView.layoutParams = layoutParams

        val folderName = data.keys.elementAt(position)
        val images = data[folderName]

        holder.albumName.text = folderName
        holder.imageCount.text = images?.size.toString()

        Glide
            .with(context)
            .load(images?.get(0))
            .centerCrop()
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(position, images, folderName)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_image)
        val albumName: TextView = itemView.findViewById(R.id.tv_album_name)
        val imageCount: TextView = itemView.findViewById(R.id.tv_img_count)
    }

    interface ItemClickListener {
        fun onItemClick(position: Int, images: List<Uri>?, name:String)

    }
}