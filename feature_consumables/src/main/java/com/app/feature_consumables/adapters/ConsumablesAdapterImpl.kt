package com.app.feature_consumables.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.feature_consumables.R
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscoremodels.saloon.SaloonConsumable
import java.util.*
import javax.inject.Inject

class ConsumablesAdapterImpl
@Inject constructor(private val dbRepository: DbRepository) :
    RecyclerView.Adapter<ConsumablesAdapterImpl.ViewHolder>(), ConsumablesAdapter {

    private val items = ArrayList<SaloonConsumable>()
    private var onConsumableClick: (consumable: SaloonConsumable) -> Unit = {}
    private var onDeleteConsumable: (consumable: SaloonConsumable) -> Unit = {}

    override fun setItems(items: List<SaloonConsumable>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_consumable, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onConsumableClick, onDeleteConsumable)
    }

    override fun setOnConsumableClickListener(onConsumableClick: (consumable: SaloonConsumable) -> Unit) {
        this.onConsumableClick = onConsumableClick
    }

    override fun setOnConsumableDeleteListener(onDeleteConsumable: (consumable: SaloonConsumable) -> Unit) {
        this.onDeleteConsumable = onDeleteConsumable
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView by lazy { itemView.findViewById<TextView>(R.id.consumable_name) }
        private val uom: TextView by lazy { itemView.findViewById<TextView>(R.id.consumable_uom) }
        private val price: TextView by lazy { itemView.findViewById<TextView>(R.id.consumable_price) }
        private val deleteConsumable: ImageButton by lazy { itemView.findViewById<ImageButton>(R.id.delete_consumable) }

        fun bind(
            consumable: SaloonConsumable,
            onConsumableClick: (consumable: SaloonConsumable) -> Unit,
            onDeleteConsumable: (consumable: SaloonConsumable) -> Unit
        ) {
            name.text = consumable.name
            uom.text = consumable.uom
            price.text = String.format("%.2f", consumable.price)
            itemView.setOnClickListener {
                onConsumableClick(consumable)
            }
            deleteConsumable.setOnClickListener {
                onDeleteConsumable(consumable)
            }
        }
    }
}