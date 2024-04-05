package com.cryptware.whatsappimagepicker

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ResoItemAdapter (
    private val context: Context,
    private val data: List<Uri>,
    private val width: Int,
    private val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<ResoItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
        layoutParams.width = width/3
        layoutParams.height = width/3
        holder.itemView.layoutParams = layoutParams
        Glide
            .with(context)
            .load(data[position])
            .centerCrop()
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(position, data[position].toString())
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
    }

    interface ItemClickListener {
        fun onItemClick(position: Int, uri: String)

    }
}