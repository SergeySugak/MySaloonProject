package com.app.feature_newservice.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.feature_newservice.R
import com.app.feature_newservice.di.DaggerNewServiceFeatureComponent
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSDialogFragment
import com.app.mscorebase.ui.dialogs.choicedialog.OnChoiceItemsSelectedListener
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.app.mscoremodels.saloon.ChoosableServiceDuration
import javax.inject.Inject

class NewServiceFragment : MSDialogFragment<NewServiceViewModel>() {
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    override val layoutId = R.layout.fragment_new_service

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
            .setPositiveButton(getString(R.string.ok)) { _, _ -> }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                dialog?.dismiss()
            }
        val serviceDuration = view.findViewById<EditText>(R.id.service_duration)
        serviceDuration.setOnClickListener{
            val fragment = ServiceDurationSelectionDialog.newInstance(getString(R.string.str_service_duration),
                null,
                object: OnChoiceItemsSelectedListener<ChoosableServiceDuration, Parcelable?>{
                    override fun onChoiceItemsSelected(item: List<ChoosableServiceDuration>, payload: Parcelable?){
                        getViewModel()?.serviceDuration = item[0]
                        serviceDuration.setText(item[0].description)
                    }
                    override fun onNoItemSelected(payload: Parcelable?) {}
                })
            showDialogFragment(fragment, "")
        }
        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        val dlg = dialog as AlertDialog?
        if (dlg != null){
            val ok = dlg.getButton(Dialog.BUTTON_POSITIVE)
            ok.setOnClickListener{ _ ->
                getViewModel()?.saveServiceInfo(
                    dlg.findViewById<EditText>(R.id.service_name)?.text.toString(),
                    getViewModel()?.serviceDuration,
                    dlg.findViewById<EditText>(R.id.service_price)?.text.toString(),
                    dlg.findViewById<EditText>(R.id.service_description)?.text.toString())
            }
        }
    }

    override fun createViewModel(savedInstanceState: Bundle?): NewServiceViewModel {
        return ViewModelProvider(this, providerFactory).get(NewServiceViewModel::class.java)
    }

    override fun onViewModelCreated(viewModel: NewServiceViewModel, savedInstanceState: Bundle?) {
        super.onViewModelCreated(viewModel, savedInstanceState)
        val serviceId = arguments?.get(ARG_EDIT_SERVICE_ID)?.toString()
        if (!TextUtils.isEmpty(serviceId)) {
            viewModel.loadData(serviceId!!)
        }
    }

    override fun onStartObservingViewModel(viewModel: NewServiceViewModel) {
        viewModel.error.observe(this, Observer { error ->
            if (!viewModel.error.isHandled){
                MessageDialogFragment.showError(this, error, false)
                viewModel.error.isHandled = true
            }
        })

        viewModel.serviceInfoSaveState.observe(this, Observer {
            if (it) {
                val intent = Intent()
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                dismiss()
            }
        })

        viewModel.serviceInfo.observe(this, Observer { service ->
            dialog?.findViewById<EditText>(R.id.service_name)?.setText(service.name)
            dialog?.findViewById<EditText>(R.id.service_duration)?.setText(getViewModel()?.serviceDuration?.description)
            dialog?.findViewById<EditText>(R.id.service_price)?.setText(service.price.toString())
            dialog?.findViewById<EditText>(R.id.service_description)?.setText(service.description)
        })
    }

    companion object {
        fun newInstance() = NewServiceFragment()

        const val ARG_EDIT_SERVICE_ID = "ARG_EDIT_SERVICE_ID"
    }
}
