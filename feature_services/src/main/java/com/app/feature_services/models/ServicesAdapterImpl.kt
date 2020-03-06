package com.app.feature_services.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.feature_services.R
import java.util.*
import javax.inject.Inject

class ServicesAdapterImpl @Inject constructor(): RecyclerView.Adapter<ServicesAdapterImpl.ViewHolder>(), ServicesAdapter {

    private val items = ArrayList<com.app.mscoremodels.services.SaloonService>()

    override fun setItems(items: List<com.app.mscoremodels.services.SaloonService>){
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_service, parent, false)
        return ViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(service: com.app.mscoremodels.services.SaloonService){
            var name = itemView.findViewById<TextView>(R.id.service_name)
            var duration = itemView.findViewById<TextView>(R.id.service_duration)
            var price = itemView.findViewById<TextView>(R.id.service_price)
            var description = itemView.findViewById<TextView>(R.id.service_description)
        }
    }
}