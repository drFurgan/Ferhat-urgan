package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.viewmodel.WorldCupViewModel

enum class WorldCupTab(val title: String, val icon: ImageVector, val tag: String) {
    DASHBOARD("Ana Sayfa", Icons.Default.Home, "tab_dashboard"),
    TEAMS("Ülkeler", Icons.Default.SportsSoccer, "tab_teams"),
    GROUPS("Gruplar", Icons.Default.FormatListNumbered, "tab_groups"),
    STATS_API("Stats REST", Icons.Default.CloudQueue, "tab_stats"),
    AI_CHAT("AI Asistan", Icons.Default.AutoAwesome, "tab_chat")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldCupApp(
    viewModel: WorldCupViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(WorldCupTab.DASHBOARD) }
    val isOnboarded by viewModel.isOnboarded.collectAsState()
    val selectedSkinTeam by viewModel.selectedSkinTeam.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    if (!isOnboarded) {
        OnboardingScreen(
            viewModel = viewModel,
            onComplete = {}
        )
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = when (activeTab) {
                                WorldCupTab.DASHBOARD -> {
                                    val flag = selectedSkinTeam?.flag ?: ""
                                    if (flag.isNotEmpty()) "WordCup26 $flag" else "WordCup26"
                                }
                                WorldCupTab.TEAMS -> "Katılımcı Takımlar"
                                WorldCupTab.GROUPS -> "Grup Puan Durumları"
                                WorldCupTab.STATS_API -> "Stats Rest API"
                                WorldCupTab.AI_CHAT -> "Dünya Kupası AI Tahmincisi"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                            letterSpacing = if (activeTab == WorldCupTab.DASHBOARD) 1.sp else 0.sp
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    actions = {
                        IconButton(
                            onClick = {
                                val sendIntent = android.content.Intent().apply {
                                    action = android.content.Intent.ACTION_SEND
                                    putExtra(
                                        android.content.Intent.EXTRA_TEXT,
                                        "⚽ Taraftar Dünyası & Dünya Kupası '26 uygulamasına katıl! Seçtiğin takımın renkleriyle canlı maç sonuçlarını takip et, yapay zeka ile maçları tahmin et ve şifresiz canlı maç yayını izle!\n\nUygulamayı hemen keşfet: https://ais-pre-sm26idcm56wmcffad22uja-915340588803.europe-west2.run.app"
                                    )
                                    type = "text/plain"
                                }
                                val shareIntent = android.content.Intent.createChooser(sendIntent, "Uygulamayı Paylaş")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier.testTag("app_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Uygulamayı Paylaş",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (activeTab == WorldCupTab.DASHBOARD) {
                            IconButton(onClick = { viewModel.resetAndSeed() }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Sıfırla",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                )
            },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                WorldCupTab.values().forEach { tab ->
                    val isSelected = activeTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { activeTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.testTag("icon_${tab.tag}")
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        },
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Smooth Crossfade screen switcher
            Crossfade(
                targetState = activeTab,
                animationSpec = tween(durationMillis = 250),
                modifier = Modifier.fillMaxSize()
            ) { targetTab ->
                when (targetTab) {
                    WorldCupTab.DASHBOARD -> {
                        DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToChat = { activeTab = WorldCupTab.AI_CHAT }
                        )
                    }
                    WorldCupTab.TEAMS -> {
                        TeamsScreen(
                            viewModel = viewModel,
                            onNavigateToChat = { activeTab = WorldCupTab.AI_CHAT }
                        )
                    }
                    WorldCupTab.GROUPS -> {
                        GroupsScreen(
                            viewModel = viewModel
                        )
                    }
                    WorldCupTab.STATS_API -> {
                        StatsApiScreen(
                            viewModel = viewModel
                        )
                    }
                    WorldCupTab.AI_CHAT -> {
                        AiChatScreen(
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
    }
}
