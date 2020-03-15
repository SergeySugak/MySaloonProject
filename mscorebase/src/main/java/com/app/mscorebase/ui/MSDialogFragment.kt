package com.app.mscorebase.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.fragment.app.DialogFragment
import com.app.mscorebase.ui.dialogs.DialogButtonClickListener
import com.app.mscorebase.ui.dialogs.messagedialog.DialogFragmentPresenterImpl
import com.app.mscorebase.ui.dialogs.messagedialog.DialogFragmentPresenterImpl.WhichButton
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment

abstract class MSDialogFragment<VM : MSFragmentViewModel> :
    DialogFragment(), MSContext<VM>,
    ViewModelHolder<VM>,
    DialogButtonClickListener, OnBackPressedListener {
    private val TAG = javaClass.simpleName
    private lateinit var viewModel: VM
    private val mainHandler = Handler()

    /**
     * Override to set layout resource
     *
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    abstract fun createViewModel(savedInstanceState: Bundle?): VM

    override fun getViewModel(): VM? {
        return if (::viewModel.isInitialized) viewModel else null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(savedInstanceState)
        if (layoutId != 0 && activity != null) {
            val inflater = activity!!.layoutInflater
            inflater.inflate(layoutId, null, false)
        }
        onViewModelCreated(viewModel, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveInstanceState()
        super.onSaveInstanceState(outState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = onBuildDialog(savedInstanceState)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    protected open fun onViewModelCreated(viewModel: VM, savedInstanceState: Bundle?) {
        viewModel.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        onStartObservingViewModel(viewModel)
    }

    protected abstract fun onBuildDialog(savedInstanceState: Bundle?): Dialog

    /*
    Реализация должна выглядеть как-нибудь типа
    viewModel.viewModelEventSender.observe(this, viewModelEvent -> {
        if (viewModelEvent.isOneTimer()){
            if (viewModelEvent.isProcessed()) {
                return;
            }
            else {
                viewModelEvent.stopProcessing();
            }
        }
        viewModel.onViewModelEvent(this, viewModelEvent);
    });
    */
    protected abstract fun onStartObservingViewModel(viewModel: VM)

    @get:MenuRes
    val menu: Int
        get() = 0

    fun fabIsShow(): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    fun setTitle(fVM: MSFragmentViewModel) {
        if (activity is MSActivity<*>) {
            val activity = activity as MSActivity<*>
            val viewModel = activity.getViewModel()
            viewModel?.apply {
                _title.value = fVM.title.value
                _subtitle.value = fVM.subtitle.value
            }
        }
    }

    protected fun onFragmentResume() {}

    override fun onDestroyView() {
        mainHandler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacksAndMessages(null)
        viewModel.onDestroy()
    }

    override fun onCreateOptionsMenu(optMenu: Menu, inflater: MenuInflater) {
        @MenuRes val menuId: Int = menu;
        if (menuId != 0) {
            inflater.inflate(menuId, optMenu)
        }
        super.onCreateOptionsMenu(optMenu, inflater)
    }

    fun showError(error: Throwable, requestCode: Int) {
        MessageDialogFragment.showError(this, error, requestCode)
    }

    fun showError(error: Throwable) {
        MessageDialogFragment.showError(this, error, MessageDialogFragment.REQUEST_CODE_NONE)
    }

    override fun showDialogFragment(
        newFragment: DialogFragment?,
        tag: String?
    ) {
        newFragment!!.setTargetFragment(this, 0)
        val fm = parentFragmentManager
        if (fm != null) {
            val ft = fm.beginTransaction()
            val prev = fm.findFragmentByTag(tag)
            if (prev != null) {
                ft.remove(prev)
            }
            newFragment.show(ft, tag)
        }
    }

    override fun hideDialogFragment(tag: String?) {
        try {
            if (activity != null) {
                val fm =
                    activity!!.supportFragmentManager
                val ft = fm.beginTransaction()
                val prev = fm.findFragmentByTag(tag)
                if (prev != null) ft.remove(prev)
                ft.commit()
            }
        } catch (e: Throwable) {
            if (activity != null) {
                MessageDialogFragment.showError(activity!!, e)
            }
        }
    }

    override fun setInProgress(inProgress: Boolean) {
        viewModel.setInProgress(inProgress)
    }

    //    protected void defShowErrorEventHandler(ViewEvent event) {
//        if (event != null && !event.isProcessed()) {
//            event.stopProcessing();
//            Throwable throwable = (Throwable) event.get(LDE_P_SHOW_ERROR_EX);
//            Integer requestCode = (Integer) event.get(LDE_P_SHOW_ERROR_RC);
//            String tag = (String)event.get(LDE_P_SHOW_ERROR_TAG);
//            if (requestCode == null) {
//                requestCode = REQUEST_CODE_SHOW_ERROR;
//            }
//            Bundle params = new Bundle();
//            params.putBoolean(LDE_P_SHOW_ERROR_EVENT, true);
//            if (TextUtils.isEmpty(tag)) {
//                MessageDialogFragment.showError(this, throwable, requestCode, params);
//            }
//            else {
//                MessageDialogFragment.showError(this, throwable, requestCode, params, tag);
//            }
//        }
//    }
    override fun onClickDialogButton(
        dialog: DialogInterface?, @WhichButton whichButton: Int,
        requestCode: Int,
        params: Bundle?
    ) { //        if (requestCode == REQUEST_CODE_SHOW_ERROR &&
//                params != null &&
//                params.getBoolean(LDE_P_SHOW_ERROR_EVENT, false) &&
//                getViewModel() != null &&
//                getViewModel().getErrorVMEvent().getValue() != null &&
//                !getViewModel().getErrorVMEvent().getValue().isProcessed()) {
//            getViewModel().getErrorVMEvent().getValue().interrupt();
//        }
    }

    fun showWaitingPanel(title: String?, message: String?) {
        val activity = activity
        if (activity == null) {
            val fragment = targetFragment
            if (fragment != null) {
                mainHandler.post {
                    MessageDialogFragment.showMessage(
                        fragment,
                        title,
                        message,
                        DialogFragmentPresenterImpl.ICON_INFO,
                        MessageDialogFragment.REQUEST_CODE_NONE,
                        DialogFragmentPresenterImpl.NO_BUTTONS
                    )
                }
            }
        } else {
            mainHandler.post {
                MessageDialogFragment.showMessage(
                    activity,
                    title,
                    message,
                    DialogFragmentPresenterImpl.ICON_INFO,
                    MessageDialogFragment.REQUEST_CODE_NONE,
                    DialogFragmentPresenterImpl.NO_BUTTONS
                )
            }
        }
    }

    fun hideWaitingPanel() {
        val activity = activity
        if (activity == null) {
            val fragment = targetFragment
            if (fragment != null) {
                mainHandler.post { MessageDialogFragment.hide(fragment) }
            }
        } else {
            mainHandler.post { MessageDialogFragment.hide(activity) }
        }
    }

    override fun onBackPressed(): Boolean {
        clearViewModelInstanceState()
        return true
    }

    override fun clearViewModelInstanceState() {
        viewModel.clearInstanceState()
        for (fragment in childFragmentManager.fragments){
            if (fragment is ViewModelHolder<*>){
                fragment.clearViewModelInstanceState()
            }
        }
    }
}