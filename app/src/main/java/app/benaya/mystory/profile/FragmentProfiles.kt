package app.benaya.mystory.profile

import app.benaya.mystory.settings.ViewModelAuthentication
import app.benaya.mystory.R
import app.benaya.mystory.settings.PreferencesForSetting
import app.benaya.mystory.databinding.FragmentProfileBinding
import app.benaya.mystory.settings.lightStatusBar
import app.benaya.mystory.login.LoginActivity
import app.benaya.mystory.login.dataStore
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_LOCALE_SETTINGS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class FragmentProfiles : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var prefen: PreferencesForSetting
    private lateinit var name: String
    private lateinit var uid: String
    private lateinit var email: String
    private lateinit var lastlogin: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefen = PreferencesForSetting.getInstance(requireContext().dataStore)
        lightStatusBar(requireActivity().window, true)
        floatingClick()

        val viewModelAuthentication =
            ViewModelProvider(
                this,
                ModelFactoryProfiles(prefen)
            )[ViewModelAuthentication::class.java]
        viewModelAuthentication.getUserLoginName()
            .observe(viewLifecycleOwner) {
                name = it
                binding.tvName.text = name
            }
        viewModelAuthentication.getUserLoginUid()
            .observe(viewLifecycleOwner) {
                uid = it
                binding.tvUid.text = uid
            }
        viewModelAuthentication.getUserLoginEmail()
            .observe(viewLifecycleOwner) {
                email = it
                binding.tvEmail.text = email
            }
        viewModelAuthentication.getUserLastLoginSession()
            .observe(viewLifecycleOwner) {
                lastlogin = it
                binding.tvLastlogin.text = getString(R.string.LAST_LOGIN) + " $lastlogin"
            }
        binding.tvLogout.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val alert = builder.create()
            builder
                .setTitle(getString(R.string.CONFIRMATION_LOGOUT_TITLE))
                .setMessage(getString(R.string.CONFIRMATION_LOGOUT_MESSAGE))
                .setPositiveButton(getString(R.string.CONFIRMATION_LOGOUT_YES)) { _, _ ->
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    viewModelAuthentication.deleteUserLoginSession()
                }
                .setNegativeButton(getString(R.string.CONFIRMATION_LOGOUT_CANCEL)) { _, _ ->
                    alert.cancel()
                }
                .show()
        }
    }

    private fun openInstagram() {
        try {
            val uri = Uri.parse("https://instagram.com/_u/louis_michael_")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } catch (_: Exception) {
        }
    }

    private fun floatingClick() {
        binding.apply {
            tvLanguage.setOnClickListener {
                startActivity(Intent(ACTION_LOCALE_SETTINGS))
            }
            tvReport.setOnClickListener {
                openInstagram()
            }
        }
    }
}
