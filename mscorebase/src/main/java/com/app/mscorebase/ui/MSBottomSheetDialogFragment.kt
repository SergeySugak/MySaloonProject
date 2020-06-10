package com.app.mscorebase.ui

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.app.mscorebase.ui.dialogs.DialogButtonClickListener
import com.app.mscorebase.ui.dialogs.messagedialog.DialogFragmentPresenter
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class MSBottomSheetDialogFragment <VM : MSFragmentViewModel>: BottomSheetDialogFragment(), MSContext<VM>,
    ViewModelHolder<VM>,
    DialogButtonClickListener, OnBackPressedListener  {

    private val TAG = javaClass.simpleName
    private lateinit var viewModel: VM
    private val mainHandler = Handler()

    @get:LayoutRes
    abstract val layoutId: Int

    override fun getViewModel(): VM? {
        return if (::viewModel.isInitialized) viewModel else null
    }

    abstract fun createViewModel(savedInstanceState: Bundle?): VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel(savedInstanceState)
        onViewModelCreated(viewModel, savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (layoutId != 0) {
            return inflater.inflate(layoutId, container, false)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveInstanceState()
        super.onSaveInstanceState(outState)
    }

    protected open fun onViewModelCreated(viewModel: VM, savedInstanceState: Bundle?) {
        viewModel.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        onStartObservingViewModel(viewModel)
    }

    protected abstract fun onStartObservingViewModel(viewModel: VM)

    override fun onClickDialogButton(dialog: DialogInterface?, @DialogFragmentPresenter.WhichButton whichButton: Int,
                                     requestCode: Int, params: Bundle?) {}

    override fun onDestroyView() {
        mainHandler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacksAndMessages(null)
        viewModel.onDestroy()
    }

    override fun onBackPressed(): Boolean {
        clearViewModelInstanceState()
        return true
    }

    override fun showDialogFragment(
        newFragment: DialogFragment?,
        tag: String?
    ) {
        newFragment!!.setTargetFragment(this, 0)
        val fm = parentFragmentManager
        val ft = fm.beginTransaction()
        val prev = fm.findFragmentByTag(tag)
        if (prev != null) {
            ft.remove(prev)
        }
        newFragment.show(ft, tag)
    }

    override fun hideDialogFragment(tag: String?) {
        try {
            if (activity != null) {
                val fm =
                    requireActivity().supportFragmentManager
                val ft = fm.beginTransaction()
                val prev = fm.findFragmentByTag(tag)
                if (prev != null) ft.remove(prev)
                ft.commit()
            }
        } catch (e: Throwable) {
            if (activity != null) {
                MessageDialogFragment.showError(this, e)
            }
        }
    }

    override fun setInProgress(inProgress: Boolean) {
        viewModel.setInProgress(inProgress)
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