package app.benaya.mystory.story

import app.benaya.mystory.R
import app.benaya.mystory.data.local.StoryEntity
import app.benaya.mystory.settings.convertDateTime
import app.benaya.mystory.databinding.ActivityStoryDetailBinding
import app.benaya.mystory.settings.LocationConverter
import app.benaya.mystory.settings.lightStatusBar
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

@Suppress("DEPRECATION")
class DetailActivityStories : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        lightStatusBar(window, true)
        setContentView(binding.root)
        backButton()

        val storyDetail: Parcelable? = intent.getParcelableExtra(EXTRA_DETAIL_STORY)
        if (storyDetail != null) {
            when (storyDetail) {
                is StoryEntity -> showViewModel(storyDetail)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showViewModel(story: StoryEntity) {
        binding.apply {
            tvDetailStory.text = getString(R.string.MSG_DETAIL_STORIES) + " ${story.name}"
            tvUsername.text = story.name
            tvCapstion.text = story.description
            tvDate.text = convertDateTime(story.createdAt)
            tvLocation.text = LocationConverter.getStringAddress(
                LocationConverter.convertLatLng(story.lat, story.lon),
                this@DetailActivityStories
            )
        }
        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.imgStory)
    }

    private fun backButton() {
        binding.apply {
            bckDetailStory.setOnClickListener {
                finish()
            }
        }
    }

    companion object {
        const val EXTRA_DETAIL_STORY = "extra_detail_story"
        const val EXTRA_DETAIL_STORY_ID = "extra_detail_story_id"
    }
}