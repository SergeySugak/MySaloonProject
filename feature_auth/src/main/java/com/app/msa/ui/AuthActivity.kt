package com.app.msa.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.msa.auth.R
import com.app.msa.di.DaggerAuthComponent
import com.app.mscorebase.common.Result
import com.app.mscorebase.ui.MSActivity
import com.app.mscorebase.ui.dialogs.messagedialog.MessageDialogFragment
import com.app.mscorebase.utils.isOnline
import com.app.msa.navigation.AppNavigator
import com.app.mscorebase.di.ViewModelProviderFactory
import com.app.mscorebase.di.findComponentDependencies
import javax.inject.Inject

class AuthActivity : MSActivity<AuthActivity, AuthActivityViewModel>() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    @Inject
    lateinit var appNavigator: AppNavigator

    val username: EditText by lazy { findViewById<EditText>(R.id.username) }
    val password: EditText by lazy { findViewById<EditText>(R.id.password) }
    val login: Button by lazy { findViewById<Button>(R.id.login) }
    val loading: ProgressBar by lazy { findViewById<ProgressBar>(R.id.loading) }


    override fun getLayoutId() = R.layout.activity_auth

    override fun getThis() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerAuthComponent
            .builder()
            .authFeatureDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun createViewModel(savedInstanceState: Bundle?): AuthActivityViewModel {
        return ViewModelProvider(this, providerFactory).get(AuthActivityViewModel::class.java)
    }

    override fun onViewModelCreated(
        viewModel: AuthActivityViewModel,
        savedInstanceState: Bundle?
    ) {
        super.onViewModelCreated(viewModel, savedInstanceState)

        password.apply {
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> tryLogin(viewModel)
                }
                false
            }

            login.setOnClickListener {
                tryLogin(viewModel)
            }
        }
    }

    private fun tryLogin(viewModel: AuthActivityViewModel) {
        if (
            viewModel.validate(
                username.text.toString(),
                password.text.toString()
            )
        ) {
            if (isOnline(this)) {
                loading.visibility = View.VISIBLE
                viewModel.login(username.text.toString(), password.text.toString())
            }
            else {
                MessageDialogFragment.showError(this, Exception(getString(R.string.err_no_internet)), false)
            }
        }
    }

    override fun onStartObservingViewModel(viewModel: AuthActivityViewModel) {
        viewModel.authFormState.observe(this, Observer {
            val loginState = it ?: return@Observer

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        viewModel.loginResult.observe(this, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult is Result.Error) {
                showLoginFailed(loginResult.exception.message ?: getString(R.string.login_failed))
            }
            else {
                setResult(Activity.RESULT_OK)
                navigate()
                finish()
            }
        })

        viewModel.createAccountResult.observe(this, Observer {
            val createAccountResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (createAccountResult is Result.Error) {
                showLoginFailed(createAccountResult.exception.message ?: getString(R.string.login_failed))
            }
            else {
                showConfirmEmailMessage()
            }
        })

    }

    private fun showConfirmEmailMessage() {
        Toast.makeText(applicationContext, R.string.str_confirm_email, Toast.LENGTH_SHORT).show()
    }

    private fun navigate() {
        appNavigator.navigateToMainActivity(this)
    }

    private fun showLoginFailed(errorString: String) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}
