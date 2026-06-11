package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Team
import com.example.ui.viewmodel.WorldCupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: WorldCupViewModel,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val teams by viewModel.teams.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedTeam by remember { mutableStateOf<Team?>(null) }

    // Preselect Turkey if found
    LaunchedEffect(teams) {
        if (selectedTeam == null) {
            selectedTeam = teams.find { it.id == "TUR" } ?: teams.firstOrNull()
        }
    }

    val filteredTeams = remember(teams, searchQuery) {
        if (searchQuery.isBlank()) {
            teams
        } else {
            teams.filter { 
                it.name.contains(searchQuery, ignoreCase = true) || 
                it.code.contains(searchQuery, ignoreCase = true) 
            }
        }.sortedBy { it.name }
    }

    // Popular choices for quick action
    val popularTeamIds = listOf("TUR", "ARG", "BRA", "GER", "ENG", "ESP")
    val popularTeams = remember(teams) {
        teams.filter { it.id in popularTeamIds }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1B2A),
                        Color(0xFF1B263B)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(44.dp))

            // Brand Header logo
            Icon(
                imageVector = Icons.Default.SportsSoccer,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier
                    .size(54.dp)
                    .background(Color.White.copy(alpha = 0.12f), CircleShape)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "TARAFTAR DÜNYASI",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp
            )

            Text(
                text = "Hoş Geldiniz! Lütfen ülkenizi seçin. Uygulama seçtiğiniz ülkenin renkleri ve bayrağıyla özelleştirilecektir.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Popular teams shortcut block
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Hızlı Seçim ✨",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    popularTeams.forEach { team ->
                        val isChosen = selectedTeam?.id == team.id
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isChosen) Color(0xFFFFD700).copy(alpha = 0.25f)
                                    else Color.White.copy(alpha = 0.08f)
                                )
                                .border(
                                    border = BorderStroke(
                                        width = if (isChosen) 2.dp else 1.dp,
                                        color = if (isChosen) Color(0xFFFFD700) else Color.White.copy(alpha = 0.15f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedTeam = team }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(team.flag, fontSize = 24.sp)
                                Text(
                                    text = team.name.take(5),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // General Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("48 ülkeden birini arayın...", color = Color.White.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null, tint = Color.White)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_search_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.White.copy(alpha = 0.05f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                    focusedBorderColor = Color(0xFFFFD700),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Grid list of all matchable countries
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredTeams, key = { it.id }) { team ->
                    val isChosen = selectedTeam?.id == team.id
                    Card(
                        onClick = { selectedTeam = team },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isChosen) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            width = if (isChosen) 2.dp else 1.dp,
                            color = if (isChosen) Color(0xFFFFD700) else Color.White.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboard_team_${team.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = team.flag,
                                fontSize = 32.sp
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = team.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Grup ${team.group}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                            
                            // Colors indicators
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(team.primaryColor)))
                                )
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(team.secondaryColor)))
                                )
                            }
                        }
                    }
                }
            }

            // Sticky Bottom selection summary & action button
            selectedTeam?.let { team ->
                Surface(
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(team.flag, fontSize = 28.sp)
                            Column {
                                Text(
                                    text = "${team.name} Seçildi",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tema renkleri: ${team.primaryColor} ve ${team.secondaryColor}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.selectUserCountry(team, context)
                                onComplete()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFD700),
                                contentColor = Color(0xFF0F1D36)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("confirm_onboarding_btn")
                        ) {
                            Text(
                                text = "KAYDET ⚽",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
