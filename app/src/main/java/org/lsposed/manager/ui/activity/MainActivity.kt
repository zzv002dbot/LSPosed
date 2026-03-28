package org.lsposed.manager.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.lsposed.manager.App
import org.lsposed.manager.ConfigManager
import org.lsposed.manager.ui.activity.base.BaseActivity
import org.lsposed.manager.ui.compose.LSPosedManagerApp
import org.lsposed.manager.util.ShortcutUtil

class MainActivity : BaseActivity() {
    private var restarting = false

    private var pendingNavigation: ExternalNavigation? by mutableStateOf(null)

    companion object {
        private const val KEY_PREFIX = "${MainActivity::class.java.name}."
        private const val EXTRA_SAVED_INSTANCE_STATE = KEY_PREFIX + "SAVED_INSTANCE_STATE"

        @JvmStatic
        fun newIntent(context: Context): Intent = Intent(context, MainActivity::class.java)

        private fun newIntent(savedInstanceState: Bundle, context: Context): Intent {
            return newIntent(context).putExtra(EXTRA_SAVED_INSTANCE_STATE, savedInstanceState)
        }
    }

    sealed class ExternalNavigation {
        data object Modules : ExternalNavigation()
        data object Logs : ExternalNavigation()
        data object Repo : ExternalNavigation()
        data object Settings : ExternalNavigation()
        data class ModuleScope(val modulePackageName: String, val moduleUserId: Int) : ExternalNavigation()
        data class RepoItem(val modulePackageName: String) : ExternalNavigation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        var restoredState = savedInstanceState
        if (restoredState == null) {
            restoredState = intent?.getBundleExtra(EXTRA_SAVED_INSTANCE_STATE)
        }

        super.onCreate(restoredState)
        handleIntent(intent)

        setContent {
            LSPosedManagerApp(
                activity = this,
                pendingNavigation = pendingNavigation,
                onNavigationConsumed = { pendingNavigation = null },
            )
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return

        if (intent.action == "android.intent.action.APPLICATION_PREFERENCES") {
            pendingNavigation = ExternalNavigation.Settings
            return
        }

        if (!ConfigManager.isBinderAlive()) return

        val dataString = intent.dataString
        if (!dataString.isNullOrEmpty()) {
            when (dataString) {
                "modules" -> {
                    pendingNavigation = ExternalNavigation.Modules
                    return
                }

                "logs" -> {
                    pendingNavigation = ExternalNavigation.Logs
                    return
                }

                "repo" -> {
                    if (ConfigManager.isMagiskInstalled()) {
                        pendingNavigation = ExternalNavigation.Repo
                    }
                    return
                }

                "settings" -> {
                    pendingNavigation = ExternalNavigation.Settings
                    return
                }
            }
        }

        val data = intent.data ?: return
        if (data.scheme == "module") {
            pendingNavigation = ExternalNavigation.ModuleScope(
                modulePackageName = data.host.orEmpty(),
                moduleUserId = data.port,
            )
        } else if (data.scheme == "repo") {
            val pkg = data.getQueryParameter("modulePackageName") ?: data.host
            if (!pkg.isNullOrBlank()) {
                pendingNavigation = ExternalNavigation.RepoItem(pkg)
            }
        } else if (data.scheme == "lsposed") {
            handleLsposedUri(data)
        }
    }

    private fun handleLsposedUri(uri: Uri) {
        when (uri.authority) {
            "module" -> {
                val packageName = uri.getQueryParameter("modulePackageName") ?: return
                val userId = uri.getQueryParameter("moduleUserId")?.toIntOrNull() ?: 0
                pendingNavigation = ExternalNavigation.ModuleScope(packageName, userId)
            }

            "repo" -> {
                val packageName = uri.getQueryParameter("modulePackageName") ?: return
                pendingNavigation = ExternalNavigation.RepoItem(packageName)
            }
        }
    }

    fun restart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S || App.isParasitic) {
            recreate()
            return
        }

        try {
            val state = Bundle()
            onSaveInstanceState(state)
            finish()
            startActivity(newIntent(state, this))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            restarting = true
        } catch (_: Throwable) {
            recreate()
        }
    }

    override fun onResume() {
        super.onResume()
        if (App.isParasitic) {
            val updateShortcut = ShortcutUtil.updateShortcut()
            Log.d(App.TAG, "update shortcut success = $updateShortcut")
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return restarting || super.dispatchKeyEvent(event)
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyShortcutEvent(event: KeyEvent): Boolean {
        return restarting || super.dispatchKeyShortcutEvent(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return restarting || super.dispatchTouchEvent(event)
    }

    override fun dispatchTrackballEvent(event: MotionEvent): Boolean {
        return restarting || super.dispatchTrackballEvent(event)
    }

    override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        return restarting || super.dispatchGenericMotionEvent(event)
    }
}
