package app.benaya.mystory.story

import app.benaya.mystory.PagingStoryModelFactory
import app.benaya.mystory.adapters.LoadingStateAdapters
import app.benaya.mystory.settings.ViewModelAuthentication
import app.benaya.mystory.settings.PreferencesForSetting
import app.benaya.mystory.adapters.StoriesAdapter
import app.benaya.mystory.data.local.StoryEntity
import app.benaya.mystory.databinding.FragmentStoryBinding
import app.benaya.mystory.settings.lightStatusBar
import app.benaya.mystory.login.dataStore
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager


class FragmentStories : Fragment() {
    private lateinit var binding: FragmentStoryBinding
    private lateinit var prefen: PreferencesForSetting
    private lateinit var token: String
    private val viewModel: ViewModelStories by lazy {
        ViewModelProvider(
            this,
            PagingStoryModelFactory(requireContext())
        )[ViewModelStories::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefen = PreferencesForSetting.getInstance(requireContext().dataStore)
        lightStatusBar(requireActivity().window, true)
        floatingClick()
        showRecyclerView()

        val viewModelAuthentication =
            ViewModelProvider(
                this,
                ModelFactoryStories(prefen)
            )[ViewModelAuthentication::class.java]
        viewModelAuthentication.getUserLoginToken().observe(viewLifecycleOwner) {
            token = it
            setStoryData(it)
        }
    }

    private fun showRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvStory.layoutManager = layoutManager
        binding.rvStory.setHasFixedSize(true)
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun setStoryData(token: String) {
        val storiesAdapter = StoriesAdapter()
        binding.rvStory.adapter = storiesAdapter.withLoadStateFooter(
            footer = LoadingStateAdapters {
                storiesAdapter.retry()
            })

        viewModel.getPagingStory(token).observe(viewLifecycleOwner) {
            storiesAdapter.submitData(lifecycle, it)
        }

        storiesAdapter.setOnItemClickCallback(object : StoriesAdapter.OnItemClickCallback {
            override fun onItemClicked(data: StoryEntity) {
                selectedStory(data)
            }
        })
    }

    private fun floatingClick() {
        binding.apply {
            btnFloating.setOnClickListener {
                val intent = Intent(requireContext(), ActivityStoriesUpload::class.java)
                startActivity(intent)
            }
        }
    }

    private fun selectedStory(story: StoryEntity) {
        val intent = Intent(requireContext(), DetailActivityStories::class.java)
        intent.putExtra(DetailActivityStories.EXTRA_DETAIL_STORY, story)
        intent.putExtra(DetailActivityStories.EXTRA_DETAIL_STORY_ID, story.id)
        startActivity(intent)
    }
}
