package app.benaya.mystory.register

import app.benaya.mystory.R
import app.benaya.mystory.RegisterViewModelFactory
import app.benaya.mystory.databinding.ActivityRegisterBinding
import app.benaya.mystory.settings.lightStatusBar
import app.benaya.mystory.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import android.annotation.SuppressLint
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
import androidx.lifecycle.ViewModelProvider

@Suppress("KotlinConstantConditions")
class ActivityRegister : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val delayTime: Long = 1000
    private val viewModel: ViewModelRegister by lazy {
        ViewModelProvider(this, RegisterViewModelFactory(this))[ViewModelRegister::class.java]
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        lightStatusBar(window, true)
        setContentView(binding.root)

        val tvLogin = binding.tvLogin
        val tvFirstPart = getString(R.string.INFO_TEXT_LOGIN)
        val tvSecondPart = " " + getString(R.string.TEXT_LOGIN)
        val loginSpannable = generateSpannableString(tvFirstPart, tvSecondPart)
        tvLogin.text = loginSpannable
        tvLogin.movementMethod = LinkMovementMethod.getInstance()

        viewModel.isLoadingRegister.observe(this) {
            showLoading(it)
        }

        viewModel.registerStatus.observe(this) { registerStatus ->
            val isError = viewModel.isErrorRegister

            if (isError && !registerStatus.isNullOrEmpty()) {
                Snackbar.make(binding.root, registerStatus, Snackbar.LENGTH_SHORT).show()
                if (isError && registerStatus.isNotEmpty() && registerStatus == "Email is already taken") {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.ERROR_EMAIL_ALREADY_REGISTER),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else if (!isError && !registerStatus.isNullOrEmpty()) {
                Snackbar.make(binding.root, registerStatus, Snackbar.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@ActivityRegister, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }, delayTime)
            }
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPass.text.toString()
            val confirmPassword = binding.etConfirmPass.text.toString()

            val nameError = if (name.isEmpty()) {
                getString(R.string.ERROR_EMPTY_NAME)
            } else if (name.length > 25) {
                getString(R.string.ERROR_TOOLONG_NAME)
            } else null

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

            val confirmPasswordError = if (confirmPassword.isEmpty()) {
                getString(R.string.ERROR_EMPTY_PASSWORD)
            } else if (password != confirmPassword) {
                getString(R.string.ERROR_MISMATCH_PASSWORD)
            } else null

            binding.apply {
                etName.error = nameError
                etEmail.error = emailError
                etPass.error = passwordError
                etConfirmPass.error = confirmPasswordError
            }

            val allErrors = listOf(nameError, emailError, passwordError, confirmPasswordError)
            val anyErrors = allErrors.any { it != null }

            if (anyErrors) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.ERROR_EMPTY_EDITEXT),
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                viewModel.register(name, email, password)
            }
        }
    }

    private fun generateSpannableString(firstPart: String, secondPart: String): Spannable {
        val spannable = SpannableString(firstPart + secondPart)
        val boldStyleSpan = StyleSpan(Typeface.BOLD)

        val clickableSpan2 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@ActivityRegister, LoginActivity::class.java)
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
