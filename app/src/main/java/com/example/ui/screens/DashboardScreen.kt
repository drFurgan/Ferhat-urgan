package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import com.example.data.WorldCupMatch
import com.example.ui.viewmodel.WorldCupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: WorldCupViewModel,
    modifier: Modifier = Modifier,
    onNavigateToChat: () -> Unit = {}
) {
    val matches by viewModel.matches.collectAsState()
    val teams by viewModel.teams.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    var showOnlyTodayMatches by remember { mutableStateOf(false) }
    var selectedMatchForDetails by remember { mutableStateOf<WorldCupMatch?>(null) }
    var showLiveStreamPlayerWithMatch by remember { mutableStateOf<WorldCupMatch?>(null) }

    // Filter matches based on user's selected menu
    val filteredMatches = remember(matches, showOnlyTodayMatches) {
        if (showOnlyTodayMatches) {
            matches.filter { it.date.contains("Bugün") || it.date.contains("Yarın") }
        } else {
            matches
        }
    }

    val liveMatches = remember(filteredMatches) {
        filteredMatches.filter { !it.isPlayed }
    }
    val playedMatches = remember(filteredMatches) {
        filteredMatches.filter { it.isPlayed }.reversed()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Hoş geldiniz, kafkasbilgisayar@gmail.com ⚽",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsFootball,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "FIFA WORLD CUP 2026",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White.copy(alpha = 0.85f),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "TURNUVA BAŞLADI!",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            lineHeight = 36.sp
                        )

                        Text(
                            text = "11 Haziran - 19 Temmuz 2026 | ABD • Meksika • Kanada",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Opening Countdown Indicator / Status & Share CTA
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.clip(RoundedCornerShape(12.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(Color.Green, CircleShape)
                                    )
                                    Text(
                                        text = "CANLI VERİ AKTİF",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Share Action Button
                            val shareContext = androidx.compose.ui.platform.LocalContext.current
                            Button(
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
                                    shareContext.startActivity(shareIntent)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFD700),
                                    contentColor = Color(0xFF0F1D36)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("banner_share_app_btn")
                            ) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "PAYLAŞ 🚀",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // MENU SELECTOR CHIPS - Today's Matches Toggle
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Maç Takvimi Menüleri",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = !showOnlyTodayMatches,
                        onClick = { showOnlyTodayMatches = false },
                        label = { Text("Tüm Fikstür 🗓️") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )

                    FilterChip(
                        selected = showOnlyTodayMatches,
                        onClick = { showOnlyTodayMatches = true },
                        label = { Text("Günün Maçları 🔥") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Fast Simulator Section (Only visible under all matches to prevent clutter)
        if (!showOnlyTodayMatches) {
            item {
                Text(
                    text = "Turnuva Simülatörü",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Tüm grup maçlarını gerçekçi takımların güç dengelerine (Poisson algoritması) göre anında simüle edin ve puan durumlarını canlı güncelleyin!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { viewModel.simulateAllMatches() },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("simulate_all_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tüm Kupayı Simüle Et", fontSize = 13.sp, maxLines = 1)
                            }

                            OutlinedButton(
                                onClick = { viewModel.resetAndSeed() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("reset_kupa_btn"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sıfırla", fontSize = 13.sp, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }

        // Quick Actions with AI
        item {
            Card(
                onClick = onNavigateToChat,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_assist_banner"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Yapay Zeka Tahmin Robotu",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Hangi takım şampiyon olur? Türkiye gruptan çıkar mı? Kadro analizleri için tıkla sor!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Upcoming Matches Header
        if (liveMatches.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showOnlyTodayMatches) "Günün Yaklaşan Maçları" else "Sıradaki Maçlar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${liveMatches.size} Maç",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            items(liveMatches) { match ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedMatchForDetails = match },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                ) {
                    MatchCardContent(match = match, viewModel = viewModel, onSimulate = { viewModel.simulateMatch(match.id) })
                }
            }
        }

        // Finished Matches Header
        if (playedMatches.isNotEmpty()) {
            item {
                Text(
                    text = if (showOnlyTodayMatches) "Oynanan Günün Maçları" else "Oynanan Son Maçlar",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(playedMatches) { match ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedMatchForDetails = match },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                ) {
                    MatchCardContent(match = match, viewModel = viewModel, onSimulate = {}, isPlayed = true)
                }
            }
        }
    }

    // Match Detail Dialog featuring In-Match Chat and AI Commentaries
    selectedMatchForDetails?.let { match ->
        val homeTeam = teams.find { it.id == match.homeTeamId } ?: return@let
        val awayTeam = teams.find { it.id == match.awayTeamId } ?: return@let
        val matchChats by viewModel.matchChats.collectAsState()
        val chatMessages = matchChats[match.id] ?: emptyList()

        var userCommentText by remember { mutableStateOf("") }
        var aiCommentaryText by remember { mutableStateOf<String?>(null) }
        var isAiAnalyzing by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        AlertDialog(
            onDismissRequest = { selectedMatchForDetails = null },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            confirmButton = {
                TextButton(onClick = { selectedMatchForDetails = null }) {
                    Text("Kapat")
                }
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Maç Merkezi & Canlı Sohbet",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (match.isPlayed) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                    ) {
                        Text(
                            text = if (match.isPlayed) "BİTTİ" else "SİMÜLE ET",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (match.isPlayed) Color(0xFF2E7D32) else Color(0xFFE65100),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 480.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Match representation core
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Grup ${match.group} • ${match.date}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Home
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(homeTeam.flag, fontSize = 42.sp)
                                        Text(homeTeam.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                        Text("Puan: ${homeTeam.rating}", style = MaterialTheme.typography.labelSmall)
                                    }

                                    // Score or Sim button
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        if (match.isPlayed) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                Text("${match.homeScore}", fontSize = 36.sp, fontWeight = FontWeight.Black)
                                                Text("-", fontSize = 36.sp, fontWeight = FontWeight.Bold)
                                                Text("${match.awayScore}", fontSize = 36.sp, fontWeight = FontWeight.Black)
                                            }
                                        } else {
                                            Button(
                                                onClick = {
                                                    viewModel.simulateMatch(match.id)
                                                    // Quick reset details match state to show result immediately
                                                    selectedMatchForDetails = matches.find { it.id == match.id }
                                                },
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text("Simüle Et")
                                            }
                                        }
                                    }

                                    // Away
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(awayTeam.flag, fontSize = 42.sp)
                                        Text(awayTeam.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                        Text("Puan: ${awayTeam.rating}", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }

                    // Live stream options if not played yet
                    if (!match.isPlayed) {
                        item {
                            Button(
                                onClick = { 
                                    showLiveStreamPlayerWithMatch = match 
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("match_detail_live_stream_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LiveTv,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CANLI YAYINI İZLE (ŞİFRESİZ) 📺",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    // Yapay Zeka AI match commentary block
                    item {
                        Card(
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    Text("Yapay Zeka Canlı Anlatımı 🎙️", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                                }

                                if (aiCommentaryText == null) {
                                    Button(
                                        onClick = {
                                            isAiAnalyzing = true
                                            scope.launch {
                                                val scoreSuffix = if (match.isPlayed) "Maç bitti. Skor: ${match.homeScore}-${match.awayScore}." else "Maç henüz oynanmadı."
                                                val p = "${homeTeam.name} (${homeTeam.flag}) vs ${awayTeam.name} (${awayTeam.flag}) maçının canlı anlatımını dakika dakika önemli pozisyonlarla, heyecanlı bir radyo spikeri sunumuyla Türkçe oluştur. $scoreSuffix"
                                                val answer = com.example.data.GeminiService.askAssistant(p)
                                                aiCommentaryText = answer
                                                isAiAnalyzing = false
                                            }
                                        },
                                        enabled = !isAiAnalyzing,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        if (isAiAnalyzing) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("AI Analiz Ediyor...")
                                        } else {
                                            Text("Yapay Zekadan Canlı Anlatım Al ⚡")
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = aiCommentaryText ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Live In-match chat section
                    item {
                        Text(
                            text = "Maç İçi Canlı Sohbet Odası 💬",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Messages box
                    if (chatMessages.isEmpty()) {
                        item {
                            Text("Sohbet odası boş. İlk yazan taraftar sen ol!", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    } else {
                        items(chatMessages) { chatPair ->
                            val isUser = chatPair.first.contains("Kullanıcı")
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    shape = RoundedCornerShape(
                                        topStart = 12.dp,
                                        topEnd = 12.dp,
                                        bottomStart = if (isUser) 12.dp else 0.dp,
                                        bottomEnd = if (isUser) 0.dp else 12.dp
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                                        Text(
                                            text = chatPair.first,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Black,
                                            color = if (isUser) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary,
                                            fontSize = 9.sp
                                        )
                                        Text(
                                            text = chatPair.second,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // User chat entry form
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = userCommentText,
                                onValueChange = { userCommentText = it },
                                placeholder = { Text("Fikrini paylaş...", fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                              )
                            IconButton(
                                onClick = {
                                    if (userCommentText.isNotBlank()) {
                                        viewModel.sendMatchChatMessage(match.id, "Kullanıcı (Siz)", userCommentText)
                                        userCommentText = ""
                                    }
                                },
                                modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Gönder", tint = Color.White)
                            }
                        }
                    }
                }
            }
        )
    }

    showLiveStreamPlayerWithMatch?.let { match ->
        LiveStreamPlayerDialog(
            match = match,
            onDismiss = { showLiveStreamPlayerWithMatch = null }
        )
    }
}

@Composable
fun MatchCardContent(
    match: WorldCupMatch,
    viewModel: WorldCupViewModel,
    onSimulate: () -> Unit,
    isPlayed: Boolean = false
) {
    val teams by viewModel.teams.collectAsState()
    val homeTeam = remember(match.homeTeamId) { teams.find { it.id == match.homeTeamId } }
    val awayTeam = remember(match.awayTeamId) { teams.find { it.id == match.awayTeamId } }

    if (homeTeam == null || awayTeam == null) return

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Match meta
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Grup ${match.group} • ${match.date}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold
            )

            val isTodayActive = remember(match) { !isPlayed && match.date.contains("Bugün") }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isPlayed) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else if (isTodayActive) {
                    Color(0xFFE53935)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                modifier = Modifier.clip(RoundedCornerShape(6.dp))
            ) {
                Text(
                    text = if (isPlayed) "BİTTİ" else if (isTodayActive) "CANLI YAYIN 📺" else "YAKINDA",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isPlayed) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else if (isTodayActive) {
                        Color.White
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Score area
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Home Team
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = homeTeam.flag,
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = homeTeam.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Text(
                    text = homeTeam.code,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            // Match Score / VS
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.widthIn(min = 100.dp)
            ) {
                if (isPlayed) {
                    Text(
                        text = "${match.homeScore}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Right
                    )
                    Text(
                        text = "-",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${match.awayScore}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Left
                    )
                } else {
                    Button(
                        onClick = onSimulate,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("sim_button_${match.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text("Simüle", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Away Team
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = awayTeam.flag,
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = awayTeam.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Text(
                    text = awayTeam.code,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Info prompt to open live center
        Text("Maç merkezi, canlı sohbet ve AI Anlatım için tıkla 📍", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(12.dp))

        // Stadium Info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = match.stadium,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                maxLines = 1
            )
        }
    }
}
