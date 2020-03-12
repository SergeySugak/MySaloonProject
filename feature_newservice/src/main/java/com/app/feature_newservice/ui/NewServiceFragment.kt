package com.app.feature_newservice.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.CheckedTextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.feature_newservice.R
import com.app.feature_newservice.di.DaggerNewServiceFeatureComponent
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSDialogFragment
import com.app.mscorebase.ui.dialogs.SingleChoiceAdapter
import com.app.mscoremodels.services.ServiceDuration
import javax.inject.Inject

class NewServiceFragment : MSDialogFragment<NewServiceViewModel>() {
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    @Inject
    lateinit var serviceDurationsAdapter: SingleChoiceAdapter<ServiceDuration>
        protected set

    override val layoutId = R.layout.fragment_new_service

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerNewServiceFeatureComponent
            .builder()
            .newServiceFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onBuildDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(layoutId, null)
        val builder = AlertDialog.Builder(activity!!)
        builder.setView(view)
            .setTitle(R.string.title_fragment_edit_service)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                val intent = Intent()
                //intent.putExtra(TAG_WEIGHT_SELECTED, mNpWeight.getValue())
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
            }
        recyclerView = view.findViewById(R.id.service_descriptions)
        setupRecyclerView(recyclerView)
        return builder.create()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        serviceDurationsAdapter.setViewBinder { view, position ->
            val nameTextView: CheckedTextView = view.findViewById(android.R.id.text1)
            val nameText = serviceDurationsAdapter.getItem(position).name
            nameTextView.text = nameText
            nameTextView.isChecked = position == serviceDurationsAdapter.checkedPosition
        }
        recyclerView.adapter = serviceDurationsAdapter
        getViewModel().getServiceDurations()
    }

    override fun createViewModel(savedInstanceState: Bundle?): NewServiceViewModel {
        return ViewModelProvider(this, providerFactory).get(NewServiceViewModel::class.java)
    }

    override fun onStartObservingViewModel(viewModel: NewServiceViewModel) {
        viewModel.serviceDurations.observe(this, Observer { items ->
            serviceDurationsAdapter.setItems(items)
        })
    }

    companion object {
        fun newInstance() = NewServiceFragment()
    }
}
