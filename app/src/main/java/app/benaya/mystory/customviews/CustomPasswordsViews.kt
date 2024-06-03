package app.benaya.mystory.customviews

import app.benaya.mystory.R
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText


class CustomPasswordsViews : AppCompatEditText, View.OnFocusChangeListener {

    private var isPasswordValid = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        setBackgroundResource(R.drawable.edit_text_background)
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        onFocusChangeListener = this
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus) {
            validatePassword()
        }
    }

    private fun validatePassword() {
        val password = text.toString().trim()
        val confirmPassword =
            (parent as ViewGroup).findViewById<CustomPasswordsViews>(R.id.et_Pass).text.toString()
                .trim()

        isPasswordValid =
            password.isNotEmpty() && password.length >= 8 && password == confirmPassword
        error = if (!isPasswordValid) {
            if (password.isEmpty()) {
                resources.getString(R.string.ERROR_EMPTY_PASSWORD)
            } else if (password.length < 8) {
                resources.getString(R.string.ERROR_LENGTH_PASSWORD)
            } else {
                resources.getString(R.string.ERROR_MISMATCH_PASSWORD)
            }
        } else {
            null
        }
    }
}