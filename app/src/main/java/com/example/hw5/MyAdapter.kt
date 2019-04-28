package com.example.hw5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MyAdapter(private val myDataset: LinkedList<CapturedLocation>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

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