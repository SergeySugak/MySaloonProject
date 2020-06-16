package com.app.feature_services.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.feature_services.R
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscoremodels.saloon.SaloonService
import java.util.*
import javax.inject.Inject

class ServicesAdapterImpl
@Inject constructor(private val dbRepository: DbRepository) :
    RecyclerView.Adapter<ServicesAdapterImpl.ViewHolder>(), ServicesAdapter {

    private val items = ArrayList<SaloonService>()
    private var onServiceClick: (service: SaloonService) -> Unit = {}
    private var onDeleteService: (service: SaloonService) -> Unit = {}

    override fun setItems(items: List<SaloonService>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_service, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onServiceClick, onDeleteService)
    }

    override fun setOnServiceClickListener(onServiceClick: (service: SaloonService) -> Unit) {
        this.onServiceClick = onServiceClick
    }

    override fun setOnServiceDeleteListener(onDeleteService: (service: SaloonService) -> Unit) {
        this.onDeleteService = onDeleteService
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(
            service: SaloonService,
            onServiceClick: (service: SaloonService) -> Unit,
            onDeleteService: (service: SaloonService) -> Unit
        ) {
            val name = itemView.findViewById<TextView>(R.id.service_name)
            val duration = itemView.findViewById<TextView>(R.id.service_duration)
            val price = itemView.findViewById<TextView>(R.id.service_price)
            val description = itemView.findViewById<TextView>(R.id.service_description)
            val deleteService = itemView.findViewById<ImageButton>(R.id.delete_service)
            name.text = service.name
            duration.text = service.duration?.description
            price.text = service.price.toString()
            description.text = service.description
            itemView.setOnClickListener {
                onServiceClick(service)
            }
            deleteService.setOnClickListener {
                onDeleteService(service)
            }
        }
    }
}