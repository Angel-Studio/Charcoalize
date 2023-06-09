package fr.julespvx.charcoalize.data

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.content.pm.LauncherApps.ShortcutQuery
import android.content.pm.LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC
import android.content.pm.LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST
import android.content.pm.LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.ShortcutInfo
import android.graphics.drawable.Drawable
import android.os.Process
import android.os.UserHandle
import android.util.Log
import androidx.compose.runtime.mutableStateListOf


data class AppInfo(
    val packageManager: PackageManager,
    var label: CharSequence,
    var packageName: CharSequence,
    var icon: Drawable,
) {
    val intent: Intent?
        get() = packageManager.getLaunchIntentForPackage(packageName.toString())?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } ?: run {
            Log.e("AppInfo", "No intent found for $packageName")
            null
        }
}

class App {
    var icon: Drawable
    var label: String
    var packageName: String
    var className: String
    var userHandle: UserHandle? = null
    var shortcutInfo: List<ShortcutInfo>
    var intent: Intent

    constructor(packageManager: PackageManager?, info: ResolveInfo, shortcutInfo: List<ShortcutInfo>) {
        icon = info.loadIcon(packageManager)
        label = info.loadLabel(packageManager).toString()
        packageName = info.activityInfo.packageName
        className = info.activityInfo.name
        this.shortcutInfo = shortcutInfo

        intent = packageManager?.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } ?: run {
            Log.e("AppInfo", "No intent found for $packageName")
            Intent()
        }
    }

    @SuppressLint("NewApi")
    constructor(packageManager: PackageManager?, info: LauncherActivityInfo, shortcutInfo: List<ShortcutInfo>) {
        // check for this app's icon
        icon = info.getIcon(0)
        label = info.label.toString()
        packageName = info.componentName.packageName
        className = info.name
        this.shortcutInfo = shortcutInfo

        intent = packageManager?.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } ?: run {
            Log.e("AppInfo", "No intent found for $packageName")
            Intent()
        }
    }

    val componentName: String
        get() = ComponentName(packageName, className).toString()
}

class PersistentData {
    companion object {

        val apps = mutableStateListOf<App>()

        fun loadApps(context: Context) {
            val packageManager = context.packageManager

            val nonFilteredApps = mutableListOf<App>()

            val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
            val profiles = launcherApps.profiles
            for (userHandle in profiles) {
                val apps = launcherApps.getActivityList(null, userHandle)
                for (info in apps) {
                    val shortcutInfo: List<ShortcutInfo> =
                        getShortcutInfo(context, info.componentName.packageName)
                    Log.d("AppInfo", "Found ${shortcutInfo.size} shortcuts for ${info.componentName.packageName}")
                    val app = App(
                        packageManager,
                        info,
                        shortcutInfo
                    )
                    app.userHandle = userHandle
                    nonFilteredApps.add(app)
                }
            }

            nonFilteredApps.sortBy { it.label }
            apps.clear()
            apps.addAll(nonFilteredApps)

            /*val appInfos = mutableListOf<AppInfo>()

            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)

            val allApps = packageManager.queryIntentActivities(intent, 0)
            for (resolveInfo in allApps) {
                if (resolveInfo == null) continue
                val app = AppInfo(
                    packageManager = packageManager,
                    label = resolveInfo.loadLabel(packageManager),
                    packageName = resolveInfo.activityInfo.packageName,
                    icon = resolveInfo.activityInfo.loadIcon(packageManager)
                )
                appInfos.add(app)
            }

            apps.clear()
            appInfos.sortBy { it.label.toString() }
            apps.addAll(appInfos)*/
        }
    }
}

fun getShortcutInfo(context: Context, packageName: String): List<ShortcutInfo> {
    var shortcutInfo: List<ShortcutInfo>? = null
    val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    val shortcutQuery = ShortcutQuery()
    shortcutQuery.setQueryFlags(FLAG_MATCH_DYNAMIC or FLAG_MATCH_MANIFEST or FLAG_MATCH_PINNED)
    shortcutQuery.setPackage(packageName)
    try {
        shortcutInfo = launcherApps.getShortcuts(shortcutQuery, Process.myUserHandle())
    } catch (e: SecurityException) {
        Log.w(
            "AppInfo",
            "Can't get shortcuts info. App is not set as default launcher"
        )
    }

    return shortcutInfo ?: emptyList()
}