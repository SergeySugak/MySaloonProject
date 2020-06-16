package com.app.feature_masters.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.feature_masters.R
import com.app.msa_db_repo.repository.db.DbRepository
import com.app.mscoremodels.saloon.SaloonMaster
import java.util.*
import javax.inject.Inject

class MastersAdapterImpl
@Inject constructor(private val dbRepository: DbRepository) :
    RecyclerView.Adapter<MastersAdapterImpl.ViewHolder>(), MastersAdapter {

    private val items = ArrayList<SaloonMaster>()
    private var onMasterClick: (master: SaloonMaster) -> Unit = {}
    private var onDeleteMaster: (master: SaloonMaster) -> Unit = {}

    override fun setItems(items: List<SaloonMaster>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_master, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onMasterClick, onDeleteMaster)
    }

    override fun setOnMasterClickListener(onMasterClick: (master: SaloonMaster) -> Unit) {
        this.onMasterClick = onMasterClick
    }

    override fun setOnMasterDeleteListener(onDeleteMaster: (master: SaloonMaster) -> Unit) {
        this.onDeleteMaster = onDeleteMaster
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView by lazy { itemView.findViewById<TextView>(R.id.master_name) }
        private val description: TextView by lazy { itemView.findViewById<TextView>(R.id.master_description) }
        private val portfolioUrl: TextView by lazy { itemView.findViewById<TextView>(R.id.master_portfolio_url) }
        private val deleteMaster: ImageButton by lazy { itemView.findViewById<ImageButton>(R.id.delete_master) }

        fun bind(
            master: SaloonMaster,
            onMasterClick: (master: SaloonMaster) -> Unit,
            onDeleteMaster: (master: SaloonMaster) -> Unit
        ) {
            name.text = master.name
            description.text = master.description
            portfolioUrl.text = master.portfolioUrl
            itemView.setOnClickListener {
                onMasterClick(master)
            }
            deleteMaster.setOnClickListener {
                onDeleteMaster(master)
            }
        }
    }
}