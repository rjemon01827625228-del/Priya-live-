package com.example.ui.storage

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class StoreItem(
  val id: String,
  val category: String, // "FRAME", "THEME", "VEHICLE", "BUBBLE"
  val name: String,
  val durationText: String, // "7 DAY", "15 DAY", "30 DAY", "1 MONTH", "PERMANENT"
  val durationDays: Int, // 1, 3, 7, 15, 30, -1
  val coinPrice: Long,
  val imageUrl: String? = null,
  val presetStyle: String = "GOLDEN_CROWN",
  val isActive: Boolean = true,
  val description: String = ""
)

data class OwnedItem(
  val itemId: String,
  val category: String,
  val purchaseTimestamp: Long,
  val expiryTimestamp: Long, // -1 for permanent, or ms timestamp
  val isEquipped: Boolean = false
) {
  val isExpired: Boolean
    get() = expiryTimestamp > 0 && System.currentTimeMillis() > expiryTimestamp

  val remainingTimeText: String
    get() {
      if (expiryTimestamp <= 0) return "Permanent"
      val diff = expiryTimestamp - System.currentTimeMillis()
      if (diff <= 0) return "Expired"
      val days = diff / (1000L * 60 * 60 * 24)
      val hours = (diff % (1000L * 60 * 60 * 24)) / (1000L * 60 * 60)
      return if (days > 0) "${days}d ${hours}h left" else "${hours}h left"
    }
}

sealed class PurchaseResult {
  data class Success(val item: StoreItem, val remainingCoins: Long) : PurchaseResult()
  data class InsufficientCoins(val currentCoins: Long, val price: Long, val needed: Long) : PurchaseResult()
  data class Error(val message: String) : PurchaseResult()
}

object StoreCenterStorage {
  private const val PREFS_NAME = "store_catalog_clean_v4"
  private const val KEY_STORE_ITEMS = "key_store_items_v4"
  private const val KEY_OWNED_ITEMS_PREFIX = "key_owned_items_"
  private const val KEY_EQUIPPED_PREFIX = "key_equipped_"

  private fun getPrefs(context: Context): SharedPreferences {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
  }

  // Default store catalog starts completely empty (fresh)
  val defaultStoreItems: List<StoreItem> = emptyList()

  // Preset templates available for admin selection when creating items
  val presetTemplates = listOf(
    StoreItem(
      id = "frame_golden_crown",
      category = "FRAME",
      name = "Royal Crown",
      durationText = "7 DAY",
      durationDays = 7,
      coinPrice = 10_000L,
      presetStyle = "GOLDEN_CROWN",
      isActive = true,
      description = "Regal 24K imperial crown with glowing ruby stones and laurel aura."
    ),
    StoreItem(
      id = "frame_angel_wings",
      category = "FRAME",
      name = "Angel Wings",
      durationText = "7 DAY",
      durationDays = 7,
      coinPrice = 15_000L,
      presetStyle = "ANGEL_WINGS",
      isActive = true,
      description = "Ethereal feathered wings with glowing pink-cyan halo and heavenly sparkles."
    ),
    StoreItem(
      id = "frame_spring_blossom",
      category = "FRAME",
      name = "Spring Blossom",
      durationText = "15 DAY",
      durationDays = 15,
      coinPrice = 20_000L,
      presetStyle = "SPRING_BLOSSOM",
      isActive = true,
      description = "Enchanted cherry blossom petals and emerald leaves with morning dew glow."
    ),
    StoreItem(
      id = "frame_starry_moon",
      category = "FRAME",
      name = "Starry Moon",
      durationText = "15 DAY",
      durationDays = 15,
      coinPrice = 25_000L,
      presetStyle = "STARRY_MOON",
      isActive = true,
      description = "Celestial golden crescent moon with violet cosmic mist and dangling stars."
    ),
    StoreItem(
      id = "frame_ruby_roses",
      category = "FRAME",
      name = "Ruby Roses",
      durationText = "30 DAY",
      durationDays = 30,
      coinPrice = 35_000L,
      presetStyle = "RUBY_ROSES",
      isActive = true,
      description = "Velvet scarlet roses wrapped in golden filigree and passion aura."
    ),
    StoreItem(
      id = "frame_ice_lotus",
      category = "FRAME",
      name = "Ice Crystal Lotus",
      durationText = "30 DAY",
      durationDays = 30,
      coinPrice = 40_000L,
      presetStyle = "ICE_LOTUS",
      isActive = true,
      description = "Glacial diamond lotus petals radiating crystalline sub-zero blue glow."
    ),
    StoreItem(
      id = "frame_heart_tiara",
      category = "FRAME",
      name = "Heart Tiara",
      durationText = "30 DAY",
      durationDays = 30,
      coinPrice = 50_000L,
      presetStyle = "HEART_TIARA",
      isActive = true,
      description = "Radiant golden tiara adorned with a beating neon magenta heart gem."
    ),
    StoreItem(
      id = "frame_cyber_matrix",
      category = "FRAME",
      name = "Cyber Matrix",
      durationText = "PERMANENT",
      durationDays = -1,
      coinPrice = 100_000L,
      presetStyle = "CYBER_MATRIX",
      isActive = true,
      description = "Ultra futuristic holographic HUD with revolving pulse rings and energy arcs."
    ),

    // ----------------- THEMES -----------------
    StoreItem(
      id = "theme_cyber_neon",
      category = "THEME",
      name = "Cyberpunk Night",
      durationText = "30 DAY",
      durationDays = 30,
      coinPrice = 20_000L,
      presetStyle = "THEME_CYBER",
      isActive = true,
      description = "Neon violet and cyan party room theme with grid lines."
    ),
    StoreItem(
      id = "theme_royal_gold",
      category = "THEME",
      name = "Imperial Gold",
      durationText = "30 DAY",
      durationDays = 30,
      coinPrice = 30_000L,
      presetStyle = "THEME_GOLD",
      isActive = true,
      description = "Luxurious royal palace golden room theme."
    ),
    StoreItem(
      id = "theme_cosmic_galaxy",
      category = "THEME",
      name = "Galaxy Cosmos",
      durationText = "PERMANENT",
      durationDays = -1,
      coinPrice = 80_000L,
      presetStyle = "THEME_GALAXY",
      isActive = true,
      description = "Deep celestial nebula stars and cosmic stellar dust."
    ),

    // ----------------- VEHICLES -----------------
    StoreItem(
      id = "vehicle_supercar",
      category = "VEHICLE",
      name = "Cyber Supercar",
      durationText = "7 DAY",
      durationDays = 7,
      coinPrice = 30_000L,
      presetStyle = "VEHICLE_CAR",
      isActive = true,
      description = "High-speed electric sports car entrance with tire smoke and strobe lights."
    ),
    StoreItem(
      id = "vehicle_phoenix",
      category = "VEHICLE",
      name = "Fire Phoenix",
      durationText = "15 DAY",
      durationDays = 15,
      coinPrice = 60_000L,
      presetStyle = "VEHICLE_PHOENIX",
      isActive = true,
      description = "Legendary flame bird soaring into the room with sparkling ember wings."
    ),
    StoreItem(
      id = "vehicle_dragon",
      category = "VEHICLE",
      name = "Golden Dragon",
      durationText = "30 DAY",
      durationDays = 30,
      coinPrice = 120_000L,
      presetStyle = "VEHICLE_DRAGON",
      isActive = true,
      description = "Imperial mythical golden dragon spiraling across the screen."
    ),

    // ----------------- BUBBLES -----------------
    StoreItem(
      id = "bubble_holo_cyan",
      category = "BUBBLE",
      name = "Hologram Cyan",
      durationText = "7 DAY",
      durationDays = 7,
      coinPrice = 5_000L,
      presetStyle = "BUBBLE_CYAN",
      isActive = true,
      description = "Translucent cyan glass chat bubble with neon contour."
    ),
    StoreItem(
      id = "bubble_love_pink",
      category = "BUBBLE",
      name = "Love Romance",
      durationText = "15 DAY",
      durationDays = 15,
      coinPrice = 8_000L,
      presetStyle = "BUBBLE_PINK",
      isActive = true,
      description = "Soft glowing pink pastel bubble with miniature floating hearts."
    ),
    StoreItem(
      id = "bubble_vip_gold",
      category = "BUBBLE",
      name = "Golden Crown",
      durationText = "30 DAY",
      durationDays = 30,
      coinPrice = 15_000L,
      presetStyle = "BUBBLE_GOLD",
      isActive = true,
      description = "Exclusive gold plated VIP chat bubble with royal insignia."
    )
  )

  /**
   * Reads all store items from SharedPreferences (Admin dynamic source).
   * Starts fresh with 0 items; displays items once added.
   */
  fun getAllItems(context: Context): List<StoreItem> {
    val prefs = getPrefs(context)
    val rawJson = prefs.getString(KEY_STORE_ITEMS, null)
    if (rawJson.isNullOrBlank()) {
      return emptyList()
    }
    return try {
      val arr = JSONArray(rawJson)
      val list = mutableListOf<StoreItem>()
      for (i in 0 until arr.length()) {
        val obj = arr.getJSONObject(i)
        list.add(
          StoreItem(
            id = obj.getString("id"),
            category = obj.optString("category", "FRAME"),
            name = obj.getString("name"),
            durationText = obj.optString("durationText", "7 DAY"),
            durationDays = obj.optInt("durationDays", 7),
            coinPrice = obj.optLong("coinPrice", 10_000L),
            imageUrl = if (obj.has("imageUrl") && !obj.isNull("imageUrl")) obj.getString("imageUrl") else null,
            presetStyle = obj.optString("presetStyle", "GOLDEN_CROWN"),
            isActive = obj.optBoolean("isActive", true),
            description = obj.optString("description", "")
          )
        )
      }
      list
    } catch (_: Exception) {
      emptyList()
    }
  }

  /**
   * Returns only active items belonging to the given category.
   */
  fun getActiveItemsByCategory(context: Context, category: String): List<StoreItem> {
    return getAllItems(context).filter { it.category.equals(category, ignoreCase = true) && it.isActive }
  }

  /**
   * Admin: Add a new item to the store catalog.
   */
  fun addItem(context: Context, item: StoreItem) {
    val items = getAllItems(context).toMutableList()
    // Remove if already exists with same ID
    items.removeAll { it.id == item.id }
    items.add(0, item)
    saveItems(context, items)
  }

  /**
   * Admin: Update an existing item.
   */
  fun updateItem(context: Context, item: StoreItem) {
    val items = getAllItems(context).toMutableList()
    val idx = items.indexOfFirst { it.id == item.id }
    if (idx != -1) {
      items[idx] = item
    } else {
      items.add(0, item)
    }
    saveItems(context, items)
  }

  /**
   * Admin: Toggle Active/Inactive state.
   */
  fun toggleItemActive(context: Context, itemId: String): Boolean {
    val items = getAllItems(context).toMutableList()
    val idx = items.indexOfFirst { it.id == itemId }
    if (idx != -1) {
      val newState = !items[idx].isActive
      items[idx] = items[idx].copy(isActive = newState)
      saveItems(context, items)
      return newState
    }
    return false
  }

  /**
   * Admin: Delete item from catalog.
   */
  fun deleteItem(context: Context, itemId: String) {
    val items = getAllItems(context).toMutableList()
    items.removeAll { it.id == itemId }
    saveItems(context, items)
  }

  /**
   * Admin: Reset to default factory items.
   */
  fun resetToDefaults(context: Context) {
    saveItems(context, defaultStoreItems)
  }

  private fun saveItems(context: Context, items: List<StoreItem>) {
    val arr = JSONArray()
    items.forEach { item ->
      val obj = JSONObject().apply {
        put("id", item.id)
        put("category", item.category)
        put("name", item.name)
        put("durationText", item.durationText)
        put("durationDays", item.durationDays)
        put("coinPrice", item.coinPrice)
        put("imageUrl", item.imageUrl ?: JSONObject.NULL)
        put("presetStyle", item.presetStyle)
        put("isActive", item.isActive)
        put("description", item.description)
      }
      arr.put(obj)
    }
    getPrefs(context).edit().putString(KEY_STORE_ITEMS, arr.toString()).apply()
  }

  // ---------------------------------------------------------------------------
  // USER INVENTORY & OWNERSHIP
  // ---------------------------------------------------------------------------

  fun getOwnedItems(context: Context, userId: String? = null): List<OwnedItem> {
    val uid = userId ?: UserProfileStorage.getActiveUserId(context)
    val prefs = getPrefs(context)
    val raw = prefs.getString(KEY_OWNED_ITEMS_PREFIX + uid, null) ?: return emptyList()
    return try {
      val arr = JSONArray(raw)
      val list = mutableListOf<OwnedItem>()
      for (i in 0 until arr.length()) {
        val obj = arr.getJSONObject(i)
        list.add(
          OwnedItem(
            itemId = obj.getString("itemId"),
            category = obj.optString("category", "FRAME"),
            purchaseTimestamp = obj.optLong("purchaseTimestamp", System.currentTimeMillis()),
            expiryTimestamp = obj.optLong("expiryTimestamp", -1L),
            isEquipped = obj.optBoolean("isEquipped", false)
          )
        )
      }
      list
    } catch (_: Exception) {
      emptyList()
    }
  }

  private fun saveOwnedItems(context: Context, uid: String, list: List<OwnedItem>) {
    val arr = JSONArray()
    list.forEach { item ->
      val obj = JSONObject().apply {
        put("itemId", item.itemId)
        put("category", item.category)
        put("purchaseTimestamp", item.purchaseTimestamp)
        put("expiryTimestamp", item.expiryTimestamp)
        put("isEquipped", item.isEquipped)
      }
      arr.put(obj)
    }
    getPrefs(context).edit().putString(KEY_OWNED_ITEMS_PREFIX + uid, arr.toString()).apply()
  }

  /**
   * Gets the currently equipped frame for the active user.
   */
  fun getEquippedFrame(context: Context, userId: String? = null): StoreItem? {
    val uid = userId ?: UserProfileStorage.getActiveUserId(context)
    val prefs = getPrefs(context)
    val frameId = prefs.getString(KEY_EQUIPPED_PREFIX + uid + "_FRAME", null) ?: return null
    // Check if owned and not expired
    val owned = getOwnedItems(context, uid).find { it.itemId == frameId }
    if (owned == null || owned.isExpired) return null
    return getAllItems(context).find { it.id == frameId }
  }

  /**
   * Equips an item (e.g. FRAME, THEME) to the user's profile.
   */
  fun equipItem(context: Context, category: String, itemId: String, userId: String? = null) {
    val uid = userId ?: UserProfileStorage.getActiveUserId(context)
    val owned = getOwnedItems(context, uid).toMutableList()
    val updated = owned.map {
      if (it.category.equals(category, ignoreCase = true)) {
        it.copy(isEquipped = (it.itemId == itemId))
      } else {
        it
      }
    }
    saveOwnedItems(context, uid, updated)
    getPrefs(context).edit().putString(KEY_EQUIPPED_PREFIX + uid + "_" + category.uppercase(), itemId).apply()
  }

  /**
   * Unequips whatever item is active in the category.
   */
  fun unequipItem(context: Context, category: String, userId: String? = null) {
    val uid = userId ?: UserProfileStorage.getActiveUserId(context)
    val owned = getOwnedItems(context, uid).toMutableList()
    val updated = owned.map {
      if (it.category.equals(category, ignoreCase = true)) it.copy(isEquipped = false) else it
    }
    saveOwnedItems(context, uid, updated)
    getPrefs(context).edit().remove(KEY_EQUIPPED_PREFIX + uid + "_" + category.uppercase()).apply()
  }

  fun isItemEquipped(context: Context, category: String, itemId: String, userId: String? = null): Boolean {
    val uid = userId ?: UserProfileStorage.getActiveUserId(context)
    val current = getPrefs(context).getString(KEY_EQUIPPED_PREFIX + uid + "_" + category.uppercase(), null)
    return current == itemId
  }

  /**
   * Grants a permanent item to the user's inventory without duplicates.
   * Prevents duplicate records per Rule 18.
   */
  fun grantPermanentItem(context: Context, itemId: String, category: String, userId: String? = null) {
    val uid = userId ?: UserProfileStorage.getActiveUserId(context)
    val owned = getOwnedItems(context, uid).toMutableList()
    val existingIndex = owned.indexOfFirst { it.itemId == itemId }
    if (existingIndex == -1) {
      owned.add(
        OwnedItem(
          itemId = itemId,
          category = category,
          purchaseTimestamp = System.currentTimeMillis(),
          expiryTimestamp = -1L,
          isEquipped = false
        )
      )
      saveOwnedItems(context, uid, owned)
    }
  }

  fun hasItem(context: Context, itemId: String, userId: String? = null): Boolean {
    val uid = userId ?: UserProfileStorage.getActiveUserId(context)
    return getOwnedItems(context, uid).any { it.itemId == itemId && !it.isExpired }
  }

  // ---------------------------------------------------------------------------
  // PURCHASE SYSTEM (Safe, atomic balance check and deduction)
  // ---------------------------------------------------------------------------

  fun purchaseItem(context: Context, item: StoreItem, userId: String? = null): PurchaseResult {
    val uid = userId ?: UserProfileStorage.getActiveUserId(context)
    val currentBalance = UserProfileStorage.getCoinBalance(context, uid)

    if (currentBalance < item.coinPrice) {
      val needed = item.coinPrice - currentBalance
      return PurchaseResult.InsufficientCoins(currentCoins = currentBalance, price = item.coinPrice, needed = needed)
    }

    // Deduct coins safely
    val newBalance = currentBalance - item.coinPrice
    UserProfileStorage.setCoinBalance(context, newBalance, uid)

    // Calculate expiry
    val now = System.currentTimeMillis()
    val expiry = if (item.durationDays > 0) {
      now + (item.durationDays.toLong() * 24L * 60L * 60L * 1000L)
    } else {
      -1L
    }

    // Add to owned inventory
    val ownedList = getOwnedItems(context, uid).toMutableList()
    val existingIdx = ownedList.indexOfFirst { it.itemId == item.id }
    if (existingIdx != -1) {
      // Extend expiry if already owned and not permanent
      val existing = ownedList[existingIdx]
      val newExpiry = if (existing.expiryTimestamp > 0 && expiry > 0) {
        val base = if (existing.expiryTimestamp > now) existing.expiryTimestamp else now
        base + (item.durationDays.toLong() * 24L * 60L * 60L * 1000L)
      } else {
        expiry
      }
      ownedList[existingIdx] = existing.copy(expiryTimestamp = newExpiry, isEquipped = true)
    } else {
      ownedList.add(
        0,
        OwnedItem(
          itemId = item.id,
          category = item.category,
          purchaseTimestamp = now,
          expiryTimestamp = expiry,
          isEquipped = true
        )
      )
    }

    saveOwnedItems(context, uid, ownedList)

    // Automatically apply/equip to user profile!
    equipItem(context, item.category, item.id, uid)

    return PurchaseResult.Success(item = item, remainingCoins = newBalance)
  }
}
