package app.benaya.mystory

import app.benaya.mystory.databinding.ActivityFragmentBinding
import app.benaya.mystory.maps.FragmentMaps
import app.benaya.mystory.profile.FragmentProfiles
import app.benaya.mystory.story.FragmentStories
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


@Suppress("DEPRECATION")
class ActivityFragment : AppCompatActivity() {

    private lateinit var binding: ActivityFragmentBinding
    private var currentFragmentId: Int = R.id.beranda

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.beranda -> {
                    currentFragmentId = R.id.beranda
                    replaceFragment(FragmentStories())
                    true
                }

                R.id.maps -> {
                    currentFragmentId = R.id.maps
                    replaceFragment(FragmentMaps())
                    true
                }

                R.id.profile -> {
                    currentFragmentId = R.id.profile
                    replaceFragment(FragmentProfiles())
                    true
                }

                else -> false
            }
        }

        if (savedInstanceState != null) {
            currentFragmentId = savedInstanceState.getInt("currentFragmentId", R.id.beranda)
        }

        when (currentFragmentId) {
            R.id.beranda -> replaceFragment(FragmentStories())
            R.id.profile -> replaceFragment(FragmentProfiles())
            R.id.maps -> replaceFragment(FragmentMaps())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentFragmentId", currentFragmentId)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}