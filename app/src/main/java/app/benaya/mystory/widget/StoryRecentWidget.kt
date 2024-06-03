package app.benaya.mystory.widget

import app.benaya.mystory.R
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.net.toUri


class StoryRecentWidget : AppWidgetProvider() {

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {

        val intent = Intent(context, StackWidgetService::class.java).also {
            it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            it.data = it.toUri(Intent.URI_INTENT_SCHEME).toUri()
        }

        val views = RemoteViews(context.packageName, R.layout.story_recent_widget).also {
            it.setRemoteAdapter(R.id.stack_view, intent)
            it.setEmptyView(R.id.stack_view, R.id.tv_empty)
        }

        val toastIntent = Intent(context, StoryRecentWidget::class.java).also {
            it.action = TOAST_ACTION
            it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }

        val toastPendingIntent = PendingIntent.getBroadcast(
            context, 0, toastIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            else 0
        )

        views.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }

    companion object {
        private const val TOAST_ACTION =
            "app.benaya.mystory.widget.StoryRecentWidget.TOAST_ACTION"
    }
}