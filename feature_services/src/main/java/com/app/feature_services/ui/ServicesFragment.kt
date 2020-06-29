package com.app.feature_services.ui

import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.feature_services.R
import com.app.feature_services.di.DaggerServicesFeatureComponent
import com.app.feature_services.models.ServicesAdapter
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSFragment
import com.app.mscorebase.ui.dialogs.messagedialog.DialogFragmentPresenter.Companion.ICON_WARNING
import com.app.mscorebase.ui.dialogs.messagedialog.DialogFragmentPresenter.Companion.TWO_BUTTONS_YN
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import javax.inject.Inject

class ServicesFragment : MSFragment<ServicesViewModel>() {

    private lateinit var servicesList: RecyclerView

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    @Inject
    lateinit var appNavigator: AppNavigator
        protected set

    @Inject
    lateinit var servicesAdapter: ServicesAdapter
        protected set

    override val layoutId = R.layout.services_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerServicesFeatureComponent
            .builder()
            .servicesFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        servicesAdapter.setOnServiceClickListener { service ->
            appNavigator.navigateToEditServiceFragment(this, service.id)
        }
        servicesAdapter.setOnServiceDeleteListener { service ->
            MessageDialogFragment.showMessage(
                this, getString(R.string.title_warning),
                String.format(getString(R.string.str_delete_service), service.name),
                ICON_WARNING, REQ_DELETE_SERVICE, TWO_BUTTONS_YN,
                bundleOf(Pair(SERVICE_ID, service.id))
            )
        }
        servicesList = view.findViewById(R.id.services_list)
        servicesList.layoutManager = LinearLayoutManager(context)
        servicesList.adapter = servicesAdapter as RecyclerView.Adapter<*>
    }

    override fun createViewModel(savedInstanceState: Bundle?): ServicesViewModel {
        return ViewModelProvider(this, providerFactory).get(ServicesViewModel::class.java)
    }

    override fun onStartObservingViewModel(viewModel: ServicesViewModel) {
        viewModel.services.observe(this, Observer { services ->
            servicesAdapter.setItems(services)
        })

        viewModel.error.observe(this, Observer { error ->
            if (!viewModel.error.isHandled) {
                MessageDialogFragment.showError(this, error, false)
                viewModel.error.isHandled = true
            }
        })
    }

    override fun onClickDialogButton(
        dialog: DialogInterface?,
        whichButton: Int,
        requestCode: Int,
        params: Bundle?
    ) {
        if (requestCode == REQ_DELETE_SERVICE) {
            if (whichButton == BUTTON_POSITIVE) {
                getViewModel()?.deleteService(params?.getString(SERVICE_ID))
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {

    }

    companion object {
        fun newInstance() = ServicesFragment()

        const val EDIT_SERVICE_FRAGMENT_TAG = "NewServiceDialogFragment"
        const val REQ_EDIT_SERVICE = 10002
        const val REQ_DELETE_SERVICE = 10003
        val SERVICE_ID = "${ServicesFragment::class.java.simpleName}_SERVICE_ID"
    }

}
