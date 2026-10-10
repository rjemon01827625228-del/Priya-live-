package com.example.ui.storage

import android.content.Context
import java.util.Locale

/**
 * VIP RECHARGE AUTO-UNLOCK SYSTEM
 *
 * VIP is NOT purchased separately.
 * VIP level is automatically unlocked according to the user's REAL recharge total.
 *
 * VIP Levels:
 * VIP 1  = 100,000 (100K)
 * VIP 2  = 200,000 (200K)
 * VIP 3  = 400,000 (400K)
 * VIP 4  = 800,000 (800K)
 * VIP 5  = 1,600,000 (1.6M)
 * VIP 6  = 3,200,000 (3.2M)
 * VIP 7  = 6,400,000 (6.4M)
 * VIP 8  = 12,800,000 (12.8M)
 * VIP 9  = 25,600,000 (25.6M)
 * VIP 10 = 51,200,000 (51.2M)
 */
data class VipTier(
  val level: Int,
  val requiredRecharge: Long,
  val title: String,
  val frameId: String,
  val frameName: String,
  val presetStyle: String,
  val badgeText: String,
  val benefits: List<String>
)

data class VerifiedRechargeResult(
  val amountAdded: Long,
  val newTotalRecharge: Long,
  val previousVip: Int,
  val currentVip: Int,
  val didLevelUpVip: Boolean
)

object VipSystem {
  val VIP_LEVELS = listOf(
    VipTier(
      level = 1,
      requiredRecharge = 100_000L,
      title = "Imperial SVIP 1",
      frameId = "frame_vip_1",
      frameName = "Imperial Laurel Crown Frame",
      presetStyle = "VIP_1_IMPERIAL",
      badgeText = "SVIP 1",
      benefits = listOf(
        "Imperial Laurel Golden Avatar Frame",
        "Exclusive Gold SVIP 1 Crest Badge",
        "Special Room Entrance Banner",
        "Golden Highlighted Bullet Chat in Live Streams",
        "Priority Live Stream Call Queue"
      )
    ),
    VipTier(
      level = 2,
      requiredRecharge = 200_000L,
      title = "Royal SVIP 2",
      frameId = "frame_vip_2",
      frameName = "Royal Lion Crest Frame",
      presetStyle = "VIP_2_ROYAL",
      badgeText = "SVIP 2",
      benefits = listOf(
        "Royal Lion Crest Glowing Avatar Frame",
        "Exclusive Royal SVIP 2 Crest Badge",
        "Animated Neon Entrance Flash Effect",
        "Highlighted Chat with Golden Ribbon",
        "1.1x PK Battle Multiplier Boost"
      )
    ),
    VipTier(
      level = 3,
      requiredRecharge = 400_000L,
      title = "Majestic SVIP 3",
      frameId = "frame_vip_3",
      frameName = "Majestic Ruby Laurels Frame",
      presetStyle = "VIP_3_MAJESTIC",
      badgeText = "SVIP 3",
      benefits = listOf(
        "Majestic Ruby Laurels Avatar Frame",
        "Exclusive Ruby SVIP 3 Crest Badge",
        "Full-Screen Live Entrance Animation",
        "Special VIP Flying Bullet Chat",
        "Instant Live Guest Call Auto-Approval"
      )
    ),
    VipTier(
      level = 4,
      requiredRecharge = 800_000L,
      title = "Sovereign SVIP 4",
      frameId = "frame_vip_4",
      frameName = "Sovereign Gold Wing Frame",
      presetStyle = "VIP_4_SOVEREIGN",
      badgeText = "SVIP 4",
      benefits = listOf(
        "Sovereign Gold Wing Frame",
        "Sovereign SVIP 4 Crest Badge",
        "Custom Voice Party Sound Effect",
        "VIP Exclusive Chat Emotes & Stickers",
        "1.2x PK Battle Multiplier Boost"
      )
    ),
    VipTier(
      level = 5,
      requiredRecharge = 1_600_000L,
      title = "Grand Monarch SVIP 5",
      frameId = "frame_vip_5",
      frameName = "Grand Monarch Diamond Tiara",
      presetStyle = "VIP_5_MONARCH",
      badgeText = "SVIP 5",
      benefits = listOf(
        "Grand Monarch Diamond Tiara Frame",
        "Grand Monarch SVIP 5 Crest Badge",
        "Hypercar Super Entrance Vehicle",
        "Exclusive VIP Room Theme",
        "1.3x PK Multiplier Boost"
      )
    ),
    VipTier(
      level = 6,
      requiredRecharge = 3_200_000L,
      title = "Celestial SVIP 6",
      frameId = "frame_vip_6",
      frameName = "Celestial Astral Halo Frame",
      presetStyle = "VIP_6_CELESTIAL",
      badgeText = "SVIP 6",
      benefits = listOf(
        "Celestial Astral Halo Frame",
        "Celestial SVIP 6 Crest Badge",
        "Celestial Dragon Room Entrance",
        "Rainbow Animated Bullet Chat",
        "Dedicated VIP Customer Support"
      )
    ),
    VipTier(
      level = 7,
      requiredRecharge = 6_400_000L,
      title = "Overlord SVIP 7",
      frameId = "frame_vip_7",
      frameName = "Overlord Flaming Crest Frame",
      presetStyle = "VIP_7_OVERLORD",
      badgeText = "SVIP 7",
      benefits = listOf(
        "Overlord Flaming Crest Frame",
        "Overlord SVIP 7 Crest Badge",
        "Custom Animated Voice Mic Rim",
        "1.5x PK Multiplier Boost",
        "Permanent Anti-Mute & Anti-Kick"
      )
    ),
    VipTier(
      level = 8,
      requiredRecharge = 12_800_000L,
      title = "Emperor SVIP 8",
      frameId = "frame_vip_8",
      frameName = "Imperial Emperor Dragon Frame",
      presetStyle = "VIP_8_EMPEROR",
      badgeText = "SVIP 8",
      benefits = listOf(
        "Imperial Emperor Dragon Frame",
        "Emperor SVIP 8 Crest Badge",
        "Global Broadcast Announcement upon Login",
        "Exclusive Emperor Profile Decoration",
        "1.8x PK Multiplier Boost"
      )
    ),
    VipTier(
      level = 9,
      requiredRecharge = 25_600_000L,
      title = "Supreme SVIP 9",
      frameId = "frame_vip_9",
      frameName = "Supreme Solar Crown Frame",
      presetStyle = "VIP_9_SUPREME",
      badgeText = "SVIP 9",
      benefits = listOf(
        "Supreme Solar Crown Frame",
        "Supreme SVIP 9 Crest Badge",
        "Solar Phoenix Room Entrance",
        "Custom 7-Digit Vanity ID Option",
        "2.0x PK Multiplier Boost"
      )
    ),
    VipTier(
      level = 10,
      requiredRecharge = 51_200_000L,
      title = "Godlike SVIP 10",
      frameId = "frame_vip_10",
      frameName = "Eternal Godlike Regalia Frame",
      presetStyle = "VIP_10_GODLIKE",
      badgeText = "SVIP 10",
      benefits = listOf(
        "Eternal Godlike Regalia Frame",
        "Godlike SVIP 10 Crest Badge",
        "Cosmic Titan Entrance Spectacle",
        "Permanent Custom Room Name Banner",
        "Direct Contact with Platform Executive Management"
      )
    )
  )

  /**
   * Calculate VIP from REAL lifetime recharge amount.
   * Matches business logic specification:
   * 100,000 recharge  = VIP 1
   * 200,000 recharge  = VIP 2
   * 400,000 recharge  = VIP 3
   * 800,000 recharge  = VIP 4
   * 1,600,000 recharge = VIP 5
   * 3,200,000 recharge = VIP 6
   * 6,400,000 recharge = VIP 7
   * 12,800,000 recharge = VIP 8
   * 25,600,000 recharge = VIP 9
   * 51,200,000 recharge = VIP 10
   */
  fun calculateVIPLevel(totalRecharge: Long): Int {
    var vipLevel = 0
    for (vip in VIP_LEVELS) {
      if (totalRecharge >= vip.requiredRecharge) {
        vipLevel = vip.level
      }
    }
    return vipLevel
  }

  fun formatCoins(value: Long): String {
    return when {
      value >= 1_000_000L -> {
        val m = value.toDouble() / 1_000_000.0
        if (m == m.toLong().toDouble()) "${m.toLong()}M" else String.format(Locale.US, "%.1fM", m)
      }
      value >= 1_000L -> {
        val k = value.toDouble() / 1_000.0
        if (k == k.toLong().toDouble()) "${k.toLong()}K" else String.format(Locale.US, "%.1fK", k)
      }
      else -> value.toString()
    }
  }

  fun getTier(level: Int): VipTier? = VIP_LEVELS.find { it.level == level }
}
