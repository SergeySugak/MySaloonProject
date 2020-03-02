package com.app.mscorebase.ui

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.app.mscorebase.ui.dialogs.DialogButtonClickListener
import com.app.mscorebase.ui.dialogs.messagedialog.DialogFragmentPresenterImpl
import com.app.mscorebase.ui.dialogs.messagedialog.DialogFragmentPresenterImpl.WhichButton
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment.Companion.hide
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment.Companion.showError
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment.Companion.showMessage

abstract class MSFragment<VM : MSFragmentViewModel> :
    Fragment(), MSContext<VM>, ViewModelHolder<VM>,
    DialogButtonClickListener, OnBackPressedListener {
    private val TAG = javaClass.simpleName
    private lateinit var viewModel: VM
    private val mainHandler = Handler()
    private var optionsMenu: Menu? = null

    /**
     * Override for set layout resource
     *
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    abstract fun createViewModel(savedInstanceState: Bundle?): VM

    override fun getViewModel(): VM {
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val result = inflater.inflate(layoutId, container, false)
        viewModel = createViewModel(savedInstanceState)
        onViewModelCreated(viewModel, savedInstanceState)
        if (activity != null) {
            activity!!.invalidateOptionsMenu()
        }
        return result
    }

    override fun onResume() {
        super.onResume()
        viewModel.let { onStartObservingViewModel(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveInstanceState()
        super.onSaveInstanceState(outState)
    }

    protected open fun onViewModelCreated(viewModel: VM, savedInstanceState: Bundle?) {
        viewModel.onCreate(savedInstanceState)
    }

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

    override fun setInProgress(inProgress: Boolean) {
        viewModel.setInProgress(inProgress)
    }

    fun setTitle(fVM: MSFragmentViewModel) {
        if (activity is MSActivity<*>) {
            val activity = activity as MSActivity<*>
            val viewModel = activity.getViewModel()
            viewModel.apply {
                _title.value = fVM.title.value
                _subtitle.value = fVM.subtitle.value
            }
        }
    }

    internal fun onFragmentResume() {}
    override fun onDestroyView() {
        mainHandler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacksAndMessages(null)
        viewModel.onDestroy()
    }

    override fun onCreateOptionsMenu(
        optMenu: Menu,
        inflater: MenuInflater
    ) {
        @MenuRes val menuId: Int = menu
        if (menuId != 0) {
            inflater.inflate(menuId, optMenu)
            optionsMenu = optMenu
        }
        super.onCreateOptionsMenu(optMenu, inflater)
    }

    fun showError(error: Throwable, requestCode: Int) {
        showError(this, error, requestCode)
    }

    fun showError(error: Throwable) {
        showError(this, error, MessageDialogFragment.REQUEST_CODE_NONE)
    }

    override fun showDialogFragment(
        newFragment: DialogFragment?,
        tag: String?
    ) {
        newFragment!!.setTargetFragment(this, 0)
        val fm = fragmentManager
        val ft = fm!!.beginTransaction()
        val prev = fm.findFragmentByTag(tag)
        if (prev != null) {
            ft.remove(prev)
        }
        newFragment.show(ft, tag)
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
                    showMessage(
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
                showMessage(
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
                mainHandler.post { hide(fragment) }
            }
        } else {
            mainHandler.post { hide(activity) }
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
                showError(activity!!, e)
            }
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