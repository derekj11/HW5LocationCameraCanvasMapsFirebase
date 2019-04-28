package com.example.hw5

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MyAdapter(private val myDataset: LinkedList<CapturedLocation>,
                private val mContext: Context
) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val mCoordinates: TextView = itemView.findViewById(R.id.latlong)
        private val mAddress: TextView = itemView.findViewById(R.id.address)
        private val mTimestamp: TextView = itemView.findViewById(R.id.timestamp)

        init {
            itemView.setOnClickListener(this)
        }

        fun bindTo(currentLocation: CapturedLocation) {
            mCoordinates.text = currentLocation.coordinates
            mAddress.text = currentLocation.address
            mTimestamp.text = currentLocation.timeStamp

            // use Glide for image?
        }

        override fun onClick(v: View?) {
            // go to new screen with map
            val gmmIntentUri: Uri = Uri.parse("geo:${mCoordinates.text}")
            val mapIntent: Intent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            mContext.startActivity(mapIntent)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false))
    }

    override fun getItemCount(): Int {
        return myDataset.size
    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
        val currentLocation = myDataset[position]
        holder.bindTo(currentLocation)
    }

}