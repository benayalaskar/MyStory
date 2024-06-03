package app.benaya.mystory.splashscreen

import app.benaya.mystory.settings.ViewModelAuthentication
import app.benaya.mystory.settings.PreferencesForSetting
import app.benaya.mystory.databinding.ActivitySplashScreenBinding
import app.benaya.mystory.settings.lightStatusBar
import app.benaya.mystory.login.dataStore
import app.benaya.mystory.welcomes.ActivityWelcomes
import app.benaya.mystory.ActivityFragment
import app.benaya.mystory.R
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class ActivitySplashScreens : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        lightStatusBar(window, true)
        setContentView(binding.root)
        binding.logoapp.setImageResource(R.drawable.ic_applogo)

        val viewModelAuthentication = ViewModelProvider(
            this,
            ModelFactorySplashScreens(PreferencesForSetting.getInstance(dataStore))
        )[ViewModelAuthentication::class.java]

        viewModelAuthentication.getUserLoginSession().observe(this) { isLoggedIn ->
            val intent = Intent(
                this@ActivitySplashScreens,
                if (isLoggedIn) ActivityFragment::class.java else ActivityWelcomes::class.java
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            window.decorView.postDelayed({ startActivity(intent) }, 2000L)
        }
    }
}