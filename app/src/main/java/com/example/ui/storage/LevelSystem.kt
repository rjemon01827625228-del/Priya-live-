package com.example.ui.storage

/**
 * Mathematical Level Progression System (Level 1 to Level 50)
 *
 * Mathematical Rule:
 * Required Coins = Level × 2,000
 *
 * Base Progression:
 * Level 1 = 2,000 coins
 * Level 2 = 4,000 coins
 * Level 3 = 6,000 coins
 * Level 4 = 8,000 coins
 * Level 5 = 10,000 coins
 * ...
 * Level 10 = 20,000 coins
 * Level 20 = 40,000 coins
 * Level 30 = 60,000 coins
 * Level 40 = 80,000 coins
 * Level 50 = 100,000 coins
 *
 * Visual Tiers:
 * Level 1–10  → Color Tier 1 (Emerald / Teal)
 * Level 11–20 → Color Tier 2 (Sapphire / Royal Blue)
 * Level 21–30 → Color Tier 3 (Amethyst / Imperial Purple)
 * Level 31–40 → Color Tier 4 (Vivid Amber / Sunset Gold)
 * Level 41–49 → Color Tier 5 (Crimson Ruby / Emperor Red)
 * Level 50    → Premium Gold / Celestial Crown Tier
 */
data class LevelProgressInfo(
  val currentLevel: Int,
  val currentCoins: Long,
  val nextLevel: Int,
  val coinsRequiredForNextLevel: Long,
  val progressRatio: Float,
  val coinsRemaining: Long,
  val rewards: List<String>
)

data class LevelTier(
  val level: Int,
  val requiredCoins: Long,
  val title: String,
  val perks: String,
  val badgeEmoji: String,
  val isUnlocked: Boolean
)

object LevelSystem {
  const val MIN_LEVEL = 1
  const val MAX_LEVEL = 50
  const val COINS_PER_LEVEL = 2_000L

  /**
   * Required Coins = Level * 2,000
   */
  fun requiredCoinsForLevel(level: Int): Long {
    val clamped = level.coerceIn(MIN_LEVEL, MAX_LEVEL)
    return clamped * COINS_PER_LEVEL
  }

  fun getRequiredCoins(level: Int): Long = requiredCoinsForLevel(level)

  /**
   * Calculates the user's level (1 to 50) from total coin progression.
   * If coins < 2,000, level is 1.
   * Formula: level = (coins / 2,000).coerceIn(1, 50)
   */
  fun calculateLevel(coins: Long): Int {
    if (coins < 2_000L) {
      return 1
    }
    val calculated = (coins / COINS_PER_LEVEL).toInt()
    return calculated.coerceIn(MIN_LEVEL, MAX_LEVEL)
  }

  fun getTierIndex(level: Int): Int {
    return when {
      level >= 50 -> 6
      level >= 41 -> 5
      level >= 31 -> 4
      level >= 21 -> 3
      level >= 11 -> 2
      else -> 1
    }
  }

  fun getTierName(level: Int): String {
    return when {
      level >= 50 -> "Supreme Sovereign"
      level >= 41 -> "Crimson Emperor"
      level >= 31 -> "Sunset Archduke"
      level >= 21 -> "Amethyst Overlord"
      level >= 11 -> "Sapphire Knight"
      else -> "Emerald Pioneer"
    }
  }

  fun getAllTiers(currentCoins: Long): List<LevelTier> {
    return (1..MAX_LEVEL).map { lvl ->
      val req = requiredCoinsForLevel(lvl)
      val unlocked = currentCoins >= req
      LevelTier(
        level = lvl,
        requiredCoins = req,
        title = getTierName(lvl),
        perks = "Level $lvl Official Privileges",
        badgeEmoji = "",
        isUnlocked = unlocked
      )
    }
  }

  /**
   * Generates a full progress report for the given coin amount.
   */
  fun getProgressInfo(coins: Long): LevelProgressInfo {
    val currentLvl = calculateLevel(coins)
    val nextLvl = (currentLvl + 1).coerceAtMost(MAX_LEVEL)
    val nextLevelReq = requiredCoinsForLevel(nextLvl)
    val currentLevelReq = requiredCoinsForLevel(currentLvl)

    val progressRatio: Float
    val coinsRemaining: Long

    if (currentLvl >= MAX_LEVEL) {
      progressRatio = 1.0f
      coinsRemaining = 0L
    } else {
      val span = nextLevelReq - currentLevelReq
      val inLevel = (coins - currentLevelReq).coerceAtLeast(0L)
      progressRatio = if (span > 0) {
        (inLevel.toDouble() / span.toDouble()).toFloat().coerceIn(0f, 1f)
      } else {
        1.0f
      }
      coinsRemaining = (nextLevelReq - coins).coerceAtLeast(0L)
    }

    return LevelProgressInfo(
      currentLevel = currentLvl,
      currentCoins = coins,
      nextLevel = nextLvl,
      coinsRequiredForNextLevel = nextLevelReq,
      progressRatio = progressRatio,
      coinsRemaining = coinsRemaining,
      rewards = getRewardsForLevel(currentLvl)
    )
  }

  /**
   * Level Perks & Privileges configuration for Level 1 to Level 50.
   * Professional clean descriptions with zero emojis or stickers.
   */
  fun getRewardsForLevel(level: Int): List<String> {
    return when {
      level >= 50 -> listOf(
        "Supreme Sovereign Avatar Halo",
        "Celestial Sovereign Entrance Animation",
        "Global Server Announcement Privilege",
        "100% Experience Boost Multiplier"
      )
      level >= 41 -> listOf(
        "Crimson Emperor Profile Crest",
        "Cyber Hypercar Entrance Animation",
        "Unlimited Megaphone Broadcast Rights",
        "Room Kick & Mute Immunity"
      )
      level >= 31 -> listOf(
        "Archduke Holographic Badge",
        "Phantom Neon Entrance Effect",
        "Glowing Chat Bubble Styling",
        "Audio Party Priority Seat Access"
      )
      level >= 21 -> listOf(
        "Amethyst Prestige Identity Badge",
        "Superbike Entrance Animation",
        "Streamer Spotlight Highlight",
        "Store Discount Privileges"
      )
      level >= 11 -> listOf(
        "Sapphire Noble Nameplate",
        "Cyan Entrance Flash Accent",
        "Special Chat Styling",
        "Level Exclusive Gift Access"
      )
      else -> listOf(
        "Emerald Identity Crest",
        "Standard Room Voice & Chat Access",
        "Daily Progression Privileges"
      )
    }
  }

  /**
   * Formats coin amounts with comma separation.
   */
  fun formatCoins(amount: Long): String {
    return String.format(java.util.Locale.US, "%,d", amount)
  }
}
