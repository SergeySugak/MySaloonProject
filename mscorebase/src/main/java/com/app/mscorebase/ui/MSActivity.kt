package com.app.mscorebase.ui

import android.annotation.TargetApi
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Debug
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.app.mscorebase.R
import com.app.mscorebase.ui.dialogs.messagedialog.DialogFragmentPresenterImpl
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.google.android.material.navigation.NavigationView

abstract class MSActivity<VM : MSViewModel> :
    AppCompatActivity(), MSContext<VM>,
    ViewModelHolder<VM>,
    FragmentManager.OnBackStackChangedListener {
    private val TAG = javaClass.simpleName

    private lateinit var viewModel: VM
    private val mainHandler = Handler()
    private var optionsMenu: Menu? = null

    abstract fun createViewModel(savedInstanceState: Bundle?): VM

    /**
     * Override to set layout resource
     *
     * @return layout resource id
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    private fun bind(savedInstanceState: Bundle?) {
        setContentView(getLayoutId())
        viewModel = createViewModel(savedInstanceState)
        onViewModelCreated(viewModel, savedInstanceState)
        supportFragmentManager.addOnBackStackChangedListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveInstanceState()
        super.onSaveInstanceState(outState)
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

    protected open fun onViewModelCreated(viewModel: VM, savedInstanceState: Bundle?) {
        viewModel.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
        onStartObservingViewModel(viewModel)
    }

    override fun getViewModel(): VM {
        return viewModel
    }

    @MenuRes
    open fun getMenu(): Int {
        return 0
    }

    @IdRes
    open fun getHostFragmentId(): Int {
        return 0
    }

    override fun onBackStackChanged() {
        val currFrag = getCurrentFragment()
        if (currFrag is MSFragment<*>) currFrag.onFragmentResume()
    }

    fun getCurrentFragment(): Fragment? {
        val manager = supportFragmentManager
        @IdRes val id = getHostFragmentId()
        return if (id != 0) manager.findFragmentById(id) else null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind(savedInstanceState)
        invalidateOptionsMenu()
    }

    override fun setTitle(title: CharSequence) {
        if (Debug.isDebuggerConnected()/* || BuildConfig.DEBUG*/) {
            super.setTitle(title.toString() + "\n[" + javaClass.canonicalName + "]")
        } else {
            super.setTitle(title)
        }
    }

    override fun setTitle(titleId: Int) {
        super.setTitle(titleId)
        if (Debug.isDebuggerConnected() /*|| BuildConfig.DEBUG*/) {
            super.setTitle(title.toString() + "\n[" + javaClass.canonicalName + "]")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuRes = getMenu()
        if (menuRes > 0) {
            menuInflater.inflate(menuRes, menu)
            optionsMenu = menu
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.removeOnBackStackChangedListener(this)
        mainHandler.removeCallbacksAndMessages(null)
        if (isFinishing) {
            viewModel.onDestroy()
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//												User navigations												  //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onBackPressed() {
        viewModel.clearInstanceState()
        overridePendingTransitionExit()
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @CallSuper
    protected open fun setupActionBar() { // Show the Up button in the action bar.
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            if (Debug.isDebuggerConnected() /*|| BuildConfig.DEBUG*/) {
                actionBar.subtitle =
                    (if (actionBar.subtitle == null) "" else actionBar.subtitle).toString() +
                            " [" + javaClass.canonicalName + "]"
            }
        }
    }

    protected open fun getNavigationView(): NavigationView? {
        return null
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransitionEnter()
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        overridePendingTransitionEnterResult()
    }

    override fun finish() {
        clearViewModelInstanceState()
        clearFragmentsInstanceStates()
        super.finish()
        overridePendingTransitionExit()
    }

    override fun clearViewModelInstanceState() {
        viewModel.clearInstanceState()
    }

    private fun clearFragmentsInstanceStates() {
        for (fragment in supportFragmentManager.fragments){
            if (fragment is ViewModelHolder<*>) {
                fragment.clearViewModelInstanceState()
            }
        }
    }

    fun getOptionsMenu(): Menu? {
        return optionsMenu
    }

    @JvmOverloads
    fun addFragment(
        @IdRes containerViewId: Int,
        fragment: Fragment,
        fragmentTag: String,
        addToBackStack: Boolean = true,
        backStackStateName: String? = null
    ) {
        val ft = supportFragmentManager.beginTransaction()
        ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.add(containerViewId, fragment, fragmentTag)
        if (addToBackStack) ft.addToBackStack(backStackStateName)
        ft.commit()
    }

    protected fun removeAllAndReplaceFragments(
        @IdRes containerViewId: Int,
        fragment: Fragment,
        fragmentTag: String,
        addToBackStack: Boolean,
        backStackStateName: String?
    ) {
        try {
            supportFragmentManager.popBackStackImmediate(
                0,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            val ft =
                supportFragmentManager.beginTransaction()
            ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ft.replace(containerViewId, fragment, fragmentTag)
            if (addToBackStack) ft.addToBackStack(backStackStateName)
            ft.commit()
        } catch (e: Exception) {
            MessageDialogFragment.showError(this, e)
        }
    }

    override fun showDialogFragment(
        newFragment: DialogFragment?,
        tag: String?
    ) {
        try {
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()
            val prev = fm.findFragmentByTag(tag)
            if (prev != null) ft.remove(prev)
            newFragment!!.show(ft, tag)
        } catch (e: Throwable) {
            MessageDialogFragment.showError(this, e)
        }
    }

    override fun hideDialogFragment(tag: String?) {
        try {
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()
            val prev = fm.findFragmentByTag(tag)
            if (prev != null) ft.remove(prev)
            ft.commit()
        } catch (e: Throwable) {
            MessageDialogFragment.showError(this, e)
        }
    }

    fun showError(error: Throwable, requestCode: Int, tag: String?) {
        MessageDialogFragment.showError(this, error, requestCode)
    }

    fun showError(
        error: Throwable,
        stackTrace: Boolean,
        tag: String?
    ) {
        MessageDialogFragment.showError(
            this, error,
            MessageDialogFragment.REQUEST_CODE_NONE, stackTrace, tag
        )
    }

    protected fun overridePendingTransitionEnterResult() {
        overridePendingTransition(
            R.anim.slide_from_right,
            R.anim.slide_to_left
        )
    }

    protected fun overridePendingTransitionEnter() {
        overridePendingTransition(
            R.anim.slide_from_right,
            R.anim.slide_to_left
        )
    }

    protected fun overridePendingTransitionExit() {
        overridePendingTransition(
            R.anim.slide_from_right,
            R.anim.slide_to_left
        )
    }

    override fun setInProgress(inProgress: Boolean) {
        viewModel.setInProgress(inProgress)
    }

    override fun onClickDialogButton(
        dialog: DialogInterface?, @DialogFragmentPresenterImpl.WhichButton whichButton: Int,
        requestCode: Int,
        params: Bundle?
    ) {
    }

    protected fun showWaitingPanel(
        title: String?,
        message: String?
    ) {
        MessageDialogFragment.showMessage(
            this,
            title,
            message,
            DialogFragmentPresenterImpl.ICON_INFO,
            MessageDialogFragment.REQUEST_CODE_NONE,
            DialogFragmentPresenterImpl.NO_BUTTONS
        )
    }

    protected fun hideWaitingPanel() {
        MessageDialogFragment.hide(this)
    }

    override fun getContext(): Context {
        return this
    }

//Возможно потребуется потом
//	@Override
//	public boolean onSupportNavigateUp() {
//		Log.d(TAG, "onSupportNavigateUp");
//		return super.onSupportNavigateUp();
//	}
//
//	@Override
//	public boolean onNavigateUp() {
//		Log.d(TAG, "onNavigateUp");
//		return super.onNavigateUp();
//	}
//
//	@Override
//	public boolean onNavigateUpFromChild(Activity child) {
//		Log.d(TAG, "onNavigateUpFromChild");
//		return super.onNavigateUpFromChild(child);
//	}
}