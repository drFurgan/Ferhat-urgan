package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Team
import com.example.ui.viewmodel.WorldCupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(
    viewModel: WorldCupViewModel,
    modifier: Modifier = Modifier,
    onNavigateToChat: () -> Unit = {}
) {
    val teams by viewModel.teams.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedGroupFilter by remember { mutableStateOf("TÜMÜ") } // "TÜMÜ", "A" to "L", "FAVORİLER"

    var selectedTeamForDetails by remember { mutableStateOf<Team?>(null) }

    val filteredTeams = remember(teams, searchQuery, selectedGroupFilter, favorites) {
        teams.filter { team ->
            val matchesSearch = team.name.contains(searchQuery, ignoreCase = true) ||
                    team.code.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedGroupFilter) {
                "TÜMÜ" -> true
                "FAVORİLER" -> favorites.contains(team.id)
                else -> team.group == selectedGroupFilter
            }
            matchesSearch && matchesFilter
        }.sortedBy { it.name }
    }

    val groupsList = remember {
        listOf("TÜMÜ", "FAVORİLER") + ('A'..'L').map { it.toString() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search and Stats Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Katılımcı Ülkeler",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Ülke veya kod ara...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_teams_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                singleLine = true
            )

            // Scrollable filter chips
            ScrollableTabRow(
                selectedTabIndex = groupsList.indexOf(selectedGroupFilter).coerceAtLeast(0),
                edgePadding = 0.dp,
                divider = {},
                indicator = {},
                containerColor = Color.Transparent,
                modifier = Modifier.fillMaxWidth()
            ) {
                groupsList.forEach { filterItem ->
                    val isSelected = filterItem == selectedGroupFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedGroupFilter = filterItem },
                        label = {
                            Text(
                                text = if (filterItem.length == 1) "Grup $filterItem" else filterItem,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("filter_chip_$filterItem")
                    )
                }
            }
        }

        // Teams Grid Layout
        if (filteredTeams.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Aramanızla eşleşen takım bulunamadı.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredTeams, key = { it.id }) { team ->
                    val isFav = favorites.contains(team.id)
                    TeamCard(
                        team = team,
                        isFavorite = isFav,
                        onToggleFavorite = { viewModel.toggleFavorite(team.id) },
                        onClick = { selectedTeamForDetails = team }
                    )
                }
            }
        }
    }

    val selectedSkinTeam by viewModel.selectedSkinTeam.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Details dialog
    selectedTeamForDetails?.let { team ->
        TeamDetailsDialog(
            team = team,
            isFavorite = favorites.contains(team.id),
            isSkinActive = selectedSkinTeam?.id == team.id,
            onToggleFavorite = { viewModel.toggleFavorite(team.id) },
            onToggleSkin = {
                if (selectedSkinTeam?.id == team.id) {
                    viewModel.selectSkinTeam(null, context)
                } else {
                    viewModel.selectSkinTeam(team, context)
                }
            },
            onDismiss = { selectedTeamForDetails = null },
            onAskAi = {
                viewModel.askAiAboutTeam(team.name)
                selectedTeamForDetails = null
                onNavigateToChat()
            }
        )
    }
}

@Composable
fun TeamCard(
    team: Team,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .testTag("team_card_${team.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Accent Color Stripe at the left side
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(Color(android.graphics.Color.parseColor(team.primaryColor)))
                    .align(Alignment.CenterStart)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = team.flag,
                        fontSize = 28.sp
                    )

                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favoriye Ekle",
                            tint = if (isFavorite) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = team.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Grup ${team.group} • ${team.code}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )

                        // Strength Rating Pill
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ) {
                            Text(
                                text = "OVR ${team.rating}",
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamDetailsDialog(
    team: Team,
    isFavorite: Boolean,
    isSkinActive: Boolean,
    onToggleFavorite: () -> Unit,
    onToggleSkin: () -> Unit,
    onAskAi: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with Flag and Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = team.flag,
                        fontSize = 44.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = team.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "FIFA Kod: ${team.code} | Grup ${team.group}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isFavorite) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

                // Coach & Jersey Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Teknik Direktör",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = team.tactician,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Takım Renkleri",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(team.primaryColor)))
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(team.secondaryColor)))
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), CircleShape)
                            )
                        }
                    }
                }

                // Roster Squad Area
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Öne Çıkan Yıldızlar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    team.starPlayers.forEach { player ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Position Badge
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when(player.position) {
                                            "GK" -> Color(0xFFE65100).copy(alpha = 0.15f)
                                            "DEF" -> Color(0xFF1B5E20).copy(alpha = 0.15f)
                                            "MID" -> Color(0xFF0D47A1).copy(alpha = 0.15f)
                                            else -> Color(0xFFB71C1C).copy(alpha = 0.15f)
                                        }
                                    ) {
                                        Text(
                                            text = player.position,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Black,
                                            color = when(player.position) {
                                                "GK" -> Color(0xFFE65100)
                                                "DEF" -> Color(0xFF1B5E20)
                                                "MID" -> Color(0xFF0D47A1)
                                                else -> Color(0xFFB71C1C)
                                            },
                                            fontSize = 9.sp
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = player.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = player.club,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = "${player.rating}",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onToggleSkin,
                    modifier = Modifier.fillMaxWidth().testTag("toggle_skin_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSkinActive) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondary,
                        contentColor = if (isSkinActive) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Icon(
                        imageVector = if (isSkinActive) Icons.Default.Refresh else Icons.Default.ColorLens,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSkinActive) "Skin Rengini Varsayılana Sıfırla" else "Bu Ülkeyi Aktif Tema Yap 🎨",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Kapat")
                    }

                    Button(
                        onClick = onAskAi,
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("ai_analyze_dialog_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Yapay Zeka Analizi", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
