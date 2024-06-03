package app.benaya.mystory.widget

import app.benaya.mystory.R
import app.benaya.mystory.data.local.StoryEntityDao
import app.benaya.mystory.data.local.database.EntityStoriesDatabase
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

internal class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = arrayListOf<Bitmap>()
    private lateinit var storydao: StoryEntityDao

    override fun onCreate() {
        storydao = EntityStoriesDatabase.getDatabase(mContext.applicationContext).storyentityDao()
    }

    override fun onDataSetChanged() {
        val tokenIdentifier = Binder.clearCallingIdentity()
        runBlocking(Dispatchers.IO) {
            try {
                storydao.getAllStory().map {
                    val bitmap = try {
                        Glide.with(mContext)
                            .asBitmap()
                            .load(it.photoUrl)
                            .submit()
                            .get()
                    } catch (e: Exception) {
                        BitmapFactory.decodeResource(mContext.resources, R.drawable.ic_applogo)
                    }
                    mWidgetItems.add(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        Binder.restoreCallingIdentity(tokenIdentifier)
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int {
        return mWidgetItems.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.list_item_widget)
        rv.setImageViewBitmap(R.id.wd_story, mWidgetItems[position])

        return rv
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return false
    }
}