package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Traverses context wrappers to find the host Activity.
 */
fun Context.findActivity(): Activity? {
  var currentContext = this
  while (currentContext is ContextWrapper) {
    if (currentContext is Activity) return currentContext
    currentContext = currentContext.baseContext
  }
  return null
}

/**
 * Configures any Android Window (Activity or Dialog) for true edge-to-edge full-screen rendering:
 * - Extends layout into status bar, navigation bar, and display cutout (notch/punch-hole) areas
 * - Removes system status/navigation bar contrast scrims and semi-transparent dark strips
 * - Sets transparent status & navigation bars with light icons for dark backgrounds
 */
@Suppress("DEPRECATION")
fun configureEdgeToEdgeWindow(window: Window, isFullScreenDialog: Boolean = false) {
  try {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    window.statusBarColor = android.graphics.Color.TRANSPARENT
    window.navigationBarColor = android.graphics.Color.TRANSPARENT

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      val attrs = window.attributes
      if (attrs.layoutInDisplayCutoutMode != WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES) {
        attrs.layoutInDisplayCutoutMode =
          WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        window.attributes = attrs
      }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      window.isStatusBarContrastEnforced = false
      window.isNavigationBarContrastEnforced = false
    }

    if (isFullScreenDialog) {
      window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
      window.setDimAmount(0f)
      window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
      window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
      window.setGravity(Gravity.CENTER)
      window.setWindowAnimations(0)
      window.decorView.setPadding(0, 0, 0, 0)
      window.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      )
    }

    val controller = WindowCompat.getInsetsController(window, window.decorView)
    controller.isAppearanceLightStatusBars = false
    controller.isAppearanceLightNavigationBars = false
  } catch (_: Exception) {}
}

/**
 * Configures the current Compose Dialog window to span the entire physical display
 * edge-to-edge (including behind the status bar, camera cutout, and navigation bar),
 * eliminating top/bottom black or system-colored strips inside Dialogs.
 */
@Composable
fun ConfigureFullScreenDialogWindow() {
  val view = LocalView.current
  SideEffect {
    val parentView = view.parent as? ViewGroup
    parentView?.setPadding(0, 0, 0, 0)
    val dialogWindow = (view.parent as? DialogWindowProvider)?.window
      ?: (view as? DialogWindowProvider)?.window
    if (dialogWindow != null) {
      configureEdgeToEdgeWindow(dialogWindow, isFullScreenDialog = true)
    }
  }
}

/**
 * Shared centered modal dialog container that fixes popup/dialog display behavior across the app:
 * 1. Ensures the root composable inside Dialog fills the full window so Compose DialogLayout never
 *    places a non-fullscreen card at (0, 0) top-left or slides it down from the top.
 * 2. Renders a smooth translucent dark backdrop scrim that dismisses on outside tap.
 * 3. Respects top status-bar/cutout, bottom navigation-bar, and IME insets across all phone sizes.
 * 4. Applies a consistent, professional centered scale + fade opening animation.
 */
@Composable
fun CenteredModalDialogContainer(
  onDismiss: () -> Unit,
  scrimAlpha: Float = 0.68f,
  content: @Composable BoxScope.() -> Unit
) {
  ConfigureFullScreenDialogWindow()
  val topSafePadding = rememberResponsiveTopBarPadding(extraTopSpacing = 12.dp)
  val bottomSafePadding = rememberResponsiveNavigationBarPadding(extraBottomSpacing = 12.dp)

  val openProgress = remember { Animatable(0f) }
  LaunchedEffect(Unit) {
    openProgress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
    )
  }

  val p = openProgress.value
  val cardScale = 0.92f + 0.08f * p
  val cardAlpha = 0.85f + 0.15f * p
  val backdropAlpha = (scrimAlpha * (0.4f + 0.6f * p)).coerceIn(0f, 1f)

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF04000C).copy(alpha = backdropAlpha))
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) { onDismiss() }
      .padding(
        top = topSafePadding,
        bottom = bottomSafePadding,
        start = 10.dp,
        end = 10.dp
      )
      .imePadding(),
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .graphicsLayer {
          scaleX = cardScale
          scaleY = cardScale
          alpha = cardAlpha
        }
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null
        ) { /* Consume taps inside modal surface */ },
      contentAlignment = Alignment.Center,
      content = content
    )
  }
}

/**
 * Shared bottom-sheet modal dialog container for bottom-anchored sheets inside Dialog.
 * Smoothly slides up from the bottom edge without top clipping or top-down window sliding.
 */
@Composable
fun BottomSheetModalDialogContainer(
  onDismiss: () -> Unit,
  scrimAlpha: Float = 0.64f,
  content: @Composable BoxScope.() -> Unit
) {
  ConfigureFullScreenDialogWindow()
  val density = LocalDensity.current
  val openProgress = remember { Animatable(0f) }
  LaunchedEffect(Unit) {
    openProgress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
    )
  }

  val p = openProgress.value
  val slideOffsetPx = with(density) { ((1f - p) * 40f).dp.toPx() }
  val sheetAlpha = 0.85f + 0.15f * p
  val backdropAlpha = (scrimAlpha * (0.4f + 0.6f * p)).coerceIn(0f, 1f)

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF04000C).copy(alpha = backdropAlpha))
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) { onDismiss() },
    contentAlignment = Alignment.BottomCenter
  ) {
    Box(
      modifier = Modifier
        .graphicsLayer {
          translationY = slideOffsetPx
          alpha = sheetAlpha
        }
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null
        ) { /* Consume taps inside bottom sheet */ },
      contentAlignment = Alignment.BottomCenter,
      content = content
    )
  }
}

/**
 * Responsive top status-bar / cutout safe-area padding for headers and top controls.
 * Ensures backgrounds extend behind the top status bar while keeping interactive header
 * controls immediately below the status bar / camera cutout across both Activity screens
 * and full-screen Dialogs without double-counting window offsets.
 */
@Composable
fun rememberResponsiveTopBarPadding(
  extraTopSpacing: Dp = 2.dp
): Dp {
  val density = LocalDensity.current
  val context = LocalContext.current
  val view = LocalView.current
  val activity = remember(context) { context.findActivity() }
  val activityDecorView = activity?.window?.decorView

  var viewTopOnScreenPx by remember(view) { mutableIntStateOf(0) }

  DisposableEffect(view) {
    fun updateTopBound() {
      try {
        val loc = IntArray(2)
        view.getLocationOnScreen(loc)
        viewTopOnScreenPx = loc[1].coerceAtLeast(0)
      } catch (_: Exception) {}
    }
    updateTopBound()
    val listener = ViewTreeObserver.OnGlobalLayoutListener { updateTopBound() }
    val vto = view.viewTreeObserver
    if (vto.isAlive) {
      vto.addOnGlobalLayoutListener(listener)
    }
    onDispose {
      try {
        val currentVto = view.viewTreeObserver
        if (currentVto.isAlive) {
          currentVto.removeOnGlobalLayoutListener(listener)
        }
      } catch (_: Exception) {}
    }
  }

  val composeStatusTopPx = with(density) {
    WindowInsets.statusBars.asPaddingValues().calculateTopPadding().roundToPx()
  }

  val viewStatusTopPx = try {
    ViewCompat.getRootWindowInsets(view)
      ?.getInsetsIgnoringVisibility(
        WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.displayCutout()
      )?.top ?: 0
  } catch (_: Exception) {
    0
  }

  val activityStatusTopPx = try {
    activityDecorView?.let { decor ->
      ViewCompat.getRootWindowInsets(decor)
        ?.getInsetsIgnoringVisibility(
          WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.displayCutout()
        )?.top
    } ?: 0
  } catch (_: Exception) {
    0
  }

  val fallbackStatusPx = remember(context, density) {
    try {
      val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
      if (resId > 0) context.resources.getDimensionPixelSize(resId)
      else with(density) { 24.dp.roundToPx() }
    } catch (_: Exception) {
      with(density) { 24.dp.roundToPx() }
    }
  }

  val rawStatusBarHeightPx = maxOf(composeStatusTopPx, viewStatusTopPx, activityStatusTopPx, fallbackStatusPx)
  val effectiveStatusBarHeightPx = (rawStatusBarHeightPx - viewTopOnScreenPx).coerceAtLeast(0)
  val statusBarHeightDp = with(density) { effectiveStatusBarHeightPx.toDp() }
  return statusBarHeightDp + extraTopSpacing.coerceAtLeast(0.dp)
}

/**
 * Comprehensive, self-calibrating bottom safe-area inset calculation.
 * Works seamlessly across:
 * - Gesture navigation mode (thin 16-24dp pill)
 * - 3-Button navigation mode (48-56dp back/home/recents bar)
 * - Standard Activity composable screens and full-screen Compose Dialog windows
 * - Small, medium, and tall phone screen sizes, aspect ratios, and densities
 */
@Composable
fun rememberResponsiveNavigationBarPadding(
  extraBottomSpacing: Dp = 6.dp
): Dp {
  val density = LocalDensity.current
  val context = LocalContext.current
  val view = LocalView.current
  val activity = remember(context) { context.findActivity() }
  val activityDecorView = activity?.window?.decorView

  // Track real-time on-screen position and height of the current Compose host view
  var viewTopOnScreenPx by remember(view) { mutableIntStateOf(0) }
  var viewHeightPx by remember(view) { mutableIntStateOf(view.height) }

  DisposableEffect(view) {
    fun updateBounds() {
      try {
        val loc = IntArray(2)
        view.getLocationOnScreen(loc)
        viewTopOnScreenPx = loc[1]
        viewHeightPx = view.height
      } catch (_: Exception) {}
    }
    updateBounds()
    val listener = ViewTreeObserver.OnGlobalLayoutListener { updateBounds() }
    val vto = view.viewTreeObserver
    if (vto.isAlive) {
      vto.addOnGlobalLayoutListener(listener)
    }
    onDispose {
      try {
        val currentVto = view.viewTreeObserver
        if (currentVto.isAlive) {
          currentVto.removeOnGlobalLayoutListener(listener)
        }
      } catch (_: Exception) {}
    }
  }

  // 1. Determine physical screen height in pixels
  val displayMetricsHeightPx = context.resources.displayMetrics.heightPixels
  val realScreenHeightPx = remember(context, activityDecorView, viewHeightPx) {
    var maxHeight = displayMetricsHeightPx
    try {
      if (activityDecorView != null && activityDecorView.height > 0) {
        val decorLoc = IntArray(2)
        activityDecorView.getLocationOnScreen(decorLoc)
        maxHeight = maxOf(maxHeight, decorLoc[1] + activityDecorView.height)
      }
      val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
      if (wm != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
          maxHeight = maxOf(maxHeight, wm.currentWindowMetrics.bounds.height())
        } else {
          @Suppress("DEPRECATION")
          val realMetrics = DisplayMetrics()
          @Suppress("DEPRECATION")
          wm.defaultDisplay?.getRealMetrics(realMetrics)
          maxHeight = maxOf(maxHeight, realMetrics.heightPixels)
        }
      }
    } catch (_: Exception) {}
    maxHeight
  }

  // 2. Determine status bar / top cutout height in pixels
  val composeStatusTopPx = with(density) {
    WindowInsets.statusBars.asPaddingValues().calculateTopPadding().roundToPx()
  }
  val activityStatusTopPx = try {
    activityDecorView?.let { decor ->
      ViewCompat.getRootWindowInsets(decor)
        ?.getInsetsIgnoringVisibility(
          WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.displayCutout()
        )?.top
    } ?: 0
  } catch (_: Exception) {
    0
  }
  val fallbackStatusPx = remember(context, density) {
    try {
      val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
      if (resId > 0) context.resources.getDimensionPixelSize(resId)
      else with(density) { 28.dp.roundToPx() }
    } catch (_: Exception) {
      with(density) { 28.dp.roundToPx() }
    }
  }
  val statusBarHeightPx = maxOf(composeStatusTopPx, activityStatusTopPx, fallbackStatusPx)

  // 3. Determine live system navigation bar height in pixels from real Android WindowInsets
  val composeNavBottomPx = with(density) {
    maxOf(
      WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
      WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    ).roundToPx()
  }

  val viewNavBottomPx = try {
    ViewCompat.getRootWindowInsets(view)
      ?.getInsetsIgnoringVisibility(
        WindowInsetsCompat.Type.navigationBars()
      )?.bottom ?: 0
  } catch (_: Exception) {
    0
  }

  val activityNavBottomPx = try {
    activityDecorView?.let { decor ->
      ViewCompat.getRootWindowInsets(decor)
        ?.getInsetsIgnoringVisibility(
          WindowInsetsCompat.Type.navigationBars()
        )?.bottom
    } ?: 0
  } catch (_: Exception) {
    0
  }

  val displayDiffBottomPx = (realScreenHeightPx - displayMetricsHeightPx - statusBarHeightPx).coerceAtLeast(0)

  val fallbackNavPx = remember(context, density) {
    try {
      val resId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
      if (resId > 0) context.resources.getDimensionPixelSize(resId)
      else with(density) { 20.dp.roundToPx() }
    } catch (_: Exception) {
      with(density) { 20.dp.roundToPx() }
    }
  }

  val minGesturePillPx = with(density) { 16.dp.roundToPx() }
  val liveNavHeightPx = maxOf(composeNavBottomPx, viewNavBottomPx, activityNavBottomPx)
  val navBarHeightPx = when {
    liveNavHeightPx > 0 -> liveNavHeightPx.coerceAtLeast(minGesturePillPx)
    displayDiffBottomPx > 0 -> displayDiffBottomPx.coerceAtLeast(minGesturePillPx)
    else -> fallbackNavPx.coerceAtLeast(minGesturePillPx)
  }

  // 4. Detect if this composable is rendered inside a separate Dialog window
  val isDialogWindow = activityDecorView != null && view.rootView !== activityDecorView

  val requiredBottomInsetPx = if (isDialogWindow && viewTopOnScreenPx > 0) {
    // Only apply offset compensation if the Dialog window was not positioned at y = 0
    val effectiveTopPx = viewTopOnScreenPx
    val composeMeasuredHeightPx = maxOf(viewHeightPx, displayMetricsHeightPx)
    val safeScreenBottomPx = realScreenHeightPx - navBarHeightPx
    val bottomOverlapPx = (effectiveTopPx + composeMeasuredHeightPx) - safeScreenBottomPx
    bottomOverlapPx.coerceAtLeast(navBarHeightPx)
  } else {
    navBarHeightPx
  }

  val requiredBottomInsetDp = with(density) { requiredBottomInsetPx.toDp() }
  return requiredBottomInsetDp + extraBottomSpacing.coerceAtLeast(0.dp)
}
