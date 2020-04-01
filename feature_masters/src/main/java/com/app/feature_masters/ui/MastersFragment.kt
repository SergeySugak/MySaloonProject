package com.app.feature_masters.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.feature_masters.R
import com.app.feature_masters.di.DaggerMastersFeatureComponent
import com.app.feature_masters.models.MastersAdapter
import com.app.msa_nav_api.navigation.AppNavigator
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import com.app.mscorebase.ui.MSFragment
import com.app.mscorebase.ui.dialogs.messagedialog.DialogFragmentPresenter
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import javax.inject.Inject

class MastersFragment : MSFragment<MastersViewModel>() {

    private lateinit var mastersList: RecyclerView

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
        protected set

    @Inject
    lateinit var appNavigator: AppNavigator
        protected set

    @Inject
    lateinit var mastersAdapter: MastersAdapter
        protected set

    override val layoutId = R.layout.masters_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mastersAdapter.setOnMasterClickListener { master ->
            appNavigator.navigateToEditMasterFragment(this, master.id, REQ_EDIT_MASTER,
                EDIT_MASTER_FRAGMENT_TAG)
        }
        mastersAdapter.setOnMasterDeleteListener { master ->
            MessageDialogFragment.showMessage(this, getString(R.string.title_warning),
                String.format(getString(R.string.str_delete_master), master.name),
                DialogFragmentPresenter.ICON_WARNING, REQ_DELETE_MASTER,
                DialogFragmentPresenter.TWO_BUTTONS_YN,
                bundleOf(Pair(MASTER_ID, master.id))
            )
        }
        mastersList = view.findViewById(R.id.masters_list)
        mastersList.layoutManager = LinearLayoutManager(context)
        mastersList.adapter = mastersAdapter as RecyclerView.Adapter<*>
    }

    override fun createViewModel(savedInstanceState: Bundle?): MastersViewModel {
        return ViewModelProvider(this, providerFactory).get(MastersViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerMastersFeatureComponent
            .builder()
            .mastersFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onStartObservingViewModel(viewModel: MastersViewModel) {
        viewModel.masters.observe(this, Observer { masters ->
            mastersAdapter.setItems(masters)
        })
    }

    override fun onClickDialogButton(
        dialog: DialogInterface?,
        whichButton: Int,
        requestCode: Int,
        params: Bundle?
    ) {
        if (requestCode == REQ_DELETE_MASTER) {
            if (whichButton == DialogInterface.BUTTON_POSITIVE){
                getViewModel()?.deleteMaster(params?.getString(MASTER_ID))
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
    }

    companion object {
        fun newInstance() = MastersFragment()

        const val EDIT_MASTER_FRAGMENT_TAG = "NewMasterDialogFragment"
        const val REQ_EDIT_MASTER = 10002
        const val REQ_DELETE_MASTER = 10003
        val MASTER_ID = "${MastersFragment::class.java.simpleName}_MASTER_ID"
    }
}
