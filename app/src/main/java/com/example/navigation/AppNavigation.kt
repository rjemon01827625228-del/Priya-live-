package com.example.navigation

/**
 * Type-safe navigation routes for Priya Live.
 * Distinguishes strictly between Top-Level Root routes (which display the bottom nav)
 * and Inner Sub-Page routes (which are full-screen and completely hide the bottom nav).
 */
sealed interface AppRoute {
  // Inner Sub-Pages (All Full-Screen, Bottom Navigation Bar is HIDDEN)
  data object Store : AppRoute
  data object AgencyCentre : AppRoute
  data object VipCentre : AppRoute
  data object EditProfile : AppRoute
  data object ProfileDetail : AppRoute
  data object Settings : AppRoute
  data object HostCentre : AppRoute
  data object HostRequest : AppRoute
  data object LevelDetails : AppRoute
  data object MyQrCode : AppRoute
  data object HelpCenter : AppRoute
  data object AboutUs : AppRoute
  data object LiveHistory : AppRoute
  data object ParallelBox : AppRoute
  data object CommissionRecord : AppRoute
  data object CoinTopUp : AppRoute
  data object CpSpace : AppRoute
  data object FollowersList : AppRoute
  data object AccountSwitch : AppRoute
}
