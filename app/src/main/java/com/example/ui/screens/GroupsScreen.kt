package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GroupStanding
import com.example.data.WorldCupMatch
import com.example.ui.viewmodel.WorldCupViewModel

@Composable
fun GroupsScreen(
    viewModel: WorldCupViewModel,
    modifier: Modifier = Modifier
) {
    val standings by viewModel.standings.collectAsState()
    val matches by viewModel.matches.collectAsState()

    var activeGroupSegment by remember { mutableStateOf("A-D") } // "A-D", "E-H", "I-L"

    val groupScope = remember(activeGroupSegment) {
        when (activeGroupSegment) {
            "A-D" -> ('A'..'D').map { it.toString() }
            "E-H" -> ('E'..'H').map { it.toString() }
            else -> ('I'..'L').map { it.toString() }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Multi Segment Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Grup Durumları",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Segmented Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("A-D", "E-H", "I-L").forEach { segment ->
                    val isSelected = activeGroupSegment == segment
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { activeGroupSegment = segment }
                            .padding(vertical = 8.dp)
                            .testTag("segment_$segment"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Gruplar $segment",
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // List containing the tables and expandable fixtures
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(groupScope) { groupName ->
                val groupStandings = standings[groupName] ?: emptyList()
                val groupMatches = matches.filter { it.group == groupName }

                GroupTableCard(
                    groupName = groupName,
                    standings = groupStandings,
                    matches = groupMatches,
                    onSimulateMatch = { viewModel.simulateMatch(it.id) }
                )
            }
        }
    }
}

@Composable
fun GroupTableCard(
    groupName: String,
    standings: List<GroupStanding>,
    matches: List<WorldCupMatch>,
    onSimulateMatch: (WorldCupMatch) -> Unit
) {
    var isFixturesExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("group_card_$groupName"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Group Title Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "D GRUBU".replace("D", groupName), // Dynamic replace
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )

                TextButton(
                    onClick = { isFixturesExpanded = !isFixturesExpanded },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = if (isFixturesExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isFixturesExpanded) "Fikstürü Gizle" else "Fikstürü Göster",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Table Columns Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "# Takım",
                    modifier = Modifier.weight(3f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "O",
                    modifier = Modifier.weight(0.7f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "G",
                    modifier = Modifier.weight(0.7f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "B",
                    modifier = Modifier.weight(0.7f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "M",
                    modifier = Modifier.weight(0.7f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Av",
                    modifier = Modifier.weight(0.8f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "P",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Table Rows
            standings.forEachIndexed { index, row ->
                val isQualifying = index < 2 // Top 2 qualify
                val rowBgColor = if (isQualifying) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)
                } else {
                    Color.Transparent
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(rowBgColor, RoundedCornerShape(8.dp))
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Position and Flag and Name
                    Row(
                        modifier = Modifier.weight(3f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Position circle indicator
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isQualifying) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = if (isQualifying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(text = row.flag, fontSize = 16.sp)

                        Text(
                            text = row.teamName,
                            fontWeight = if (isQualifying) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Stat counts
                    Text(
                        text = "${row.played}",
                        modifier = Modifier.weight(0.7f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${row.won}",
                        modifier = Modifier.weight(0.7f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${row.drawn}",
                        modifier = Modifier.weight(0.7f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${row.lost}",
                        modifier = Modifier.weight(0.7f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )

                    // Goal Difference
                    val gdSign = if (row.goalsDifference > 0) "+${row.goalsDifference}" else "${row.goalsDifference}"
                    val gdColor = if (row.goalsDifference > 0) Color(0xFF2E7D32)
                    else if (row.goalsDifference < 0) Color(0xFFC62828)
                    else MaterialTheme.colorScheme.onSurfaceVariant

                    Text(
                        text = gdSign,
                        modifier = Modifier.weight(0.8f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = gdColor
                    )

                    // Points highlight
                    Text(
                        text = "${row.points}",
                        modifier = Modifier.weight(1f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isQualifying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }

                if (index < standings.lastIndex) {
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
                }
            }

            // Expandable Fixtures Section
            AnimatedVisibility(
                visible = isFixturesExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Grup Fikstürü ve Sonuçlar",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    matches.forEach { match ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Teams & Score
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "${match.homeTeamId} vs ${match.awayTeamId}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.weight(1f))
                                if (match.isPlayed) {
                                    Text(
                                        text = "${match.homeScore} - ${match.awayScore}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                }
                            }

                            // Run Match Button
                            if (!match.isPlayed) {
                                IconButton(
                                    onClick = { onSimulateMatch(match) },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp))
                                        .testTag("simulate_match_inline_${match.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Simüle Et",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.SportsScore,
                                    contentDescription = "Oynandı",
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
