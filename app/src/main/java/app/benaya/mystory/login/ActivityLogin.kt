package app.benaya.mystory.login

import app.benaya.mystory.settings.ViewModelAuthentication
import app.benaya.mystory.ActivityFragment
import app.benaya.mystory.LoginViewModelFactory
import app.benaya.mystory.R
import app.benaya.mystory.settings.PreferencesForSetting
import app.benaya.mystory.databinding.ActivityLoginBinding
import app.benaya.mystory.settings.getCurrentDateTime
import app.benaya.mystory.settings.lightStatusBar
import app.benaya.mystory.register.ActivityRegister
import app.benaya.mystory.settings.SETTINGS_KEY
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_KEY)

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val delayTime: Long = 1000

    private val viewModel: ModelLoginView by lazy {
        ViewModelProvider(this, LoginViewModelFactory(this))[ModelLoginView::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lightStatusBar(window, true)

        val prefen = PreferencesForSetting.getInstance(dataStore)
        val viewModelAuthentication =
            ViewModelProvider(this, FactoryLoginModel(prefen))[ViewModelAuthentication::class.java]

        val tvRegister = binding.tvRegister
        val tvFirstPart = getString(R.string.TEXT_INFO_REGISTER)
        val tvSecondPart = " " + getString(R.string.TEXT_REGISTER)
        val registerSpannable = generateSpannableString(tvFirstPart, tvSecondPart)
        tvRegister.text = registerSpannable
        tvRegister.movementMethod = LinkMovementMethod.getInstance()

        viewModel.isLoadingLogin.observe(this) {
            showLoading(it)
        }

        viewModel.loginStatus.observe(this) { loginStatus ->
            val isError = viewModel.isErrorLogin

            if (isError && !loginStatus.isNullOrEmpty()) {
                Snackbar.make(binding.root, loginStatus, Snackbar.LENGTH_SHORT).show()
            }
            if (isError && !loginStatus.isNullOrEmpty() && loginStatus == "User not found") {
                Snackbar.make(
                    binding.root,
                    getString(R.string.ERROR_NOTSAME_PASSWORD),
                    Snackbar.LENGTH_SHORT
                ).show()
            } else if (!isError && !loginStatus.isNullOrEmpty()) {
                val authLogin = viewModel.login.value
                val email = binding.etEmail.text.toString()
                viewModelAuthentication.saveUserLoginSession(true)
                viewModelAuthentication.saveUserLoginToken(authLogin?.loginResult!!.token)
                viewModelAuthentication.saveUserLoginName(authLogin.loginResult.name)
                viewModelAuthentication.saveUserLoginUid(authLogin.loginResult.userId)
                viewModelAuthentication.saveUserLoginEmail(email)
                viewModelAuthentication.saveUserLastLoginSession(getCurrentDateTime())
                Snackbar.make(binding.root, loginStatus, Snackbar.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@LoginActivity, ActivityFragment::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }, delayTime)
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPass.text.toString()

            val emailError = if (email.isEmpty()) {
                getString(R.string.ERROR_EMPTY_EMAIL)
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                getString(R.string.ERROR_INVALID_EMAIL_FORMAT)
            } else null

            val passwordError = if (password.isEmpty()) {
                getString(R.string.ERROR_EMPTY_PASSWORD)
            } else if (password.length < 8) {
                getString(R.string.ERROR_LENGTH_PASSWORD)
            } else null

            binding.etEmail.error = emailError
            binding.etPass.error = passwordError

            val allErrors = listOf(emailError, passwordError)
            val anyErrors = allErrors.any { it != null }

            if (anyErrors) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.ERROR_EMPTY_EDITEXT),
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                viewModel.login(email, password)
            }
        }
    }


    private fun generateSpannableString(firstPart: String, secondPart: String): Spannable {
        val spannable = SpannableString(firstPart + secondPart)
        val boldStyleSpan = StyleSpan(Typeface.BOLD)

        val clickableSpan2 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, ActivityRegister::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        val blueColor = ContextCompat.getColor(this, R.color.blue)
        spannable.setSpan(object : UnderlineSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }, 0, firstPart.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val startSecondPart = firstPart.length
        val endSecondPart = spannable.length
        spannable.setSpan(
            clickableSpan2,
            startSecondPart,
            endSecondPart,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(blueColor),
            startSecondPart,
            endSecondPart,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            boldStyleSpan,
            startSecondPart,
            endSecondPart,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannable
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}