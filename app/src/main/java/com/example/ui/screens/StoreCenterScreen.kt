package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aistudio.neonparty.nxvqlr.R
import com.example.ui.components.AvatarFrameOverlay
import com.example.ui.storage.LevelSystem
import com.example.ui.storage.OwnedItem
import com.example.ui.storage.PurchaseResult
import com.example.ui.storage.StoreCenterStorage
import com.example.ui.storage.StoreItem
import com.example.ui.storage.UserProfileStorage

@Composable
fun StoreCenterScreen(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activeUserId = remember { UserProfileStorage.getActiveUserId(context) }
  val userProfile = remember { UserProfileStorage.getProfile(context) }

  // State: active category tab. FRAME is the active/default category!
  var selectedCategory by remember { mutableStateOf("FRAME") }

  // Dynamic user coin balance
  var coinBalance by remember { mutableLongStateOf(UserProfileStorage.getCoinBalance(context, activeUserId)) }

  // Catalog and user inventory
  var storeItems by remember { mutableStateOf(emptyList<StoreItem>()) }
  var ownedItems by remember { mutableStateOf(emptyList<OwnedItem>()) }
  var equippedFrame by remember { mutableStateOf<StoreItem?>(null) }

  // Dialog states
  var itemToPurchase by remember { mutableStateOf<StoreItem?>(null) }

  fun reloadData() {
    coinBalance = UserProfileStorage.getCoinBalance(context, activeUserId)
    storeItems = StoreCenterStorage.getAllItems(context)
    ownedItems = StoreCenterStorage.getOwnedItems(context, activeUserId)
    equippedFrame = StoreCenterStorage.getEquippedFrame(context, activeUserId)
  }

  LaunchedEffect(Unit) {
    reloadData()
  }

  val topSafePadding = com.example.ui.components.rememberResponsiveTopBarPadding(extraTopSpacing = 0.dp)
  val bottomSafePadding = com.example.ui.components.rememberResponsiveNavigationBarPadding(extraBottomSpacing = 0.dp)

  // Deep luxury purple / midnight blue gradient background
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(
            Color(0xFF09001A),
            Color(0xFF140233),
            Color(0xFF0A001F)
          )
        )
      )
      .testTag("store_center_screen")
  ) {
    // Ambient glowing decorative background orbs
    Box(
      modifier = Modifier
        .size(240.dp)
        .offset(x = (-60).dp, y = 40.dp)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            listOf(
              Color(0xFFFF007F).copy(alpha = 0.15f),
              Color.Transparent
            )
          )
        )
    )
    Box(
      modifier = Modifier
        .size(260.dp)
        .align(Alignment.TopEnd)
        .offset(x = 80.dp, y = 120.dp)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            listOf(
              Color(0xFF00E5FF).copy(alpha = 0.12f),
              Color.Transparent
            )
          )
        )
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(start = 12.dp, end = 12.dp, top = topSafePadding, bottom = bottomSafePadding)
    ) {
      // -----------------------------------------------------------------------
      // 1. TOP APP BAR (Frosted Glass Back & Amber Coin Balance Pill only)
      // Completely clean: No top text title, no admin/panel corner button!
      // -----------------------------------------------------------------------
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 10.dp, bottom = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // High-end Frosted Glass Back Button
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(
                  Color(0x38FFFFFF),
                  Color(0x14FFFFFF),
                  Color(0x05FFFFFF)
                )
              )
            )
            .border(
              width = 1.2.dp,
              brush = Brush.linearGradient(
                listOf(
                  Color.White.copy(alpha = 0.6f),
                  Color(0xFF00E5FF).copy(alpha = 0.4f),
                  Color.White.copy(alpha = 0.1f)
                )
              ),
              shape = CircleShape
            )
            .clickable(onClick = onBack),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }

        // Luxurious Frosted Amber-Gold Glass Coin Balance Pill (NO admin/panel button in any corner!)
        Box(
          modifier = Modifier
            .height(38.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
              Brush.horizontalGradient(
                listOf(
                  Color(0xFF2E045E).copy(alpha = 0.85f),
                  Color(0xFF16002C).copy(alpha = 0.85f)
                )
              )
            )
            .border(
              width = 1.2.dp,
              brush = Brush.linearGradient(
                listOf(
                  Color(0xFFFFD700),
                  Color(0xFFFFB300).copy(alpha = 0.5f),
                  Color(0xFFFFD700).copy(alpha = 0.8f)
                )
              ),
              shape = RoundedCornerShape(20.dp)
            )
            .clickable {
              selectedCategory = "DIAMOND"
            }
            .padding(horizontal = 12.dp),
          contentAlignment = Alignment.Center
        ) {
          // Specular glass highlight on top
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(1.dp)
              .align(Alignment.TopCenter)
              .background(
                Brush.horizontalGradient(
                  listOf(
                    Color.Transparent,
                    Color(0xFFFFE082).copy(alpha = 0.6f),
                    Color.Transparent
                  )
                )
              )
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(text = "💎", fontSize = 13.sp)
            Text(
              text = LevelSystem.formatCoins(coinBalance),
              color = Color(0xFF00E5FF),
              fontSize = 12.5.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.4.sp
            )
            Box(
              modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(Color(0xFF00E5FF).copy(alpha = 0.25f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Diamonds",
                tint = Color(0xFF00E5FF),
                modifier = Modifier.size(11.dp)
              )
            }
          }
        }
      }

      // -----------------------------------------------------------------------
      // 2. TOP CATEGORY NAVIGATION BAR
      // • DIAMOND • MINE • FRAME (default) • THEME • VEHICLE • BUBBLE
      // -----------------------------------------------------------------------
      val categories = listOf(
        CategoryItem("DIAMOND", "DIAMOND", Icons.Filled.Diamond, Color(0xFF00E5FF)),
        CategoryItem("MINE", "MINE", Icons.Filled.ShoppingBag, Color(0xFFFF4081)),
        CategoryItem("FRAME", "FRAME", Icons.Filled.WorkspacePremium, Color(0xFFFFD700)),
        CategoryItem("THEME", "THEME", Icons.Filled.Palette, Color(0xFFB026FF)),
        CategoryItem("VEHICLE", "VEHICLE", Icons.Filled.DirectionsCar, Color(0xFFFF9100)),
        CategoryItem("BUBBLE", "BUBBLE", Icons.Filled.Sms, Color(0xFF00E676))
      )

      // Top bar row 1: DIAMOND & MINE shortcuts with glowing accents
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        CategoryNavPill(
          title = "DIAMOND RECHARGE",
          icon = Icons.Filled.Diamond,
          isSelected = selectedCategory == "DIAMOND",
          activeColor = Color(0xFF00E5FF),
          modifier = Modifier.weight(1f),
          onClick = { selectedCategory = "DIAMOND" }
        )

        val ownedCount = ownedItems.size
        CategoryNavPill(
          title = "MINE ($ownedCount)",
          icon = Icons.Filled.ShoppingBag,
          isSelected = selectedCategory == "MINE",
          activeColor = Color(0xFFFF4081),
          modifier = Modifier.weight(1f),
          onClick = { selectedCategory = "MINE" }
        )
      }

      // Category bar row 2: Product Item Categories (FRAME as default/active)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        val itemCategories = categories.filter { it.key != "DIAMOND" && it.key != "MINE" }
        itemCategories.forEach { cat ->
          val isSel = selectedCategory == cat.key
          Box(
            modifier = Modifier
              .weight(1f)
              .height(58.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(
                if (isSel) {
                  Brush.verticalGradient(
                    listOf(
                      cat.color.copy(alpha = 0.38f),
                      Color(0xFF26044E).copy(alpha = 0.88f),
                      Color(0xFF110024).copy(alpha = 0.95f)
                    )
                  )
                } else {
                  Brush.verticalGradient(
                    listOf(
                      Color(0x22FFFFFF),
                      Color(0x0CFFFFFF),
                      Color(0x10000000)
                    )
                  )
                }
              )
              .border(
                width = if (isSel) 1.6.dp else 1.dp,
                brush = if (isSel) {
                  Brush.linearGradient(
                    listOf(
                      Color.White.copy(alpha = 0.9f),
                      cat.color,
                      Color.White.copy(alpha = 0.4f)
                    )
                  )
                } else {
                  Brush.linearGradient(
                    listOf(
                      Color.White.copy(alpha = 0.25f),
                      Color.White.copy(alpha = 0.05f)
                    )
                  )
                },
                shape = RoundedCornerShape(16.dp)
              )
              .clickable { selectedCategory = cat.key },
            contentAlignment = Alignment.Center
          ) {
            // Glass top specular highlight
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.TopCenter)
                .background(
                  Brush.horizontalGradient(
                    listOf(
                      Color.Transparent,
                      Color.White.copy(alpha = if (isSel) 0.7f else 0.2f),
                      Color.Transparent
                    )
                  )
                )
            )

            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = cat.icon,
                contentDescription = cat.title,
                tint = if (isSel) cat.color else Color(0xFFB0BEC5),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.height(3.dp))
              Text(
                text = cat.key,
                color = if (isSel) Color.White else Color(0xFF90A4AE),
                fontSize = 11.sp,
                fontWeight = if (isSel) FontWeight.Black else FontWeight.SemiBold,
                letterSpacing = 0.6.sp
              )
            }
          }
        }
      }

      // -----------------------------------------------------------------------
      // 3. MAIN CONTENT DISPLAY BASED ON CATEGORY
      // -----------------------------------------------------------------------
      when (selectedCategory) {
        "FRAME" -> {
          val activeFrames = storeItems.filter { it.category == "FRAME" && it.isActive }
          FrameStoreGrid(
            frames = activeFrames,
            ownedItems = ownedItems,
            equippedFrame = equippedFrame,
            userProfileImageUri = userProfile.profileImageUri,
            onBuyClick = { frame -> itemToPurchase = frame }
          )
        }
        "MINE" -> {
          MineInventoryView(
            ownedItems = ownedItems,
            storeItems = storeItems,
            equippedFrame = equippedFrame,
            userProfileImageUri = userProfile.profileImageUri,
            onEquip = { item ->
              StoreCenterStorage.equipItem(context, item.category, item.id, activeUserId)
              reloadData()
              Toast.makeText(context, "${item.name} equipped to your profile!", Toast.LENGTH_SHORT).show()
            },
            onUnequip = { item ->
              StoreCenterStorage.unequipItem(context, item.category, activeUserId)
              reloadData()
              Toast.makeText(context, "${item.name} removed from your profile.", Toast.LENGTH_SHORT).show()
            },
            onGoShopping = { selectedCategory = "FRAME" }
          )
        }
        "DIAMOND" -> {
          DiamondRechargeView(
            currentCoins = coinBalance,
            onAddCoins = { coinsToAdd ->
              val result = UserProfileStorage.recordVerifiedRecharge(context, coinsToAdd, activeUserId)
              reloadData()
              val vipMsg = if (result.didLevelUpVip) " [VIP ${result.currentVip} Activated!]" else ""
              Toast.makeText(context, "Added ${LevelSystem.formatCoins(coinsToAdd)} Coins!$vipMsg", Toast.LENGTH_SHORT).show()
            }
          )
        }
        else -> {
          // Other categories (THEME, VEHICLE, BUBBLE)
          val items = storeItems.filter { it.category == selectedCategory && it.isActive }
          OtherCategoryStoreGrid(
            categoryTitle = selectedCategory,
            items = items,
            ownedItems = ownedItems,
            onBuyClick = { item -> itemToPurchase = item }
          )
        }
      }
    }
  }

  // ---------------------------------------------------------------------------
  // 4. PURCHASE CONFIRMATION DIALOG
  // ---------------------------------------------------------------------------
  if (itemToPurchase != null) {
    val item = itemToPurchase!!
    val hasEnough = coinBalance >= item.coinPrice
    val remaining = coinBalance - item.coinPrice

    Dialog(
      onDismissRequest = { itemToPurchase = null },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      Surface(
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .clip(RoundedCornerShape(26.dp))
          .border(
            width = 1.6.dp,
            brush = Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFF007F), Color(0xFF00E5FF))),
            shape = RoundedCornerShape(26.dp)
          )
          .testTag("purchase_dialog"),
        color = Color(0xFF100028)
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "CONFIRM PURCHASE",
              color = Color(0xFFFFD700),
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )
            IconButton(onClick = { itemToPurchase = null }, modifier = Modifier.size(28.dp)) {
              Icon(Icons.Filled.Close, contentDescription = "Cancel", tint = Color.White)
            }
          }

          // Item Visual Preview
          Box(
            modifier = Modifier
              .size(110.dp)
              .clip(RoundedCornerShape(20.dp))
              .background(Color(0xFF1B0336))
              .border(1.dp, Color(0xFF7C4DFF), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
          ) {
            if (item.category == "FRAME") {
              AvatarFrameOverlay(
                size = 70.dp,
                frame = item,
                profileImageUri = userProfile.profileImageUri
              )
            } else {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val catIcon = when (item.category) {
                  "THEME" -> Icons.Filled.Palette
                  "VEHICLE" -> Icons.Filled.DirectionsCar
                  "BUBBLE" -> Icons.Filled.Sms
                  else -> Icons.Filled.Star
                }
                Icon(
                  imageVector = catIcon,
                  contentDescription = null,
                  tint = Color(0xFF00E5FF),
                  modifier = Modifier.size(38.dp)
                )
              }
            }
          }

          // Name and Duration
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = item.name,
              color = Color.White,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Validity: ${item.durationText}",
              color = Color(0xFF00E5FF),
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
            if (item.description.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = item.description,
                color = Color(0xFFB39DDB),
                fontSize = 11.sp,
                textAlign = TextAlign.Center
              )
            }
          }

          // Price and Balance Breakdown Box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(Color(0xFF16012E))
              .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(14.dp))
              .padding(12.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Item Price:", color = Color(0xFFB0BEC5), fontSize = 12.sp)
                Text(
                  "💎 ${LevelSystem.formatCoins(item.coinPrice)} Diamonds",
                  color = Color(0xFF00E5FF),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Your Balance:", color = Color(0xFFB0BEC5), fontSize = 12.sp)
                Text(
                  "💎 ${LevelSystem.formatCoins(coinBalance)} Diamonds",
                  color = Color.White,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(1.dp)
                  .background(Color(0x22FFFFFF))
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = if (hasEnough) "Remaining Balance:" else "Diamonds Needed:",
                  color = if (hasEnough) Color(0xFF80DEEA) else Color(0xFFFF5252),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = if (hasEnough) "💎 ${LevelSystem.formatCoins(remaining)} Diamonds" else "💎 ${LevelSystem.formatCoins(item.coinPrice - coinBalance)} Diamonds",
                  color = if (hasEnough) Color(0xFF00E5FF) else Color(0xFFFF5252),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          // Error banner if insufficient diamonds
          if (!hasEnough) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x33FF1744))
                .border(1.dp, Color(0xFFFF1744), RoundedCornerShape(10.dp))
                .padding(8.dp)
            ) {
              Text(
                text = "⚠️ Insufficient Diamonds! You need ${LevelSystem.formatCoins(item.coinPrice - coinBalance)} more diamonds.",
                color = Color(0xFFFF8A80),
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
              )
            }
          }

          // Action Buttons with Luxurious Frosted Glass
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = { itemToPurchase = null },
              modifier = Modifier
                .weight(1f)
                .height(44.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
              shape = RoundedCornerShape(12.dp),
              contentPadding = PaddingValues()
            ) {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(
                    Brush.verticalGradient(
                      listOf(Color(0x33FFFFFF), Color(0x1AFFFFFF))
                    ),
                    RoundedCornerShape(12.dp)
                  )
                  .border(
                    1.dp,
                    Brush.linearGradient(listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.1f))),
                    RoundedCornerShape(12.dp)
                  ),
                contentAlignment = Alignment.Center
              ) {
                Text("Cancel", color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
              }
            }

            if (hasEnough) {
              Button(
                onClick = {
                  val res = StoreCenterStorage.purchaseItem(context, item, activeUserId)
                  if (res is PurchaseResult.Success) {
                    reloadData()
                    itemToPurchase = null
                    Toast.makeText(
                      context,
                      "🎉 ${item.name} purchased & applied to your profile!",
                      Toast.LENGTH_LONG
                    ).show()
                  } else if (res is PurchaseResult.InsufficientCoins) {
                    Toast.makeText(context, "Insufficient Diamonds!", Toast.LENGTH_SHORT).show()
                  }
                },
                modifier = Modifier
                  .weight(1.3f)
                  .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues()
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .background(
                      Brush.verticalGradient(
                        listOf(Color(0xFFFFEA79), Color(0xFFFFD700), Color(0xFFFF9100))
                      ),
                      RoundedCornerShape(12.dp)
                    )
                    .border(
                      1.2.dp,
                      Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.8f), Color(0xFFFFD700))
                      ),
                      RoundedCornerShape(12.dp)
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  // Specular highlight
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(1.dp)
                      .align(Alignment.TopCenter)
                      .background(
                        Brush.horizontalGradient(
                          listOf(Color.Transparent, Color.White.copy(alpha = 0.8f), Color.Transparent)
                        )
                      )
                  )

                  Text(
                    text = "BUY & APPLY",
                    color = Color(0xFF1F0B00),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.6.sp
                  )
                }
              }
            } else {
              // Insufficient coins shortcut to recharge
              Button(
                onClick = {
                  itemToPurchase = null
                  selectedCategory = "DIAMOND"
                },
                modifier = Modifier
                  .weight(1.3f)
                  .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues()
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .background(
                      Brush.verticalGradient(
                        listOf(Color(0xFF80D8FF), Color(0xFF00E5FF), Color(0xFF0091EA))
                      ),
                      RoundedCornerShape(12.dp)
                    )
                    .border(
                      1.dp,
                      Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.8f), Color(0xFF00E5FF))
                      ),
                      RoundedCornerShape(12.dp)
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "+ GET COINS",
                    color = Color(0xFF001E3D),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.4.sp
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

// -----------------------------------------------------------------------------
// CATEGORY NAVIGATION PILL (Frosted Glass with Specular Highlight)
// -----------------------------------------------------------------------------
@Composable
private fun CategoryNavPill(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
  isSelected: Boolean,
  activeColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .height(42.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(
        if (isSelected) {
          Brush.verticalGradient(
            listOf(
              activeColor.copy(alpha = 0.35f),
              Color(0xFF2E045E).copy(alpha = 0.85f),
              Color(0xFF14022A).copy(alpha = 0.95f)
            )
          )
        } else {
          Brush.verticalGradient(
            listOf(
              Color(0x22FFFFFF),
              Color(0x0CFFFFFF),
              Color(0x10000000)
            )
          )
        }
      )
      .border(
        width = if (isSelected) 1.5.dp else 1.dp,
        brush = if (isSelected) {
          Brush.linearGradient(
            listOf(
              activeColor,
              Color.White.copy(alpha = 0.8f),
              activeColor.copy(alpha = 0.6f)
            )
          )
        } else {
          Brush.linearGradient(
            listOf(
              Color.White.copy(alpha = 0.35f),
              Color.White.copy(alpha = 0.08f)
            )
          )
        },
        shape = RoundedCornerShape(16.dp)
      )
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    // Glass specular top sheen
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .align(Alignment.TopCenter)
        .background(
          Brush.horizontalGradient(
            listOf(
              Color.Transparent,
              Color.White.copy(alpha = if (isSelected) 0.6f else 0.25f),
              Color.Transparent
            )
          )
        )
    )

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (isSelected) activeColor else Color(0xFFCFD8DC),
          modifier = Modifier.size(15.dp)
        )
      }
      Text(
        text = title,
        color = if (isSelected) Color.White else Color(0xFFCFD8DC),
        fontSize = 11.5.sp,
        fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
        letterSpacing = 0.4.sp
      )
    }
  }
}

// -----------------------------------------------------------------------------
// FRAME STORE 2-COLUMN GRID (Matches the reference screenshot)
// -----------------------------------------------------------------------------
@Composable
private fun FrameStoreGrid(
  frames: List<StoreItem>,
  ownedItems: List<OwnedItem>,
  equippedFrame: StoreItem?,
  userProfileImageUri: String?,
  onBuyClick: (StoreItem) -> Unit
) {
  if (frames.isEmpty()) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp, vertical = 60.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(
                  Color(0x33FFD700),
                  Color(0x08FFD700),
                  Color.Transparent
                )
              )
            )
            .border(1.2.dp, Color(0x44FFD700), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.Store,
            contentDescription = null,
            tint = Color(0xFFFFD700),
            modifier = Modifier.size(34.dp)
          )
        }
        Text(
          text = "No Items in Store",
          color = Color.White.copy(alpha = 0.92f),
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.4.sp
        )
        Text(
          text = "Store is currently fresh. New items will appear once added.",
          color = Color(0xFFB388FF).copy(alpha = 0.75f),
          fontSize = 12.sp,
          textAlign = TextAlign.Center
        )
      }
    }
    return
  }

  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(bottom = 96.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    items(frames, key = { it.id }) { frame ->
      val isEquipped = equippedFrame?.id == frame.id
      val isOwned = ownedItems.any { it.itemId == frame.id && !it.isExpired }

      FrameCard(
        frame = frame,
        isEquipped = isEquipped,
        isOwned = isOwned,
        userProfileImageUri = userProfileImageUri,
        onBuyClick = { onBuyClick(frame) }
      )
    }
  }
}

// -----------------------------------------------------------------------------
// INDIVIDUAL FRAME CARD
// -----------------------------------------------------------------------------
@Composable
private fun FrameCard(
  frame: StoreItem,
  isEquipped: Boolean,
  isOwned: Boolean,
  userProfileImageUri: String?,
  onBuyClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(
        Brush.verticalGradient(
          listOf(
            Color(0xFF1B0538),
            Color(0xFF110024)
          )
        )
      )
      .border(
        width = 1.3.dp,
        brush = Brush.linearGradient(
          listOf(
            if (isEquipped) Color(0xFF00E5FF) else Color(0xFF7C4DFF).copy(alpha = 0.8f),
            if (isEquipped) Color(0xFFFFD700) else Color(0xFFFF007F).copy(alpha = 0.5f)
          )
        ),
        shape = RoundedCornerShape(20.dp)
      )
      .padding(12.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Top Duration Pill (e.g. 🕒 7 DAY)
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(10.dp))
          .background(Color(0x55000000))
          .border(0.8.dp, Color(0xFF00E5FF).copy(alpha = 0.6f), RoundedCornerShape(10.dp))
          .padding(horizontal = 8.dp, vertical = 3.dp)
      ) {
        Text(
          text = frame.durationText,
          color = Color(0xFF80DEEA),
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
      }

      // Frame Preview (Centered large circular avatar with equipped vector frame)
      Box(
        modifier = Modifier
          .size(92.dp)
          .padding(vertical = 2.dp),
        contentAlignment = Alignment.Center
      ) {
        AvatarFrameOverlay(
          size = 62.dp,
          frame = frame,
          profileImageUri = userProfileImageUri
        )
      }

      // Frame Name
      Text(
        text = frame.name,
        color = Color.White,
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      // Price Tag Pill: 🪙 10,000 COINS
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
        .background(Color(0xFF260447))
        .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "💎", fontSize = 11.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${LevelSystem.formatCoins(frame.coinPrice)} DIAMONDS",
            color = Color(0xFF00E5FF),
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Golden Gradient Liquid-Glass BUY button (or EQUIPPED status)
      if (isEquipped) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
              Brush.verticalGradient(
                listOf(
                  Color(0xFF00E5FF).copy(alpha = 0.30f),
                  Color(0xFF0091EA).copy(alpha = 0.20f),
                  Color(0xFF001E3D).copy(alpha = 0.40f)
                )
              )
            )
            .border(
              1.2.dp,
              Brush.linearGradient(
                listOf(
                  Color.White.copy(alpha = 0.7f),
                  Color(0xFF00E5FF),
                  Color.White.copy(alpha = 0.3f)
                )
              ),
              RoundedCornerShape(12.dp)
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "✓ EQUIPPED",
            color = Color(0xFF00E5FF),
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.8.sp
          )
        }
      } else {
        Button(
          onClick = onBuyClick,
          modifier = Modifier
            .fillMaxWidth()
            .height(38.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues()
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  listOf(
                    Color(0xFFFFEA79),
                    Color(0xFFFFD700),
                    Color(0xFFFF9100)
                  )
                ),
                RoundedCornerShape(12.dp)
              )
              .border(
                1.2.dp,
                Brush.linearGradient(
                  listOf(
                    Color.White.copy(alpha = 0.85f),
                    Color(0xFFFFD700),
                    Color(0xFFFFA000)
                  )
                ),
                RoundedCornerShape(12.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            // Top specular gloss highlight
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.TopCenter)
                .background(
                  Brush.horizontalGradient(
                    listOf(
                      Color.Transparent,
                      Color.White.copy(alpha = 0.85f),
                      Color.Transparent
                    )
                  )
                )
            )

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = if (isOwned) "EXTEND" else "BUY",
                color = Color(0xFF1E0A00),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp
              )
            }
          }
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// MINE INVENTORY VIEW (Owned Frames & Items)
// -----------------------------------------------------------------------------
@Composable
private fun MineInventoryView(
  ownedItems: List<OwnedItem>,
  storeItems: List<StoreItem>,
  equippedFrame: StoreItem?,
  userProfileImageUri: String?,
  onEquip: (StoreItem) -> Unit,
  onUnequip: (StoreItem) -> Unit,
  onGoShopping: () -> Unit
) {
  if (ownedItems.isEmpty()) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(32.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Icon(
          imageVector = Icons.Filled.ShoppingBag,
          contentDescription = null,
          tint = Color(0xFFFF4081),
          modifier = Modifier.size(48.dp)
        )
        Text(
          text = "Your Backpack is Empty",
          color = Color.White,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "You have not purchased any avatar frames or items yet.",
          color = Color(0xFFB39DDB),
          fontSize = 12.sp,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Button(
          onClick = onGoShopping,
          modifier = Modifier.height(42.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          shape = RoundedCornerShape(14.dp),
          contentPadding = PaddingValues()
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  listOf(Color(0xFF9C27B0), Color(0xFF6A1B9A))
                ),
                RoundedCornerShape(14.dp)
              )
              .border(
                1.dp,
                Brush.linearGradient(
                  listOf(Color.White.copy(alpha = 0.6f), Color(0xFFE040FB))
                ),
                RoundedCornerShape(14.dp)
              )
              .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
          ) {
            Text("Browse Frame Store", color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
    return
  }

  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(bottom = 96.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    items(ownedItems, key = { it.itemId }) { owned ->
      val item = storeItems.find { it.id == owned.itemId }
      if (item != null) {
        val isEquipped = if (item.category == "FRAME") equippedFrame?.id == item.id else owned.isEquipped

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1B0538))
            .border(
              1.2.dp,
              if (isEquipped) Color(0xFF00E5FF) else Color(0x33FFFFFF),
              RoundedCornerShape(20.dp)
            )
            .padding(12.dp)
        ) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Remaining time badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x44000000))
                .border(0.8.dp, if (owned.isExpired) Color(0xFFFF5252) else Color(0xFF00E5FF), RoundedCornerShape(8.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = owned.remainingTimeText,
                color = if (owned.isExpired) Color(0xFFFF8A80) else Color(0xFF80DEEA),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }

            // Preview
            if (item.category == "FRAME") {
              AvatarFrameOverlay(
                size = 62.dp,
                frame = item,
                profileImageUri = userProfileImageUri
              )
            } else {
              val catIcon = when (item.category) {
                "THEME" -> Icons.Filled.Palette
                "VEHICLE" -> Icons.Filled.DirectionsCar
                "BUBBLE" -> Icons.Filled.Sms
                else -> Icons.Filled.Star
              }
              Icon(
                imageVector = catIcon,
                contentDescription = null,
                tint = Color(0xFF00E5FF),
                modifier = Modifier.size(42.dp)
              )
            }

            Text(
              text = item.name,
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            // Equip / Unequip button with Frosted Glass styling
            if (isEquipped) {
              Button(
                onClick = { onUnequip(item) },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues()
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .background(
                      Brush.verticalGradient(
                        listOf(Color(0x55FF1744), Color(0x33B71C1C))
                      ),
                      RoundedCornerShape(10.dp)
                    )
                    .border(
                      1.dp,
                      Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.5f), Color(0xFFFF5252))
                      ),
                      RoundedCornerShape(10.dp)
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Text("✕ UNEQUIP", color = Color(0xFFFF8A80), fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
              }
            } else {
              Button(
                onClick = { onEquip(item) },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues()
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .background(
                      Brush.verticalGradient(
                        listOf(Color(0xFF80D8FF), Color(0xFF00B0FF), Color(0xFF0091EA))
                      ),
                      RoundedCornerShape(10.dp)
                    )
                    .border(
                      1.dp,
                      Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.8f), Color(0xFF00E5FF))
                      ),
                      RoundedCornerShape(10.dp)
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Text("✓ EQUIP", color = Color(0xFF001E3D), fontSize = 11.5.sp, fontWeight = FontWeight.Black)
                }
              }
            }
          }
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// OTHER CATEGORY STORE GRID (THEME, VEHICLE, BUBBLE)
// -----------------------------------------------------------------------------
@Composable
private fun OtherCategoryStoreGrid(
  categoryTitle: String,
  items: List<StoreItem>,
  ownedItems: List<OwnedItem>,
  onBuyClick: (StoreItem) -> Unit
) {
  if (items.isEmpty()) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp, vertical = 60.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(
                  Color(0x3300E5FF),
                  Color(0x0800E5FF),
                  Color.Transparent
                )
              )
            )
            .border(1.2.dp, Color(0x4400E5FF), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          val catIcon = when (categoryTitle) {
            "THEME" -> Icons.Filled.Palette
            "VEHICLE" -> Icons.Filled.DirectionsCar
            "BUBBLE" -> Icons.Filled.Sms
            else -> Icons.Filled.Star
          }
          Icon(
            imageVector = catIcon,
            contentDescription = null,
            tint = Color(0xFF00E5FF),
            modifier = Modifier.size(34.dp)
          )
        }
        Text(
          text = "No $categoryTitle in Store",
          color = Color.White.copy(alpha = 0.92f),
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.4.sp
        )
        Text(
          text = "Store is currently fresh. New items will appear once added.",
          color = Color(0xFFB388FF).copy(alpha = 0.75f),
          fontSize = 12.sp,
          textAlign = TextAlign.Center
        )
      }
    }
    return
  }

  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(bottom = 96.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    items(items, key = { it.id }) { item ->
      val isOwned = ownedItems.any { it.itemId == item.id && !it.isExpired }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF180330))
          .border(1.2.dp, Color(0xFF7C4DFF).copy(alpha = 0.7f), RoundedCornerShape(20.dp))
          .padding(12.dp)
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Duration
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0x44000000))
              .border(0.8.dp, Color(0xFF00E5FF), RoundedCornerShape(8.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(item.durationText, color = Color(0xFF80DEEA), fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }

          // Category Icon Graphic
          Box(
            modifier = Modifier
              .size(70.dp)
              .clip(CircleShape)
              .background(Color(0xFF26004D))
              .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            val catIcon = when (categoryTitle) {
              "THEME" -> Icons.Filled.Palette
              "VEHICLE" -> Icons.Filled.DirectionsCar
              "BUBBLE" -> Icons.Filled.Sms
              else -> Icons.Filled.Star
            }
            Icon(
              imageVector = catIcon,
              contentDescription = null,
              tint = Color(0xFF00E5FF),
              modifier = Modifier.size(32.dp)
            )
          }

          Text(
            text = item.name,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          // Price
          Text(
            text = "${LevelSystem.formatCoins(item.coinPrice)} Diamonds 💎",
            color = Color(0xFF00E5FF),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )

          Button(
            onClick = { onBuyClick(item) },
            modifier = Modifier
              .fillMaxWidth()
              .height(38.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues()
          ) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    listOf(
                      Color(0xFFFFEA79),
                      Color(0xFFFFD700),
                      Color(0xFFFF9100)
                    )
                  ),
                  RoundedCornerShape(12.dp)
                )
                .border(
                  1.2.dp,
                  Brush.linearGradient(
                    listOf(
                      Color.White.copy(alpha = 0.85f),
                      Color(0xFFFFD700),
                      Color(0xFFFFA000)
                    )
                  ),
                  RoundedCornerShape(12.dp)
                ),
              contentAlignment = Alignment.Center
            ) {
              // Top specular gloss highlight
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(1.dp)
                  .align(Alignment.TopCenter)
                  .background(
                    Brush.horizontalGradient(
                      listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.85f),
                        Color.Transparent
                      )
                    )
                  )
              )

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Text(
                  text = if (isOwned) "EXTEND" else "BUY",
                  color = Color(0xFF1E0A00),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.8.sp
                )
              }
            }
          }
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// DIAMOND & COIN RECHARGE VIEW
// -----------------------------------------------------------------------------
@Composable
private fun DiamondRechargeView(
  currentCoins: Long,
  onAddCoins: (Long) -> Unit
) {
  val packages = listOf(
    StoreCoinPackage(10_000L, "$0.99", "Popular"),
    StoreCoinPackage(50_000L, "$4.99", "+5% Bonus"),
    StoreCoinPackage(100_000L, "$9.99", "+10% Bonus"),
    StoreCoinPackage(250_000L, "$24.99", "+15% Bonus"),
    StoreCoinPackage(640_000L, "$59.99", "VIP Lv.7"),
    StoreCoinPackage(1_280_000L, "$99.99", "Super Whale")
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Current Wallet Banner
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .background(
          Brush.horizontalGradient(listOf(Color(0xFF26044E), Color(0xFF15002C)))
        )
        .border(1.2.dp, Color(0xFFFFD700), RoundedCornerShape(18.dp))
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = "CURRENT DIAMOND BALANCE", color = Color(0xFF80DEEA), fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Text(
            text = "💎 ${LevelSystem.formatCoins(currentCoins)} Diamonds",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
          )
        }

        // Instant Free Test Coins Button for development/testing
        Button(
          onClick = { onAddCoins(100_000L) },
          modifier = Modifier.height(38.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues()
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  listOf(Color(0xFF80D8FF), Color(0xFF00E5FF), Color(0xFF0091EA))
                ),
                RoundedCornerShape(12.dp)
              )
              .border(
                1.dp,
                Brush.linearGradient(
                  listOf(Color.White.copy(alpha = 0.8f), Color(0xFF00E5FF))
                ),
                RoundedCornerShape(12.dp)
              )
              .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
          ) {
            Text("+100K Free", color = Color(0xFF001E3D), fontSize = 11.5.sp, fontWeight = FontWeight.Black)
          }
        }
      }
    }

    Text(
      text = "SELECT RECHARGE TIER",
      color = Color(0xFFB388FF),
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp
    )

    packages.forEach { pkg ->
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFF220542), Color(0xFF14022A))
            )
          )
          .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Filled.Diamond,
              contentDescription = null,
              tint = Color(0xFF00E5FF),
              modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "${LevelSystem.formatCoins(pkg.coins)} Diamonds",
                color = Color.White,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = pkg.tag,
                color = Color(0xFFFFD700),
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }

          Button(
            onClick = { onAddCoins(pkg.coins) },
            modifier = Modifier.height(38.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues()
          ) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    listOf(Color(0xFFFF4081), Color(0xFFE91E63), Color(0xFFC2185B))
                  ),
                  RoundedCornerShape(12.dp)
                )
                .border(
                  1.dp,
                  Brush.linearGradient(
                    listOf(Color.White.copy(alpha = 0.8f), Color(0xFFFF4081))
                  ),
                  RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(pkg.priceUsd, color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.Black)
            }
          }
        }
      }
    }
  }
}

private data class StoreCoinPackage(val coins: Long, val priceUsd: String, val tag: String)

private data class CategoryItem(
  val key: String,
  val title: String,
  val icon: ImageVector,
  val color: Color
)
