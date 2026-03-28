package org.lsposed.manager.ui.compose

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Html
import android.text.format.Formatter
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.lsposed.lspd.ILSPManagerService
import org.lsposed.lspd.models.UserInfo
import org.lsposed.manager.App
import org.lsposed.manager.BuildConfig
import org.lsposed.manager.ConfigManager
import org.lsposed.manager.R
import org.lsposed.manager.adapters.AppHelper
import org.lsposed.manager.adapters.ScopeAdapter
import org.lsposed.manager.receivers.LSPManagerServiceHolder
import org.lsposed.manager.repo.RepoLoader
import org.lsposed.manager.repo.model.OnlineModule
import org.lsposed.manager.repo.model.Release
import org.lsposed.manager.repo.model.ReleaseAsset
import org.lsposed.manager.ui.activity.MainActivity
import org.lsposed.manager.ui.fragment.CompileDialogFragment
import org.lsposed.manager.util.BackupUtils
import org.lsposed.manager.util.CloudflareDNS
import org.lsposed.manager.util.ModuleUtil
import org.lsposed.manager.util.NavUtil
import org.lsposed.manager.util.ShortcutUtil
import org.lsposed.manager.util.ThemeUtil
import rikka.material.app.LocaleDelegate
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Arrays
import java.util.Locale

private object Routes {
    const val HOME = "home"
    const val MODULES = "modules"
    const val REPO = "repo"
    const val LOGS = "logs"
    const val SETTINGS = "settings"

    const val APP_LIST_PATTERN = "app_list/{modulePackageName}/{moduleUserId}"
    const val REPO_ITEM_PATTERN = "repo_item/{modulePackageName}"

    fun appList(modulePackageName: String, moduleUserId: Int): String {
        return "app_list/${Uri.encode(modulePackageName)}/$moduleUserId"
    }

    fun repoItem(modulePackageName: String): String {
        return "repo_item/${Uri.encode(modulePackageName)}"
    }
}

private data class BottomBarDestination(
    val route: String,
    val enabled: Boolean,
    @StringRes val label: Int,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
)

@Composable
fun LSPosedManagerApp(
    activity: MainActivity,
    pendingNavigation: MainActivity.ExternalNavigation?,
    onNavigationConsumed: () -> Unit,
) {
    val prefs = App.getPreferences()
    var uiTick by remember { mutableIntStateOf(0) }
    var showBottomBar by remember { mutableStateOf(true) }

    val moduleUtil = remember { ModuleUtil.getInstance() }
    val repoLoader = remember { RepoLoader.getInstance() }

    var modulesBadge by remember { mutableIntStateOf(0) }
    var repoBadge by remember { mutableIntStateOf(0) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                uiTick += 1
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    DisposableEffect(Unit) {
        val moduleListener = object : ModuleUtil.ModuleListener {
            override fun onModulesReloaded() {
                App.getMainHandler().post {
                    modulesBadge = moduleUtil.enabledModulesCount.coerceAtLeast(0)
                    repoBadge = computeRepoUpgradableCount(moduleUtil, repoLoader)
                    uiTick += 1
                }
            }

            override fun onSingleModuleReloaded(module: ModuleUtil.InstalledModule?) {
                onModulesReloaded()
            }
        }

        val repoListener = object : RepoLoader.RepoListener {
            override fun onRepoLoaded() {
                App.getMainHandler().post {
                    repoBadge = computeRepoUpgradableCount(moduleUtil, repoLoader)
                    uiTick += 1
                }
            }

            override fun onThrowable(t: Throwable?) {
                App.getMainHandler().post { uiTick += 1 }
            }
        }

        moduleUtil.addListener(moduleListener)
        repoLoader.addListener(repoListener)

        modulesBadge = moduleUtil.enabledModulesCount.coerceAtLeast(0)
        repoBadge = computeRepoUpgradableCount(moduleUtil, repoLoader)

        onDispose {
            moduleUtil.removeListener(moduleListener)
            repoLoader.removeListener(repoListener)
        }
    }

    val darkTheme = rememberDarkThemeFromPreference(
        prefs.getString("dark_theme", ThemeUtil.MODE_NIGHT_FOLLOW_SYSTEM) ?: ThemeUtil.MODE_NIGHT_FOLLOW_SYSTEM,
    )

    LSPosedComposeTheme(
        darkTheme = darkTheme,
        followSystemAccent = prefs.getBoolean("follow_system_accent", true),
        blackDarkTheme = prefs.getBoolean("black_dark_theme", false),
        themeColor = prefs.getString("theme_color", "MATERIAL_BLUE") ?: "MATERIAL_BLUE",
    ) {
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        val scrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: androidx.compose.ui.geometry.Offset,
                    source: NestedScrollSource,
                ): androidx.compose.ui.geometry.Offset {
                    if (available.y > 6f) {
                        showBottomBar = true
                    } else if (available.y < -6f) {
                        showBottomBar = false
                    }
                    return androidx.compose.ui.geometry.Offset.Zero
                }
            }
        }

        val binderAlive = remember(uiTick) { ConfigManager.isBinderAlive() }
        val magiskInstalled = remember(uiTick) { ConfigManager.isMagiskInstalled() }
        val repoLoaded = remember(uiTick) { repoLoader.isRepoLoaded }

        val bottomDestinations = remember(binderAlive, magiskInstalled, modulesBadge, repoBadge) {
            listOf(
                BottomBarDestination(
                    route = Routes.HOME,
                    enabled = true,
                    label = R.string.overview,
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                ),
                BottomBarDestination(
                    route = Routes.MODULES,
                    enabled = true,
                    label = R.string.Modules,
                    selectedIcon = Icons.Filled.Extension,
                    unselectedIcon = Icons.Outlined.Extension,
                ),
                BottomBarDestination(
                    route = Routes.REPO,
                    enabled = true,
                    label = R.string.module_repo,
                    selectedIcon = Icons.Filled.CloudDownload,
                    unselectedIcon = Icons.Filled.CloudDownload,
                ),
                BottomBarDestination(
                    route = Routes.LOGS,
                    enabled = binderAlive,
                    label = R.string.Logs,
                    selectedIcon = Icons.Filled.Article,
                    unselectedIcon = Icons.Filled.Article,
                ),
                BottomBarDestination(
                    route = Routes.SETTINGS,
                    enabled = true,
                    label = R.string.Settings,
                    selectedIcon = Icons.Filled.Settings,
                    unselectedIcon = Icons.Outlined.Settings,
                ),
            )
        }

        val bottomRoutes = remember(bottomDestinations) { bottomDestinations.map { it.route }.toSet() }
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        val isBottomRoute = currentRoute in bottomRoutes

        val navigateTopLevel: (String) -> Unit = remember(navController) {
            { route ->
                navController.navigate(route) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                }
            }
        }

        BackHandler(enabled = !isBottomRoute) {
            navController.popBackStack()
        }

        LaunchedEffect(pendingNavigation, binderAlive, magiskInstalled) {
            val target = pendingNavigation ?: return@LaunchedEffect
            val route = when (target) {
                MainActivity.ExternalNavigation.Modules -> Routes.MODULES
                MainActivity.ExternalNavigation.Logs -> Routes.LOGS
                MainActivity.ExternalNavigation.Repo -> Routes.REPO
                MainActivity.ExternalNavigation.Settings -> Routes.SETTINGS
                is MainActivity.ExternalNavigation.ModuleScope -> {
                    Routes.appList(target.modulePackageName, target.moduleUserId)
                }

                is MainActivity.ExternalNavigation.RepoItem -> {
                    Routes.repoItem(target.modulePackageName)
                }
            }

            if (route == Routes.LOGS && !binderAlive) {
                onNavigationConsumed()
                return@LaunchedEffect
            }

            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
            }
            onNavigationConsumed()
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Routes.HOME,
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollConnection),
                ) {
                    composable(Routes.HOME) {
                        AnimatedPage {
                            HomeScreen(
                                binderAlive = binderAlive,
                                magiskInstalled = magiskInstalled,
                                repoLoaded = repoLoaded,
                                onNavigateToSettings = { navigateTopLevel(Routes.SETTINGS) },
                                onNavigateToModules = { navigateTopLevel(Routes.MODULES) },
                                onNavigateToRepo = { navigateTopLevel(Routes.REPO) },
                                modulesEnabledCount = modulesBadge,
                            )
                        }
                    }

                    composable(Routes.MODULES) {
                        AnimatedPage {
                            if (!binderAlive) {
                                DisabledScreen(R.string.not_install_summary)
                            } else {
                                ModulesScreen(
                                    activity = activity,
                                    onOpenScope = { pkg, userId ->
                                        navController.navigate(Routes.appList(pkg, userId))
                                    },
                                    onOpenRepo = { navigateTopLevel(Routes.REPO) },
                                    snackbarHostState = snackbarHostState,
                                )
                            }
                        }
                    }

                    composable(Routes.REPO) {
                        AnimatedPage {
                            RepoScreen(
                                activity = activity,
                                onOpenRepoItem = { pkg -> navController.navigate(Routes.repoItem(pkg)) },
                                snackbarHostState = snackbarHostState,
                            )
                        }
                    }

                    composable(Routes.LOGS) {
                        AnimatedPage {
                            if (!binderAlive) {
                                DisabledScreen(R.string.not_install_summary)
                            } else {
                                LogsScreen(snackbarHostState = snackbarHostState)
                            }
                        }
                    }

                    composable(Routes.SETTINGS) {
                        AnimatedPage {
                            SettingsScreen(
                                activity = activity,
                                snackbarHostState = snackbarHostState,
                            )
                        }
                    }

                    composable(
                        route = Routes.APP_LIST_PATTERN,
                        arguments = listOf(
                            navArgument("modulePackageName") { type = NavType.StringType },
                            navArgument("moduleUserId") { type = NavType.IntType },
                        ),
                    ) { entry ->
                        val pkg = Uri.decode(entry.arguments?.getString("modulePackageName").orEmpty())
                        val userId = entry.arguments?.getInt("moduleUserId") ?: 0
                        AppListScreen(
                            activity = activity,
                            modulePackageName = pkg,
                            moduleUserId = userId,
                            onBack = { navController.popBackStack() },
                            snackbarHostState = snackbarHostState,
                        )
                    }

                    composable(
                        route = Routes.REPO_ITEM_PATTERN,
                        arguments = listOf(navArgument("modulePackageName") { type = NavType.StringType }),
                    ) { entry ->
                        val pkg = Uri.decode(entry.arguments?.getString("modulePackageName").orEmpty())
                        RepoItemScreen(
                            activity = activity,
                            modulePackageName = pkg,
                            onBack = { navController.popBackStack() },
                            snackbarHostState = snackbarHostState,
                        )
                    }
                }

                AnimatedVisibility(
                    visible = showBottomBar && isBottomRoute,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    enter = slideInVertically(
                        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
                        initialOffsetY = { it / 2 },
                    ) + fadeIn(animationSpec = tween(220)),
                    exit = slideOutVertically(
                        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                        targetOffsetY = { it / 2 },
                    ) + fadeOut(animationSpec = tween(180)),
                ) {
                    FloatingBottomBar(
                        navController = navController,
                        destinations = bottomDestinations,
                        modulesBadge = modulesBadge,
                        repoBadge = repoBadge,
                        onNavigateTopLevel = navigateTopLevel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding(),
                    )
                }
            }
        }

        LaunchedEffect(Unit) {
            scope.launch(Dispatchers.IO) {
                repoLoader.loadLocalData(true)
                if (!moduleUtil.isModulesLoaded) {
                    moduleUtil.reloadInstalledModules()
                }
            }
        }
    }
}

@Composable
private fun DisabledScreen(@StringRes text: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResourceCompat(text),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AnimatedPage(content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(animationSpec = tween(durationMillis = 220)) +
                slideInVertically(
                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                    initialOffsetY = { it / 18 },
                ),
            exit = fadeOut(animationSpec = tween(durationMillis = 140)),
        ) {
            content()
        }
    }
}

private sealed class ScreenLoadState<out T> {
    object Loading : ScreenLoadState<Nothing>()
    data class Ready<T>(val value: T) : ScreenLoadState<T>()
    data class Error(val message: String) : ScreenLoadState<Nothing>()
}

private data class ModuleRowModel(
    val module: ModuleUtil.InstalledModule,
    val appName: String,
    val description: String?,
    val upgradable: Boolean,
    val enabled: Boolean,
)

private data class ModulesUiData(
    val users: List<UserInfo>,
    val rows: List<ModuleRowModel>,
)

private data class RepoRowModel(
    val module: OnlineModule,
    val packageName: String,
    val title: String,
    val summary: String?,
    val updatedText: String?,
    val upgradable: Boolean,
)

private data class RepoUiData(
    val rows: List<RepoRowModel>,
    val upgradableCount: Int,
)

@Composable
private fun LoadingStatePanel(@StringRes textRes: Int = R.string.loading) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CircularProgressIndicator()
            Text(stringResourceCompat(textRes))
        }
    }
}

@Composable
private fun ErrorStatePanel(
    title: String,
    message: String,
    onRetry: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(onClick = onRetry) {
                    Text(stringResourceCompat(android.R.string.ok))
                }
            }
        }
    }
}

@Composable
private fun FloatingBottomBar(
    navController: NavController,
    destinations: List<BottomBarDestination>,
    modulesBadge: Int,
    repoBadge: Int,
    onNavigateTopLevel: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val selectedIndex = destinations.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)
    val animatedSelected by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "nav-selection",
    )

    BoxWithConstraints(modifier = modifier) {
        val itemSize = 56.dp
        val itemSpacing = 4.dp
        val containerPadding = 8.dp
        val navWidth = itemSize * destinations.size + itemSpacing * (destinations.size - 1) + containerPadding * 2

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            val barShape = RoundedCornerShape(28.dp)
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 8.dp,
                shape = barShape,
            ) {
                Box(
                    modifier = Modifier
                        .width(navWidth)
                        .height(72.dp),
                ) {
                    val density = androidx.compose.ui.platform.LocalDensity.current
                    val indicatorOffset = with(density) {
                        ((itemSize + itemSpacing).toPx() * animatedSelected).toInt()
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = containerPadding, vertical = 8.dp)
                            .offset { IntOffset(indicatorOffset, 0) }
                            .width(itemSize)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.large,
                            ),
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = containerPadding),
                        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        destinations.forEachIndexed { index, destination ->
                            val selected = index == selectedIndex
                            val enabled = destination.enabled
                            val badgeCount = when (destination.route) {
                                Routes.MODULES -> modulesBadge
                                Routes.REPO -> repoBadge
                                else -> 0
                            }

                            Box(
                                modifier = Modifier
                                    .size(itemSize)
                                    .clip(MaterialTheme.shapes.large)
                                    .clickable {
                                        if (enabled && destination.route != currentRoute) {
                                            onNavigateTopLevel(destination.route)
                                        }
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (badgeCount > 0) {
                                            Badge { Text(badgeCount.toString()) }
                                        }
                                    },
                                ) {
                                    Icon(
                                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                        contentDescription = stringResourceCompat(destination.label),
                                        tint = when {
                                            !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                                            selected -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    binderAlive: Boolean,
    magiskInstalled: Boolean,
    repoLoaded: Boolean,
    onNavigateToSettings: () -> Unit,
    onNavigateToModules: () -> Unit,
    onNavigateToRepo: () -> Unit,
    modulesEnabledCount: Int,
) {
    val context = LocalContext.current

    var showAbout by remember { mutableStateOf(false) }

    val statusTitle: String
    val statusSummary: String
    val statusColor: Color

    val warnings = remember {
        mutableStateListOf<String>()
    }
    warnings.clear()

    if (binderAlive) {
        val dex2oatAbnormal = ConfigManager.getDex2OatWrapperCompatibility() != ILSPManagerService.DEX2OAT_OK && !ConfigManager.dex2oatFlagsLoaded()
        val sepolicyAbnormal = !ConfigManager.isSepolicyLoaded()
        val systemServerAbnormal = !ConfigManager.systemServerRequested()

        if (sepolicyAbnormal) warnings += stringResourceCompat(R.string.selinux_policy_not_loaded_summary)
        if (systemServerAbnormal) warnings += stringResourceCompat(R.string.system_inject_fail_summary)
        if (dex2oatAbnormal) warnings += stringResourceCompat(R.string.system_prop_incorrect_summary)

        if (warnings.isNotEmpty()) {
            statusTitle = stringResourceCompat(R.string.partial_activated)
            statusColor = MaterialTheme.colorScheme.tertiary
        } else {
            statusTitle = stringResourceCompat(R.string.activated)
            statusColor = Color(0xFF4CAF50)
        }

        statusSummary = String.format(
            LocaleDelegate.defaultLocale,
            "%s (%d) - %s",
            ConfigManager.getXposedVersionName(),
            ConfigManager.getXposedVersionCode(),
            ConfigManager.getApi(),
        )
    } else {
        statusTitle = if (magiskInstalled) stringResourceCompat(R.string.not_installed) else stringResourceCompat(R.string.not_installed)
        statusSummary = stringResourceCompat(R.string.not_install_summary)
        statusColor = MaterialTheme.colorScheme.error
    }

    val scroll = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResourceCompat(R.string.app_name),
                        fontWeight = FontWeight.Black,
                    )
                },
                actions = {
                    IconButton(onClick = { showAbout = true }) {
                        Icon(Icons.Filled.Info, contentDescription = null)
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = null)
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scroll)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = statusTitle, style = MaterialTheme.typography.titleLarge, color = statusColor)
                    Text(text = statusSummary, style = MaterialTheme.typography.bodyMedium)

                    if (!binderAlive && magiskInstalled) {
                        Button(onClick = { NavUtil.startURL(context as MainActivity, stringResourceCompat(R.string.install_url)) }) {
                            Text(stringResourceCompat(R.string.install))
                        }
                    }
                }
            }

            if (warnings.isNotEmpty()) {
                ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResourceCompat(R.string.warning),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                        warnings.forEach {
                            Text(text = "• $it", color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 92.dp)
                        .clickable(onClick = onNavigateToModules),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = stringResourceCompat(R.string.Modules), style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = context.resources.getQuantityString(
                                R.plurals.modules_enabled_count,
                                modulesEnabledCount,
                                modulesEnabledCount,
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                ElevatedCard(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 92.dp)
                        .clickable(onClick = onNavigateToRepo),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = stringResourceCompat(R.string.module_repo), style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = if (repoLoaded) stringResourceCompat(R.string.available) else stringResourceCompat(R.string.loading),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            DeviceInfoCard()
        }
    }

    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            title = { Text(stringResourceCompat(R.string.app_name)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                    Text(text = stringResourceCompat(R.string.about_view_source_code, "GitHub", "Telegram"))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showAbout = false
                    NavUtil.startURL(context as MainActivity, "https://github.com/JingMatrix/LSPosed")
                }) {
                    Text("GitHub")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAbout = false
                    NavUtil.startURL(context as MainActivity, "https://t.me/LSPosed")
                }) {
                    Text("Telegram")
                }
            },
        )
    }
}

@Composable
private fun DeviceInfoCard() {
    val context = LocalContext.current

    val systemVersion = if (Build.VERSION.PREVIEW_SDK_INT != 0) {
        "${Build.VERSION.CODENAME} Preview (API ${Build.VERSION.SDK_INT})"
    } else {
        "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    }

    val manufacturer = buildString {
        append(Build.MANUFACTURER.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() })
        if (Build.BRAND != Build.MANUFACTURER) {
            append(' ')
            append(Build.BRAND.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() })
        }
        append(' ')
        append(Build.MODEL)
    }

    val infoText = buildString {
        appendLine(context.getString(R.string.info_api_version))
        appendLine(ConfigManager.getXposedApiVersion().toString())
        appendLine()
        appendLine(context.getString(R.string.info_framework_version))
        appendLine("${ConfigManager.getXposedVersionName()} (${ConfigManager.getXposedVersionCode()})")
        appendLine()
        appendLine(context.getString(R.string.info_manager_package_name))
        appendLine(context.packageName)
        appendLine()
        appendLine(context.getString(R.string.info_system_version))
        appendLine(systemVersion)
        appendLine()
        appendLine(context.getString(R.string.info_device))
        appendLine(manufacturer)
        appendLine()
        appendLine(context.getString(R.string.info_system_abi))
        appendLine(Arrays.toString(Build.SUPPORTED_ABIS))
    }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = stringResourceCompat(R.string.overview), style = MaterialTheme.typography.titleMedium)
            InfoRow(stringResourceCompat(R.string.info_api_version), ConfigManager.getXposedApiVersion().toString())
            InfoRow(
                stringResourceCompat(R.string.info_framework_version),
                if (ConfigManager.isBinderAlive()) {
                    "${ConfigManager.getXposedVersionName()} (${ConfigManager.getXposedVersionCode()})"
                } else {
                    stringResourceCompat(R.string.not_installed)
                },
            )
            InfoRow(stringResourceCompat(R.string.info_manager_package_name), context.packageName)
            InfoRow(stringResourceCompat(R.string.info_system_version), systemVersion)
            InfoRow(stringResourceCompat(R.string.info_device), manufacturer)
            InfoRow(stringResourceCompat(R.string.info_system_abi), Build.SUPPORTED_ABIS.firstOrNull().orEmpty())

            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            TextButton(onClick = {
                clipboard.setPrimaryClip(ClipData.newPlainText("lsposed_info", infoText))
            }) {
                Icon(Icons.Filled.ContentCopy, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResourceCompat(R.string.info_copy))
            }
        }
    }
}

@Composable
private fun InfoRow(title: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModulesScreen(
    activity: MainActivity,
    onOpenScope: (String, Int) -> Unit,
    onOpenRepo: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val moduleUtil = remember { ModuleUtil.getInstance() }
    val repoLoader = remember { RepoLoader.getInstance() }
    val scope = rememberCoroutineScope()

    var refreshToken by remember { mutableIntStateOf(0) }
    var search by rememberSaveable { mutableStateOf("") }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var showInstallDialog by remember { mutableStateOf(false) }
    var reloadNonce by remember { mutableIntStateOf(0) }
    var uiState by remember { mutableStateOf<ScreenLoadState<ModulesUiData>>(ScreenLoadState.Loading) }

    DisposableEffect(Unit) {
        val moduleListener = object : ModuleUtil.ModuleListener {
            override fun onModulesReloaded() {
                App.getMainHandler().post { refreshToken += 1 }
            }

            override fun onSingleModuleReloaded(module: ModuleUtil.InstalledModule?) {
                onModulesReloaded()
            }
        }
        val repoListener = object : RepoLoader.RepoListener {
            override fun onRepoLoaded() {
                App.getMainHandler().post { refreshToken += 1 }
            }
        }
        moduleUtil.addListener(moduleListener)
        repoLoader.addListener(repoListener)
        onDispose {
            moduleUtil.removeListener(moduleListener)
            repoLoader.removeListener(repoListener)
        }
    }

    fun triggerReload() {
        reloadNonce += 1
        scope.launch(Dispatchers.IO) {
            moduleUtil.reloadInstalledModules()
            repoLoader.loadLocalData(true)
        }
    }

    LaunchedEffect(Unit) {
        if (!moduleUtil.isModulesLoaded) {
            scope.launch(Dispatchers.IO) {
                moduleUtil.reloadInstalledModules()
            }
        }
    }

    LaunchedEffect(refreshToken, selectedTabIndex, search, reloadNonce) {
        if (!moduleUtil.isModulesLoaded) {
            uiState = ScreenLoadState.Loading
            return@LaunchedEffect
        }
        uiState = ScreenLoadState.Loading
        uiState = runCatching {
            withContext(Dispatchers.Default) {
                val users = moduleUtil.users ?: emptyList()
                val normalizedIndex = if (users.isEmpty()) 0 else selectedTabIndex.coerceIn(0, users.lastIndex)
                val targetUserId = users.getOrNull(normalizedIndex)?.id

                val rows = moduleUtil.modules?.values?.asSequence().orEmpty()
                    .filter { targetUserId == null || it.userId == targetUserId }
                    .map { module ->
                        val appName = runCatching { module.getAppName() }.getOrElse { module.packageName }
                        val description = runCatching { module.getDescription() }.getOrNull()?.takeIf { it.isNotBlank() }
                        ModuleRowModel(
                            module = module,
                            appName = appName,
                            description = description,
                            upgradable = repoLoader.getModuleLatestVersion(module.packageName)?.upgradable(module.versionCode, module.versionName) == true,
                            enabled = moduleUtil.isModuleEnabled(module.packageName),
                        )
                    }
                    .filter { row ->
                        if (search.isBlank()) true
                        else row.appName.contains(search, ignoreCase = true) || row.module.packageName.contains(search, ignoreCase = true)
                    }
                    .sortedBy { it.appName.lowercase(Locale.ROOT) }
                    .toList()

                ModulesUiData(users = users, rows = rows)
            }
        }.fold(
            onSuccess = { ScreenLoadState.Ready(it) },
            onFailure = { ScreenLoadState.Error(it.message ?: "Failed to load modules") },
        )
    }

    val users = (uiState as? ScreenLoadState.Ready)?.value?.users.orEmpty()
    val user = users.getOrNull(selectedTabIndex)
    val moduleRows = (uiState as? ScreenLoadState.Ready)?.value?.rows.orEmpty()

    LaunchedEffect(users) {
        if (users.isNotEmpty() && selectedTabIndex > users.lastIndex) {
            selectedTabIndex = 0
        }
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                title = stringResourceCompat(R.string.Modules),
                query = search,
                onQueryChange = { search = it },
                onRefresh = { triggerReload() },
                extraActions = {
                    IconButton(onClick = onOpenRepo) {
                        Icon(Icons.Filled.CloudDownload, contentDescription = null)
                    }
                },
            )
        },
        floatingActionButton = {
            if (users.size > 1 && users.getOrNull(selectedTabIndex) != null) {
                FloatingActionButton(onClick = { showInstallDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (users.isNotEmpty()) {
                ScrollableTabRow(selectedTabIndex = selectedTabIndex) {
                    users.forEachIndexed { index, info ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(info.name) },
                        )
                    }
                }
            }

            when (val state = uiState) {
                ScreenLoadState.Loading -> {
                    LoadingStatePanel()
                }
                is ScreenLoadState.Error -> {
                    ErrorStatePanel(
                        title = stringResourceCompat(R.string.Modules),
                        message = state.message,
                        onRetry = { triggerReload() },
                    )
                }
                is ScreenLoadState.Ready -> {
                    if (moduleRows.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(stringResourceCompat(R.string.list_empty))
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(moduleRows, key = { "${it.module.packageName}:${it.module.userId}" }) { row ->
                                ModuleCard(
                                    module = row.module,
                                    appName = row.appName,
                                    description = row.description,
                                    upgradable = row.upgradable,
                                    enabled = row.enabled,
                                    onEnabledChange = { enabled ->
                                        scope.launch(Dispatchers.IO) {
                                            val result = moduleUtil.setModuleEnabled(row.module.packageName, enabled)
                                            withContext(Dispatchers.Main) {
                                                if (!result) {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(stringResourceCompat(R.string.failed_to_save_scope_list))
                                                    }
                                                } else {
                                                    refreshToken += 1
                                                }
                                            }
                                        }
                                    },
                                    onScopeClick = { onOpenScope(row.module.packageName, row.module.userId) },
                                    onLaunch = {
                                        val intent = AppHelper.getSettingsIntent(row.module.packageName, row.module.userId)
                                        if (intent != null) {
                                            ConfigManager.startActivityAsUserWithFeature(intent, row.module.userId)
                                        }
                                    },
                                    onAppInfo = {
                                        val intent = Intent(Intent.ACTION_SHOW_APP_INFO).apply {
                                            putExtra(Intent.EXTRA_PACKAGE_NAME, row.module.packageName)
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        ConfigManager.startActivityAsUserWithFeature(intent, row.module.userId)
                                    },
                                    onForceStop = {
                                        ConfigManager.forceStopPackage(row.module.packageName, row.module.userId)
                                    },
                                    onCompileSpeed = {
                                        CompileDialogFragment.speed(activity.supportFragmentManager, row.module.app)
                                    },
                                    onUninstall = {
                                        scope.launch(Dispatchers.IO) {
                                            ConfigManager.uninstallPackage(row.module.packageName, row.module.userId)
                                            moduleUtil.reloadInstalledModules()
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showInstallDialog && user != null) {
        val installCandidates = remember(refreshToken, user.id) {
            runCatching {
                val all = moduleUtil.modules?.values?.toList().orEmpty()
                val installedPackages = all.filter { it.userId == user.id }.map { it.packageName }.toSet()
                all.filter { it.userId != user.id && !installedPackages.contains(it.packageName) }
                    .distinctBy { it.packageName }
                    .map { module ->
                        ModuleRowModel(
                            module = module,
                            appName = runCatching { module.getAppName() }.getOrElse { module.packageName },
                            description = null,
                            upgradable = false,
                            enabled = false,
                        )
                    }
                    .sortedBy { it.appName.lowercase(Locale.ROOT) }
            }.getOrElse { emptyList() }
        }

        AlertDialog(
            onDismissRequest = { showInstallDialog = false },
            title = { Text(stringResourceCompat(R.string.install_to_user, user.name)) },
            text = {
                Column(modifier = Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState())) {
                    if (installCandidates.isEmpty()) {
                        Text(stringResourceCompat(R.string.list_empty))
                    } else {
                        installCandidates.forEach { row ->
                            TextButton(
                                onClick = {
                                    showInstallDialog = false
                                    scope.launch(Dispatchers.IO) {
                                        val ok = ConfigManager.installExistingPackageAsUser(row.module.packageName, user.id)
                                        if (ok) {
                                            moduleUtil.reloadSingleModule(row.module.packageName, user.id)
                                        }
                                        withContext(Dispatchers.Main) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    if (ok) {
                                                        stringResourceCompat(R.string.module_installed, row.appName, user.name)
                                                    } else {
                                                        stringResourceCompat(R.string.module_install_failed)
                                                    },
                                                )
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(row.appName, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(
                                        row.module.packageName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showInstallDialog = false }) {
                    Text(stringResourceCompat(android.R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun ModuleCard(
    module: ModuleUtil.InstalledModule,
    appName: String,
    description: String?,
    upgradable: Boolean,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    onScopeClick: () -> Unit,
    onLaunch: () -> Unit,
    onAppInfo: () -> Unit,
    onForceStop: () -> Unit,
    onCompileSpeed: () -> Unit,
    onUninstall: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AppIconView(module.pkg)
                Column(modifier = Modifier.weight(1f).clickable(onClick = onScopeClick), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(appName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(module.packageName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${module.versionName} (${module.versionCode})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (!description.isNullOrBlank()) {
                        Text(
                            description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Switch(checked = enabled, onCheckedChange = onEnabledChange)

                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text(stringResourceCompat(R.string.menu_launch)) }, onClick = {
                            expanded = false
                            onLaunch()
                        })
                        DropdownMenuItem(text = { Text(stringResourceCompat(R.string.menu_app_info)) }, onClick = {
                            expanded = false
                            onAppInfo()
                        })
                        DropdownMenuItem(text = { Text(stringResourceCompat(R.string.menu_force_stop)) }, onClick = {
                            expanded = false
                            onForceStop()
                        })
                        DropdownMenuItem(text = { Text(stringResourceCompat(R.string.menu_compile_speed)) }, onClick = {
                            expanded = false
                            onCompileSpeed()
                        })
                        DropdownMenuItem(text = { Text(stringResourceCompat(R.string.menu_uninstall)) }, onClick = {
                            expanded = false
                            onUninstall()
                        })
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (upgradable) {
                    AssistChip(onClick = {}, label = { Text(stringResourceCompat(R.string.update_available)) })
                }
                if (module.legacy) {
                    AssistChip(onClick = {}, label = { Text("Legacy") })
                }
            }

            TextButton(onClick = onScopeClick) {
                Icon(Icons.Filled.Security, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResourceCompat(R.string.scope))
            }
        }
    }
}

@Composable
private fun AppIconView(packageInfo: PackageInfo) {
    val context = LocalContext.current
    val uid = packageInfo.applicationInfo?.uid ?: -1
    val icon = remember(packageInfo.packageName, uid) {
        runCatching {
            packageInfo.applicationInfo?.loadIcon(context.packageManager)
        }.getOrNull()
    }
    AndroidView(
        factory = { android.widget.ImageView(it) },
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        update = {
            it.setImageDrawable(icon ?: context.packageManager.defaultActivityIcon)
            it.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
        },
    )
}

private data class AppScopeItem(
    val info: PackageInfo,
    val packageName: String,
    val appName: String,
    val userId: Int,
    val isSystem: Boolean,
    val isDenyListed: Boolean,
    val isRecommended: Boolean,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppListScreen(
    activity: MainActivity,
    modulePackageName: String,
    moduleUserId: Int,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val moduleUtil = remember { ModuleUtil.getInstance() }
    val scope = rememberCoroutineScope()
    val module = remember(modulePackageName, moduleUserId) { moduleUtil.getModule(modulePackageName, moduleUserId) }

    var search by rememberSaveable { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var items by remember { mutableStateOf<List<AppScopeItem>>(emptyList()) }
    var checkedScopes by remember { mutableStateOf(setOf<ScopeAdapter.ApplicationWithEquals>()) }
    var moduleEnabled by remember { mutableStateOf(module?.let { moduleUtil.isModuleEnabled(it.packageName) } ?: false) }

    val backupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/gzip"),
    ) { uri ->
        if (uri == null || module == null) return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            runCatching {
                BackupUtils.backup(uri, module.packageName)
            }.onFailure {
                scope.launch {
                    snackbarHostState.showSnackbar(stringResourceCompat(R.string.settings_backup_failed2, it.message ?: ""))
                }
            }
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null || module == null) return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            runCatching {
                BackupUtils.restore(uri, module.packageName)
                checkedScopes = ConfigManager.getModuleScope(module.packageName).toSet()
            }.onFailure {
                scope.launch {
                    snackbarHostState.showSnackbar(stringResourceCompat(R.string.settings_restore_failed2, it.message ?: ""))
                }
            }
        }
    }

    fun reload(force: Boolean = false) {
        if (module == null) return
        loading = true
        scope.launch(Dispatchers.IO) {
            val appList = AppHelper.getAppList(force)
            val denyList = AppHelper.getDenyList(force).toSet()
            val selected = ConfigManager.getModuleScope(module.packageName).toSet()
            val recommended = module.getScopeList()?.toSet().orEmpty()

            val filterSystemApps = App.getPreferences().getBoolean("filter_system_apps", true)
            val filterGames = App.getPreferences().getBoolean("filter_games", true)
            val filterModules = App.getPreferences().getBoolean("filter_modules", true)
            val filterDeny = App.getPreferences().getBoolean("filter_denylist", false)

            val built = appList.mapNotNull { info ->
                val appInfo = info.applicationInfo ?: return@mapNotNull null
                val packageName = info.packageName
                val userId = appInfo.uid / App.PER_USER_RANGE
                if ((packageName == "system" && userId != 0) || packageName == module.packageName || packageName == BuildConfig.APPLICATION_ID) {
                    return@mapNotNull null
                }
                if (userId != module.userId) {
                    return@mapNotNull null
                }

                val appRef = ScopeAdapter.ApplicationWithEquals(packageName, userId)
                val checked = selected.contains(appRef)
                val recommendedHit = recommended.contains(packageName)

                val hiddenByFilters = !checked && !recommendedHit && (
                    (filterDeny && denyList.contains(packageName)) ||
                        (filterModules && ModuleUtil.getInstance().getModule(packageName, userId) != null) ||
                        (filterGames && (appInfo.category == ApplicationInfo.CATEGORY_GAME || (appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0)) ||
                        (filterSystemApps && (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0)
                    )
                if (hiddenByFilters) return@mapNotNull null

                AppScopeItem(
                    info = info,
                    packageName = packageName,
                    appName = AppHelper.getAppLabel(info, activity.packageManager).toString(),
                    userId = userId,
                    isSystem = packageName == "system",
                    isDenyListed = denyList.contains(packageName),
                    isRecommended = recommendedHit,
                )
            }.sortedWith(compareByDescending<AppScopeItem> { selected.contains(ScopeAdapter.ApplicationWithEquals(it.packageName, it.userId)) }
                .thenByDescending { it.isRecommended }
                .thenBy { it.appName.lowercase(Locale.ROOT) })

            withContext(Dispatchers.Main) {
                checkedScopes = selected
                items = built
                moduleEnabled = moduleUtil.isModuleEnabled(module.packageName)
                loading = false
            }
        }
    }

    LaunchedEffect(modulePackageName, moduleUserId) {
        reload(force = false)
    }

    if (module == null) {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text(modulePackageName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        }) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(stringResourceCompat(R.string.list_empty))
            }
        }
        return
    }

    fun commitScope(set: Set<ScopeAdapter.ApplicationWithEquals>) {
        scope.launch(Dispatchers.IO) {
            val success = ConfigManager.setModuleScope(module.packageName, module.legacy, set)
            withContext(Dispatchers.Main) {
                if (success) {
                    checkedScopes = set
                } else {
                    scope.launch { snackbarHostState.showSnackbar(stringResourceCompat(R.string.failed_to_save_scope_list)) }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                title = module.getAppName(),
                subtitle = module.packageName,
                query = search,
                onQueryChange = { search = it },
                onBack = onBack,
                onRefresh = { reload(force = true) },
                extraActions = {
                    IconButton(onClick = {
                        val now = LocalDateTime.now()
                        backupLauncher.launch("${module.getAppName()}_${now}.lsp")
                    }) {
                        Icon(Icons.Filled.Backup, contentDescription = null)
                    }
                    IconButton(onClick = {
                        restoreLauncher.launch(arrayOf("*/*"))
                    }) {
                        Icon(Icons.Filled.Restore, contentDescription = null)
                    }

                    var menuExpanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(text = { Text(stringResourceCompat(R.string.select_all)) }, onClick = {
                            menuExpanded = false
                            val newSet = checkedScopes.toMutableSet()
                            items.forEach { newSet.add(ScopeAdapter.ApplicationWithEquals(it.packageName, it.userId)) }
                            commitScope(newSet)
                        })
                        DropdownMenuItem(text = { Text(stringResourceCompat(R.string.select_none)) }, onClick = {
                            menuExpanded = false
                            val removeSet = items.map { ScopeAdapter.ApplicationWithEquals(it.packageName, it.userId) }.toSet()
                            val newSet = checkedScopes.toMutableSet()
                            newSet.removeAll(removeSet)
                            commitScope(newSet)
                        })
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            ElevatedCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(stringResourceCompat(R.string.module_is_not_activated_yet), style = MaterialTheme.typography.titleMedium)
                        Text(
                            if (moduleEnabled) stringResourceCompat(R.string.activated) else stringResourceCompat(R.string.not_installed),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = moduleEnabled,
                        onCheckedChange = { checked ->
                            scope.launch(Dispatchers.IO) {
                                val success = moduleUtil.setModuleEnabled(module.packageName, checked)
                                withContext(Dispatchers.Main) {
                                    if (success) moduleEnabled = checked
                                }
                            }
                        },
                    )
                }
            }

            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val filtered = items.filter {
                    if (search.isBlank()) true
                    else it.appName.contains(search, true) || it.packageName.contains(search, true)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(filtered, key = { "${it.packageName}:${it.userId}" }) { item ->
                        val appRef = ScopeAdapter.ApplicationWithEquals(item.packageName, item.userId)
                        val checked = checkedScopes.contains(appRef)

                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth().clickable(enabled = moduleEnabled) {
                                val newSet = checkedScopes.toMutableSet()
                                if (checked) newSet.remove(appRef) else newSet.add(appRef)
                                commitScope(newSet)
                            },
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                AppIconView(item.info)
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(item.appName, fontWeight = FontWeight.SemiBold)
                                    Text(item.packageName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (item.isRecommended || item.isDenyListed) {
                                        val hint = buildString {
                                            if (item.isRecommended) append(stringResourceCompat(R.string.requested_by_module))
                                            if (item.isDenyListed) {
                                                if (isNotEmpty()) append(" • ")
                                                append(stringResourceCompat(R.string.deny_list_info))
                                            }
                                        }
                                        Text(hint, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Checkbox(checked = checked, onCheckedChange = {
                                    val newSet = checkedScopes.toMutableSet()
                                    if (it) newSet.add(appRef) else newSet.remove(appRef)
                                    commitScope(newSet)
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RepoScreen(
    activity: MainActivity,
    onOpenRepoItem: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val repoLoader = remember { RepoLoader.getInstance() }
    val moduleUtil = remember { ModuleUtil.getInstance() }
    val prefs = App.getPreferences()
    val scope = rememberCoroutineScope()

    var query by rememberSaveable { mutableStateOf("") }
    var refreshToken by remember { mutableIntStateOf(0) }
    var reloadNonce by remember { mutableIntStateOf(0) }
    var uiState by remember { mutableStateOf<ScreenLoadState<RepoUiData>>(ScreenLoadState.Loading) }

    DisposableEffect(Unit) {
        val repoListener = object : RepoLoader.RepoListener {
            override fun onRepoLoaded() {
                App.getMainHandler().post { refreshToken += 1 }
            }

            override fun onThrowable(t: Throwable?) {
                App.getMainHandler().post {
                    refreshToken += 1
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            stringResourceCompat(R.string.repo_load_failed, t?.localizedMessage ?: "unknown"),
                        )
                    }
                }
            }
        }
        val moduleListener = object : ModuleUtil.ModuleListener {
            override fun onModulesReloaded() {
                App.getMainHandler().post { refreshToken += 1 }
            }
        }
        repoLoader.addListener(repoListener)
        moduleUtil.addListener(moduleListener)
        onDispose {
            repoLoader.removeListener(repoListener)
            moduleUtil.removeListener(moduleListener)
        }
    }

    val sortMode = remember(refreshToken) { prefs.getInt("repo_sort", 0) }
    val upgradableFirst = remember(refreshToken) { prefs.getBoolean("upgradable_first", true) }
    val channel = remember(refreshToken) {
        prefs.getString("update_channel", activity.resources.getStringArray(R.array.update_channel_values).first())
            ?: activity.resources.getStringArray(R.array.update_channel_values).first()
    }

    fun triggerReload(forceRemote: Boolean = false) {
        reloadNonce += 1
        scope.launch(Dispatchers.IO) {
            if (forceRemote) {
                repoLoader.loadRemoteData()
            } else if (!repoLoader.isRepoLoaded) {
                repoLoader.loadLocalData(true)
            }
            if (!moduleUtil.isModulesLoaded) {
                moduleUtil.reloadInstalledModules()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!repoLoader.isRepoLoaded || !moduleUtil.isModulesLoaded) {
            triggerReload(forceRemote = false)
        }
    }

    LaunchedEffect(refreshToken, query, sortMode, upgradableFirst, channel, reloadNonce) {
        if (!repoLoader.isRepoLoaded) {
            uiState = ScreenLoadState.Loading
            return@LaunchedEffect
        }
        uiState = ScreenLoadState.Loading
        uiState = runCatching {
            withContext(Dispatchers.Default) {
                val installedByPackage = moduleUtil.modules
                    ?.values
                    ?.groupBy { it.packageName }
                    ?.mapValues { (_, versions) ->
                        versions.maxByOrNull { it.versionCode } ?: versions.first()
                    }
                    .orEmpty()

                val modules = repoLoader.onlineModules?.toList().orEmpty()
                    .filter { it.isHide != true && !it.name.isNullOrBlank() }
                    .filter { module ->
                        if (query.isBlank()) {
                            true
                        } else {
                            val title = module.description ?: ""
                            val pkg = module.name ?: ""
                            val summary = module.summary ?: ""
                            title.contains(query, true) || pkg.contains(query, true) || summary.contains(query, true)
                        }
                    }

                val upgradableByPackage = modules.associate { module ->
                    val pkg = module.name.orEmpty()
                    val installed = installedByPackage[pkg]
                    val latest = repoLoader.getModuleLatestVersion(pkg)
                    pkg to (installed != null && latest != null && latest.upgradable(installed.versionCode, installed.versionName))
                }

                val byName = compareBy<OnlineModule> { (it.description ?: it.name ?: "").lowercase(Locale.ROOT) }
                val byTime = compareByDescending<OnlineModule> {
                    parseInstantOrZero(repoLoader.getLatestReleaseTime(it.name, channel) ?: it.latestReleaseTime)
                }
                val baseComparator = if (sortMode == 1) byTime else byName

                val sorted = modules.sortedWith { a, b ->
                    val aUpgradable = upgradableByPackage[a.name] == true
                    val bUpgradable = upgradableByPackage[b.name] == true
                    when {
                        upgradableFirst && aUpgradable != bUpgradable -> if (aUpgradable) -1 else 1
                        else -> baseComparator.compare(a, b)
                    }
                }

                val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                    .withLocale(App.getLocale())
                    .withZone(ZoneId.systemDefault())

                val rows = sorted.mapNotNull { module ->
                    val pkg = module.name ?: return@mapNotNull null
                    val updatedRaw = repoLoader.getLatestReleaseTime(pkg, channel) ?: module.latestReleaseTime
                    val updatedText = if (!updatedRaw.isNullOrBlank()) {
                        runCatching { formatter.format(Instant.parse(updatedRaw)) }.getOrDefault(updatedRaw)
                    } else {
                        null
                    }
                    RepoRowModel(
                        module = module,
                        packageName = pkg,
                        title = module.description ?: pkg,
                        summary = module.summary?.takeIf { it.isNotBlank() },
                        updatedText = updatedText,
                        upgradable = upgradableByPackage[pkg] == true,
                    )
                }
                RepoUiData(rows = rows, upgradableCount = rows.count { it.upgradable })
            }
        }.fold(
            onSuccess = { ScreenLoadState.Ready(it) },
            onFailure = { ScreenLoadState.Error(it.message ?: "Failed to load repository") },
        )
    }

    val repoData = (uiState as? ScreenLoadState.Ready)?.value
    val subtitle = when (val state = uiState) {
        ScreenLoadState.Loading -> stringResourceCompat(R.string.loading)
        is ScreenLoadState.Error -> stringResourceCompat(R.string.repo_load_failed, state.message)
        is ScreenLoadState.Ready -> {
            if (state.value.upgradableCount > 0) {
                activity.resources.getQuantityString(R.plurals.module_repo_upgradable, state.value.upgradableCount, state.value.upgradableCount)
            } else {
                stringResourceCompat(R.string.module_repo_up_to_date)
            }
        }
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                title = stringResourceCompat(R.string.module_repo),
                subtitle = subtitle,
                query = query,
                onQueryChange = { query = it },
                onRefresh = { triggerReload(forceRemote = true) },
                extraActions = {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text(stringResourceCompat(R.string.sort_by_name)) }, onClick = {
                            prefs.edit { putInt("repo_sort", 0) }
                            expanded = false
                            refreshToken += 1
                        })
                        DropdownMenuItem(text = { Text(stringResourceCompat(R.string.sort_by_update_time)) }, onClick = {
                            prefs.edit { putInt("repo_sort", 1) }
                            expanded = false
                            refreshToken += 1
                        })
                        DropdownMenuItem(
                            text = { Text(stringResourceCompat(R.string.sort_upgradable_first)) },
                            trailingIcon = { Checkbox(checked = upgradableFirst, onCheckedChange = null) },
                            onClick = {
                                prefs.edit { putBoolean("upgradable_first", !upgradableFirst) }
                                expanded = false
                                refreshToken += 1
                            },
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when (val state = uiState) {
            ScreenLoadState.Loading -> {
                Box(modifier = Modifier.padding(innerPadding)) {
                    LoadingStatePanel()
                }
            }
            is ScreenLoadState.Error -> {
                Box(modifier = Modifier.padding(innerPadding)) {
                    ErrorStatePanel(
                        title = stringResourceCompat(R.string.module_repo),
                        message = state.message,
                        onRetry = { triggerReload(forceRemote = true) },
                    )
                }
            }
            is ScreenLoadState.Ready -> {
                if (repoData == null || repoData.rows.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                        Text(stringResourceCompat(R.string.list_empty))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(repoData.rows, key = { it.packageName }) { row ->
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    onOpenRepoItem(row.packageName)
                                },
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(row.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                    Text(row.packageName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (!row.summary.isNullOrBlank()) {
                                        Text(row.summary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    }
                                    if (!row.updatedText.isNullOrBlank()) {
                                        Text(
                                            stringResourceCompat(R.string.module_repo_updated_time, row.updatedText),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                    if (row.upgradable) {
                                        AssistChip(onClick = {}, label = { Text(stringResourceCompat(R.string.update_available)) })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RepoItemScreen(
    activity: MainActivity,
    modulePackageName: String,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val repoLoader = remember { RepoLoader.getInstance() }
    val scope = rememberCoroutineScope()

    var refreshToken by remember { mutableIntStateOf(0) }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    DisposableEffect(modulePackageName) {
        val listener = object : RepoLoader.RepoListener {
            override fun onRepoLoaded() {
                App.getMainHandler().post { refreshToken += 1 }
            }

            override fun onModuleReleasesLoaded(module: OnlineModule?) {
                if (module?.name == modulePackageName) {
                    App.getMainHandler().post { refreshToken += 1 }
                }
            }

            override fun onThrowable(t: Throwable?) {
                App.getMainHandler().post {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            stringResourceCompat(R.string.repo_load_failed, t?.localizedMessage ?: "unknown"),
                        )
                    }
                }
            }
        }
        repoLoader.addListener(listener)
        onDispose { repoLoader.removeListener(listener) }
    }

    val module = remember(refreshToken, modulePackageName) { repoLoader.getOnlineModule(modulePackageName) }

    if (module == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(modulePackageName) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                )
            },
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(stringResourceCompat(R.string.list_empty))
            }
        }
        return
    }

    val releases = remember(refreshToken, modulePackageName) { repoLoader.getReleases(modulePackageName).orEmpty() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(module.description ?: modulePackageName)
                        Text(modulePackageName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        NavUtil.startURL(activity, "https://modules.lsposed.org/module/$modulePackageName")
                    }) {
                        Icon(Icons.Filled.OpenInBrowser, contentDescription = null)
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            ScrollableTabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text(stringResourceCompat(R.string.module_readme)) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text(stringResourceCompat(R.string.module_releases)) })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text(stringResourceCompat(R.string.module_information)) })
            }

            when (selectedTab) {
                0 -> {
                    val readmeHtml = module.readmeHTML
                    if (readmeHtml.isNullOrBlank()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(stringResourceCompat(R.string.list_empty))
                        }
                    } else {
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { context ->
                                WebView(context).apply {
                                    settings.domStorageEnabled = true
                                    webViewClient = object : WebViewClient() {
                                        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                            if (!url.isNullOrBlank()) {
                                                NavUtil.startURL(activity, url)
                                            }
                                            return true
                                        }
                                    }
                                }
                            },
                            update = { webView ->
                                val template = runCatching { App.HTML_TEMPLATE.get() }.getOrNull()
                                val body = if (template.isNullOrBlank()) {
                                    readmeHtml
                                } else {
                                    template.replace("@dir@", "ltr").replace("@body@", readmeHtml)
                                }
                                webView.loadDataWithBaseURL(
                                    "https://github.com",
                                    body,
                                    "text/html",
                                    Charsets.UTF_8.name(),
                                    null,
                                )
                            },
                        )
                    }
                }

                1 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(releases, key = { it.url ?: it.hashCode().toString() }) { release ->
                            ReleaseCard(activity = activity, release = release)
                        }

                        item {
                            Button(onClick = {
                                scope.launch(Dispatchers.IO) {
                                    repoLoader.loadRemoteReleases(modulePackageName)
                                }
                            }) {
                                Icon(Icons.Filled.Download, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResourceCompat(R.string.module_release_view_assets))
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item {
                            InfoLinkCard(
                                title = stringResourceCompat(R.string.module_information_homepage),
                                value = module.homepageUrl,
                                onClick = { module.homepageUrl?.let { NavUtil.startURL(activity, it) } },
                            )
                        }
                        item {
                            InfoLinkCard(
                                title = stringResourceCompat(R.string.module_information_source_url),
                                value = module.sourceUrl,
                                onClick = { module.sourceUrl?.let { NavUtil.startURL(activity, it) } },
                            )
                        }
                        module.collaborators?.forEach { collaborator ->
                            item {
                                InfoLinkCard(
                                    title = stringResourceCompat(R.string.module_information_collaborators),
                                    value = collaborator.name ?: collaborator.login,
                                    onClick = {
                                        val login = collaborator.login
                                        if (!login.isNullOrBlank()) {
                                            NavUtil.startURL(activity, "https://github.com/$login")
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReleaseCard(activity: MainActivity, release: Release) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = release.name ?: release.tagName ?: stringResourceCompat(R.string.module_release),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            val published = release.publishedAt ?: release.createdAt
            if (!published.isNullOrBlank()) {
                Text(
                    text = published,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (!release.description.isNullOrBlank()) {
                Text(release.description ?: "")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = {
                    val url = release.url
                    if (!url.isNullOrBlank()) NavUtil.startURL(activity, url)
                }) {
                    Icon(Icons.Filled.OpenInBrowser, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(stringResourceCompat(R.string.open_in_browser))
                }
            }

            val assets = release.releaseAssets.orEmpty()
            if (assets.isNotEmpty()) {
                Divider()
                assets.forEach { asset ->
                    AssetRow(activity = activity, asset = asset)
                }
            }
        }
    }
}

@Composable
private fun AssetRow(activity: MainActivity, asset: ReleaseAsset) {
    val name = asset.name ?: "asset"
    val sizeText = Formatter.formatShortFileSize(activity, asset.size.toLong())
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val url = asset.downloadUrl
                if (!url.isNullOrBlank()) {
                    NavUtil.startURL(activity, url)
                }
            }
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(name, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                "$sizeText • ${asset.downloadCount}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(Icons.Filled.Download, contentDescription = null)
    }
}

@Composable
private fun InfoLinkCard(title: String, value: String?, onClick: () -> Unit) {
    if (value.isNullOrBlank()) return
    ElevatedCard(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogsScreen(
    snackbarHostState: SnackbarHostState,
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var logs by remember { mutableStateOf<List<String>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var wordWrap by remember { mutableStateOf(App.getPreferences().getBoolean("enable_word_wrap", false)) }
    val horizontalLogScroll = rememberScrollState()

    val saveLogsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip"),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            runCatching {
                val cr = App.getInstance().contentResolver
                cr.openFileDescriptor(uri, "wt")?.use { zipFd ->
                    LSPManagerServiceHolder.getService().getLogs(zipFd)
                }
            }.onSuccess {
                scope.launch {
                    snackbarHostState.showSnackbar(stringResourceCompat(R.string.logs_saved))
                }
            }.onFailure {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        stringResourceCompat(R.string.logs_save_failed2, it.cause?.message ?: it.message ?: "unknown"),
                    )
                }
            }
        }
    }

    fun loadLogs() {
        loading = true
        scope.launch(Dispatchers.IO) {
            val verbose = selectedTab == 0
            val list = runCatching {
                ConfigManager.getLog(verbose)?.use { pfd ->
                    BufferedReader(InputStreamReader(FileInputStream(pfd.fileDescriptor))).use { br ->
                        br.lines().toList()
                    }
                }.orEmpty()
            }.getOrElse { throwable ->
                listOf(throwable.stackTraceToString())
            }

            withContext(Dispatchers.Main) {
                logs = list
                loading = false
            }
        }
    }

    LaunchedEffect(selectedTab) {
        loadLogs()
    }

    LaunchedEffect(wordWrap, selectedTab) {
        if (!wordWrap) {
            horizontalLogScroll.scrollTo(0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResourceCompat(R.string.Logs), fontWeight = FontWeight.Black)
                        Text(
                            if (ConfigManager.isVerboseLogEnabled()) {
                                stringResourceCompat(R.string.enabled_verbose_log)
                            } else {
                                stringResourceCompat(R.string.disabled_verbose_log)
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val now = LocalDateTime.now()
                        saveLogsLauncher.launch("LSPosed_$now.zip")
                    }) {
                        Icon(Icons.Filled.Save, contentDescription = null)
                    }
                    IconButton(onClick = {
                        val verbose = selectedTab == 0
                        scope.launch(Dispatchers.IO) {
                            val ok = ConfigManager.clearLogs(verbose)
                            withContext(Dispatchers.Main) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (ok) stringResourceCompat(R.string.logs_cleared)
                                        else stringResourceCompat(R.string.logs_clear_failed_2),
                                    )
                                }
                                if (ok) loadLogs()
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = null)
                    }
                    IconButton(onClick = {
                        App.getPreferences().edit().putBoolean("enable_word_wrap", !wordWrap).apply()
                        wordWrap = !wordWrap
                    }) {
                        Icon(Icons.Filled.Article, contentDescription = null)
                    }
                    IconButton(onClick = {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }) {
                        Icon(Icons.Filled.ArrowUpward, contentDescription = null)
                    }
                    IconButton(onClick = {
                        scope.launch {
                            if (logs.isNotEmpty()) listState.animateScrollToItem(logs.lastIndex)
                        }
                    }) {
                        Icon(Icons.Filled.ArrowDownward, contentDescription = null)
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            ScrollableTabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text(stringResourceCompat(R.string.verbose_log)) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text(stringResourceCompat(R.string.modules_log)) })
            }

            if (loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (wordWrap) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    items(logs) { line ->
                        Text(
                            text = line,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            softWrap = true,
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(horizontalLogScroll),
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentWidth(),
                        state = listState,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        items(logs) { line ->
                            Text(
                                text = line,
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                softWrap = false,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    activity: MainActivity,
    snackbarHostState: SnackbarHostState,
) {
    val prefs = App.getPreferences()
    val scope = rememberCoroutineScope()

    var refreshToken by remember { mutableIntStateOf(0) }

    var disableVerboseLog by remember(refreshToken) { mutableStateOf(!ConfigManager.isVerboseLogEnabled()) }
    var enableLogWatchdog by remember(refreshToken) { mutableStateOf(ConfigManager.isLogWatchdogEnabled()) }
    var dexObfuscate by remember(refreshToken) { mutableStateOf(ConfigManager.isDexObfuscateEnabled()) }
    var statusNotification by remember(refreshToken) { mutableStateOf(ConfigManager.enableStatusNotification()) }
    var blackDarkTheme by remember(refreshToken) { mutableStateOf(prefs.getBoolean("black_dark_theme", false)) }
    var darkThemeMode by remember(refreshToken) {
        mutableStateOf(prefs.getString("dark_theme", ThemeUtil.MODE_NIGHT_FOLLOW_SYSTEM) ?: ThemeUtil.MODE_NIGHT_FOLLOW_SYSTEM)
    }
    var themeColor by remember(refreshToken) { mutableStateOf(prefs.getString("theme_color", "MATERIAL_BLUE") ?: "MATERIAL_BLUE") }
    var language by remember(refreshToken) { mutableStateOf(prefs.getString("language", "SYSTEM") ?: "SYSTEM") }
    var updateChannel by remember(refreshToken) {
        mutableStateOf(
            prefs.getString(
                "update_channel",
                activity.resources.getStringArray(R.array.update_channel_values).first(),
            ) ?: activity.resources.getStringArray(R.array.update_channel_values).first(),
        )
    }
    var doh by remember(refreshToken) { mutableStateOf(prefs.getBoolean("doh", true)) }

    val backupLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/gzip")) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            runCatching { BackupUtils.backup(uri) }
                .onFailure {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            stringResourceCompat(R.string.settings_backup_failed2, it.message ?: ""),
                        )
                    }
                }
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            runCatching { BackupUtils.restore(uri) }
                .onFailure {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            stringResourceCompat(R.string.settings_restore_failed2, it.message ?: ""),
                        )
                    }
                }
        }
    }

    val installed = remember(refreshToken) { ConfigManager.isBinderAlive() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResourceCompat(R.string.Settings), fontWeight = FontWeight.Black)
                        Text(
                            if (installed) {
                                String.format(
                                    LocaleDelegate.defaultLocale,
                                    "%s (%d) - %s",
                                    ConfigManager.getXposedVersionName(),
                                    ConfigManager.getXposedVersionCode(),
                                    ConfigManager.getApi(),
                                )
                            } else {
                                String.format(
                                    LocaleDelegate.defaultLocale,
                                    "%s (%d) - %s",
                                    BuildConfig.VERSION_NAME,
                                    BuildConfig.VERSION_CODE,
                                    stringResourceCompat(R.string.not_installed),
                                )
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SettingsSection(title = stringResourceCompat(R.string.group_network)) {
                val dns = App.getOkHttpClient().dns as? CloudflareDNS
                val showDoh = dns?.noProxy == true
                if (showDoh) {
                    SettingsSwitchItem(
                        title = stringResourceCompat(R.string.dns_over_http),
                        summary = stringResourceCompat(R.string.dns_over_http_summary),
                        icon = Icons.Filled.Storage,
                        checked = doh,
                        onCheckedChange = {
                            doh = it
                            prefs.edit { putBoolean("doh", it) }
                            dns.DoH = it
                        },
                    )
                }
            }

            SettingsSection(title = stringResourceCompat(R.string.settings_language)) {
                SettingsDropdownItem(
                    title = stringResourceCompat(R.string.settings_language),
                    summary = when (language) {
                        "SYSTEM" -> activity.getString(rikka.core.R.string.follow_system)
                        "zh-CN" -> Locale.SIMPLIFIED_CHINESE.getDisplayName(Locale.SIMPLIFIED_CHINESE)
                        else -> Locale.ENGLISH.getDisplayName(Locale.ENGLISH)
                    },
                    icon = Icons.Filled.Language,
                    entries = listOf(
                        "SYSTEM" to activity.getString(rikka.core.R.string.follow_system),
                        "en" to "English",
                        "zh-CN" to "简体中文",
                    ),
                    onSelected = { value ->
                        language = value
                        prefs.edit { putString("language", value) }
                        val locale = App.getLocale(value)
                        val res = App.getInstance().resources
                        val config = res.configuration
                        config.setLocale(locale)
                        LocaleDelegate.defaultLocale = locale
                        @Suppress("DEPRECATION")
                        res.updateConfiguration(config, res.displayMetrics)
                        activity.restart()
                    },
                )

                SettingsActionItem(
                    title = stringResourceCompat(R.string.settings_translation),
                    summary = stringResourceCompat(R.string.settings_translation_summary, stringResourceCompat(R.string.app_name)),
                    icon = Icons.Filled.Language,
                    onClick = { NavUtil.startURL(activity, "https://crowdin.com/project/lsposed_jingmatrix") },
                )

                val translators = Html.fromHtml(stringResourceCompat(R.string.translators), Html.FROM_HTML_MODE_LEGACY).toString()
                if (translators != "null") {
                    SettingsActionItem(
                        title = stringResourceCompat(R.string.settings_translation_contributors),
                        summary = translators,
                        icon = Icons.Filled.Info,
                        onClick = {},
                    )
                }
            }

            SettingsSection(title = stringResourceCompat(R.string.settings_group_theme)) {
                val darkEntries = listOf(
                    ThemeUtil.MODE_NIGHT_FOLLOW_SYSTEM to stringResourceCompat(R.string.dark_theme_follow_system),
                    ThemeUtil.MODE_NIGHT_NO to stringResourceCompat(R.string.dark_theme_off),
                    ThemeUtil.MODE_NIGHT_YES to stringResourceCompat(R.string.dark_theme_on),
                )
                SettingsDropdownItem(
                    title = stringResourceCompat(R.string.dark_theme),
                    summary = darkEntries.firstOrNull { it.first == darkThemeMode }?.second ?: darkThemeMode,
                    icon = Icons.Filled.DarkMode,
                    entries = darkEntries,
                    onSelected = {
                        darkThemeMode = it
                        prefs.edit { putString("dark_theme", it) }
                        activity.restart()
                    },
                )

                SettingsSwitchItem(
                    title = stringResourceCompat(R.string.pure_black_dark_theme),
                    summary = stringResourceCompat(R.string.pure_black_dark_theme_summary),
                    icon = Icons.Filled.DarkMode,
                    checked = blackDarkTheme,
                    onCheckedChange = {
                        blackDarkTheme = it
                        prefs.edit { putBoolean("black_dark_theme", it) }
                        activity.restart()
                    },
                )

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    val colorValues = activity.resources.getStringArray(R.array.color_values).toList()
                    val colorTexts = activity.resources.getStringArray(R.array.color_texts).toList()
                    SettingsDropdownItem(
                        title = stringResourceCompat(R.string.theme_color),
                        summary = colorTexts.getOrElse(colorValues.indexOf(themeColor).coerceAtLeast(0)) { themeColor },
                        icon = Icons.Filled.ColorLens,
                        entries = colorValues.zip(colorTexts),
                        onSelected = {
                            themeColor = it
                            prefs.edit { putString("theme_color", it) }
                            activity.restart()
                        },
                    )
                } else {
                    SettingsActionItem(
                        title = stringResourceCompat(R.string.theme_color_system),
                        summary = activity.getString(rikka.core.R.string.follow_system),
                        icon = Icons.Filled.ColorLens,
                        enabled = false,
                        onClick = {},
                    )
                }
            }

            SettingsSection(title = stringResourceCompat(R.string.settings_group_framework)) {
                SettingsSwitchItem(
                    title = stringResourceCompat(R.string.settings_disable_verbose_log),
                    summary = stringResourceCompat(R.string.settings_disable_verbose_log_summary),
                    icon = Icons.Filled.Article,
                    checked = disableVerboseLog,
                    enabled = installed,
                    onCheckedChange = {
                        disableVerboseLog = it
                        ConfigManager.setVerboseLogEnabled(!it)
                    },
                )

                SettingsSwitchItem(
                    title = stringResourceCompat(R.string.settings_enable_log_watchdog),
                    summary = stringResourceCompat(R.string.settings_enable_log_watchdog_summary),
                    icon = Icons.Filled.Security,
                    checked = enableLogWatchdog,
                    enabled = installed,
                    onCheckedChange = {
                        enableLogWatchdog = it
                        ConfigManager.setLogWatchdog(it)
                    },
                )

                SettingsSwitchItem(
                    title = stringResourceCompat(R.string.settings_xposed_api_call_protection),
                    summary = stringResourceCompat(R.string.settings_xposed_api_call_protection_summary),
                    icon = Icons.Filled.Security,
                    checked = dexObfuscate,
                    enabled = installed,
                    onCheckedChange = {
                        dexObfuscate = it
                        if (ConfigManager.setDexObfuscateEnabled(it)) {
                            scope.launch {
                                snackbarHostState.showSnackbar(stringResourceCompat(R.string.reboot_required))
                            }
                        }
                    },
                )

                SettingsSwitchItem(
                    title = stringResourceCompat(R.string.settings_enable_status_notification),
                    summary = stringResourceCompat(R.string.settings_enable_status_notification_summary),
                    icon = Icons.Filled.AdminPanelSettings,
                    checked = statusNotification,
                    enabled = installed,
                    onCheckedChange = {
                        statusNotification = it
                        ConfigManager.setEnableStatusNotification(it)
                    },
                )

                if (App.isParasitic) {
                    SettingsActionItem(
                        title = stringResourceCompat(R.string.create_shortcut),
                        summary = stringResourceCompat(R.string.settings_create_shortcut_summary),
                        icon = Icons.Filled.Add,
                        onClick = {
                            val ok = ShortcutUtil.requestPinLaunchShortcut {
                                prefs.edit().putBoolean("never_show_welcome", true).apply()
                            }
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (ok) stringResourceCompat(R.string.settings_shortcut_pinned_hint)
                                    else stringResourceCompat(R.string.settings_unsupported_pin_shortcut_summary),
                                )
                            }
                        },
                    )
                }
            }

            SettingsSection(title = stringResourceCompat(R.string.settings_group_repo)) {
                val channelValues = activity.resources.getStringArray(R.array.update_channel_values).toList()
                val channelTexts = activity.resources.getStringArray(R.array.update_channel_texts).toList()
                SettingsDropdownItem(
                    title = stringResourceCompat(R.string.settings_update_channel),
                    summary = channelTexts.getOrElse(channelValues.indexOf(updateChannel).coerceAtLeast(0)) { updateChannel },
                    icon = Icons.Filled.Sync,
                    entries = channelValues.zip(channelTexts),
                    onSelected = {
                        updateChannel = it
                        prefs.edit { putString("update_channel", it) }
                        RepoLoader.getInstance().updateLatestVersion(it)
                    },
                )
            }

            SettingsSection(title = stringResourceCompat(R.string.settings_backup_and_restore)) {
                SettingsActionItem(
                    title = stringResourceCompat(R.string.settings_backup),
                    summary = stringResourceCompat(R.string.settings_backup_summery),
                    icon = Icons.Filled.Backup,
                    enabled = installed,
                    onClick = {
                        val now = LocalDateTime.now()
                        backupLauncher.launch("LSPosed_$now.lsp")
                    },
                )
                SettingsActionItem(
                    title = stringResourceCompat(R.string.settings_restore),
                    summary = stringResourceCompat(R.string.settings_restore_summery),
                    icon = Icons.Filled.Restore,
                    enabled = installed,
                    onClick = {
                        restoreLauncher.launch(arrayOf("*/*"))
                    },
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                SettingsSection(title = stringResourceCompat(R.string.settings_group_system)) {
                    val hiddenIconShown = runCatching {
                        Settings.Global.getInt(activity.contentResolver, "show_hidden_icon_apps_enabled", 1) != 0
                    }.getOrDefault(true)
                    var showHidden by remember(refreshToken) { mutableStateOf(hiddenIconShown) }
                    SettingsSwitchItem(
                        title = stringResourceCompat(R.string.settings_show_hidden_icon_apps_enabled),
                        summary = stringResourceCompat(R.string.settings_show_hidden_icon_apps_enabled_summary),
                        icon = Icons.Filled.Settings,
                        checked = showHidden,
                        enabled = installed,
                        onCheckedChange = {
                            showHidden = it
                            ConfigManager.setHiddenIcon(!it)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    summary: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(icon, contentDescription = null)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                if (!summary.isNullOrBlank()) {
                    Text(summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
        }
    }
}

@Composable
private fun SettingsActionItem(
    title: String,
    summary: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(alpha = if (enabled) 1f else 0.5f)
            .clickable(enabled = enabled, onClick = onClick),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(icon, contentDescription = null)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                if (!summary.isNullOrBlank()) {
                    Text(summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun SettingsDropdownItem(
    title: String,
    summary: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    entries: List<Pair<String, String>>,
    onSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true },
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(icon, contentDescription = null)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        entries.forEach { (value, label) ->
            DropdownMenuItem(text = { Text(label) }, onClick = {
                expanded = false
                onSelected(value)
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    title: String,
    subtitle: String? = null,
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: (() -> Unit)? = null,
    onRefresh: (() -> Unit)? = null,
    extraActions: @Composable (() -> Unit)? = null,
) {
    var searching by rememberSaveable { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (searching) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(end = if (onBack == null) 14.dp else 0.dp),
                    placeholder = { Text(stringResourceCompat(R.string.search_go)) },
                )
            } else {
                Column {
                    Text(title, fontWeight = FontWeight.Black)
                    if (!subtitle.isNullOrBlank()) {
                        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
        },
        actions = {
            if (!searching) {
                if (onRefresh != null) {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Filled.Sync, contentDescription = null)
                    }
                }
                if (extraActions != null) {
                    extraActions()
                }
                IconButton(onClick = { searching = true }) {
                    Icon(Icons.Filled.Search, contentDescription = null)
                }
            } else {
                IconButton(onClick = {
                    onQueryChange("")
                    searching = false
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = null)
                }
            }
        },
    )
}

private fun computeRepoUpgradableCount(moduleUtil: ModuleUtil, repoLoader: RepoLoader): Int {
    val modules = moduleUtil.modules ?: return 0
    val processed = HashSet<String>()
    var count = 0
    modules.forEach { (k, v) ->
        val pkg = k.first
        if (processed.add(pkg)) {
            val ver = repoLoader.getModuleLatestVersion(pkg)
            if (ver != null && ver.upgradable(v.versionCode, v.versionName)) {
                count += 1
            }
        }
    }
    return count
}

private fun parseInstantOrZero(raw: String?): Instant {
    if (raw.isNullOrBlank()) return Instant.EPOCH
    return runCatching { Instant.parse(raw) }.getOrElse { Instant.EPOCH }
}

private fun stringResourceCompat(@StringRes id: Int, vararg args: Any): String {
    return App.getInstance().getString(id, *args)
}
