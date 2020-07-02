package com.app.feature_consumables.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.feature_consumables.R
import com.app.feature_consumables.adapters.ConsumablesAdapter
import com.app.feature_consumables.di.DaggerConsumablesFeatureComponent
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSFragment
import com.app.mscorebase.ui.dialogs.messagedialog.DialogFragmentPresenter
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import javax.inject.Inject

class ConsumablesFragment : MSFragment<ConsumablesViewModel>() {

    private lateinit var consumablesList: RecyclerView

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    @Inject
    lateinit var appNavigator: AppNavigator
        protected set

    @Inject
    lateinit var consumablesAdapter: ConsumablesAdapter
        protected set

    override val layoutId = R.layout.consumables_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        consumablesAdapter.setOnConsumableClickListener { consumable ->
            appNavigator.navigateToEditMasterFragment(this, consumable.id)
        }
        consumablesAdapter.setOnConsumableDeleteListener { consumable ->
            MessageDialogFragment.showMessage(
                this, getString(R.string.title_warning),
                String.format(getString(R.string.str_delete_consumable), consumable.name),
                DialogFragmentPresenter.ICON_WARNING, REQ_DELETE_CONSUMABLE,
                DialogFragmentPresenter.TWO_BUTTONS_YN,
                bundleOf(Pair(CONSUMABLE_ID, consumable.id))
            )
        }
        consumablesList = view.findViewById(R.id.consumables_list)
        consumablesList.layoutManager = LinearLayoutManager(context)
        consumablesList.adapter = consumablesAdapter as RecyclerView.Adapter<*>
    }

    override fun createViewModel(savedInstanceState: Bundle?) =
        ViewModelProvider(this, providerFactory).get(ConsumablesViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerConsumablesFeatureComponent
            .builder()
            .consumablesFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onStartObservingViewModel(viewModel: ConsumablesViewModel) {
        viewModel.consumables.observe(this, Observer { consumable ->
            consumablesAdapter.setItems(consumable)
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
        if (requestCode == REQ_DELETE_CONSUMABLE) {
            if (whichButton == DialogInterface.BUTTON_POSITIVE) {
                getViewModel()?.deleteConsumable(params?.getString(CONSUMABLE_ID))
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
    }

    companion object {
        fun newInstance() = ConsumablesFragment()

        const val EDIT_CONSUMABLE_FRAGMENT_TAG = "NewMasterDialogFragment"
        const val REQ_EDIT_CONSUMABLE = 10002
        const val REQ_DELETE_CONSUMABLE = 10003
        val CONSUMABLE_ID = "${ConsumablesFragment::class.java.simpleName}_CONSUMABLE_ID"
    }
}
