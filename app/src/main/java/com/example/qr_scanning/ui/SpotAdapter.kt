package com.example.qr_scanning.ui

// SpotAdapter.kt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.qr_scanning.R
import com.example.qr_scanning.model.Spot

class SpotAdapter(private val context: Context, private val spots: List<Spot>) : BaseAdapter() {

    override fun getCount(): Int {
        return spots.size
    }

    override fun getItem(position: Int): Any {
        return spots[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.spot_list_item, parent, false)

        val spot = spots[position]

        val tvSpotName = view.findViewById<TextView>(R.id.tvSpotName)
        val tvSpotAddress = view.findViewById<TextView>(R.id.tvSpotAddress)

        tvSpotName.text = spot.name
        tvSpotAddress.text = "住所：${spot.address}"

        return view
    }
}
