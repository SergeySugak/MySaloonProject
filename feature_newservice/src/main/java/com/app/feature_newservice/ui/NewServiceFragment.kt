package com.app.feature_newservice.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
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
import com.app.mscoremodels.saloon.ServiceDuration
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
        val serviceDuration = view.findViewById<EditText>(R.id.service_duration)
        serviceDuration.setOnClickListener{
            val fragment = ServiceDurationSelectionDialog.newInstance(getString(R.string.str_service_duration),
                null,
                object: OnChoiceItemsSelectedListener<ServiceDuration, Parcelable?>{
                    override fun onChoiceItemsSelected(item: List<ServiceDuration>, payload: Parcelable?){
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
                    dlg.findViewById<EditText>(R.id.service_price)?.text.toString(),
                    dlg.findViewById<EditText>(R.id.service_description)?.text.toString())
            }
        }
    }

    override fun createViewModel(savedInstanceState: Bundle?): NewServiceViewModel {
        return ViewModelProvider(this, providerFactory).get(NewServiceViewModel::class.java)
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
    }

    companion object {
        fun newInstance() = NewServiceFragment()
    }
}
