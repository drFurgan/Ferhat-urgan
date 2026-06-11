package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.ChatUiState
import com.example.ui.viewmodel.WorldCupViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    viewModel: WorldCupViewModel,
    modifier: Modifier = Modifier
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val chatUiState by viewModel.chatUiState.collectAsState()
    val generalChat by viewModel.generalChat.collectAsState()

    var isAiModeActive by remember { mutableStateOf(true) }
    var aiTextInput by remember { mutableStateOf("") }
    var generalTextInput by remember { mutableStateOf("") }

    val aiListState = rememberLazyListState()
    val generalListState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val suggestionChips = listOf(
        "Türkiye gruptan nasıl çıkar?",
        "2026 Kupasını kim kazanır?",
        "Hakan Çalhanoğlu & Arda Güler analizi",
        "Arjantin kadro derinliği nasıl?"
    )

    // Scroll to bottom when AI messages list grows
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty() && isAiModeActive) {
            coroutineScope.launch {
                aiListState.animateScrollToItem(chatMessages.size - 1)
            }
        }
    }

    // Scroll to bottom when General messages list grows
    LaunchedEffect(generalChat.size) {
        if (generalChat.isNotEmpty() && !isAiModeActive) {
            coroutineScope.launch {
                generalListState.animateScrollToItem(generalChat.size - 1)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // AI & Chat Header Row
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    )
                )
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isAiModeActive) Icons.Default.AutoAwesome else Icons.Default.SportsSoccer,
                        contentDescription = "Chat Logo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = if (isAiModeActive) "Dünya Kupası Yapay Zekası" else "Futbol Severler Forumu",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color.Green, CircleShape)
                        )
                        Text(
                            text = if (isAiModeActive) "Gemini • Çevrimiçi Analist" else "12,482 Taraftar Online",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Tab Row switcher
        TabRow(
            selectedTabIndex = if (isAiModeActive) 0 else 1,
            containerColor = Color.Transparent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            divider = {}
        ) {
            Tab(
                selected = isAiModeActive,
                onClick = { isAiModeActive = true },
                text = { Text("Yapay Zeka Asistanı 🤖", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = !isAiModeActive,
                onClick = { isAiModeActive = false },
                text = { Text("Genel Sohbet Odası 💬", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (isAiModeActive) {
            // Yapay Zeka AI Mode screen
            LazyColumn(
                state = aiListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(chatMessages) { chatPair ->
                    val (msg, isUser) = chatPair
                    MessageBubble(message = msg, isUser = isUser)
                }

                // Typing overlay loader
                if (chatUiState is ChatUiState.Loading) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(12.dp),
                                        strokeWidth = 1.5.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Yapay Zeka rasyonel futbol analizlerini hazırlıyor...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Suggestion Chips list
            AnimatedVisibility(
                visible = chatMessages.size == 1 && chatUiState !is ChatUiState.Loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Hızlı Soru Önerileri",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        suggestionChips.take(2).forEach { chipText ->
                            SuggestionCard(chipText = chipText, onClick = {
                                viewModel.sendChatMessage(chipText)
                            })
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        suggestionChips.takeLast(2).forEach { chipText ->
                            SuggestionCard(chipText = chipText, onClick = {
                                viewModel.sendChatMessage(chipText)
                            })
                        }
                    }
                }
            }

            // Bottom Input Field bar for AI Mode
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = aiTextInput,
                        onValueChange = { aiTextInput = it },
                        placeholder = { Text("AI Asistanına sorun...", fontSize = 14.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_text_field"),
                        shape = RoundedCornerShape(24.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (aiTextInput.isNotBlank() && chatUiState !is ChatUiState.Loading) {
                                    viewModel.sendChatMessage(aiTextInput)
                                    aiTextInput = ""
                                    focusManager.clearFocus()
                                }
                            }
                        ),
                        maxLines = 3,
                        singleLine = false
                    )

                    // Send button
                    IconButton(
                        onClick = {
                            if (aiTextInput.isNotBlank() && chatUiState !is ChatUiState.Loading) {
                                viewModel.sendChatMessage(aiTextInput)
                                aiTextInput = ""
                                focusManager.clearFocus()
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (aiTextInput.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                            .testTag("chat_send_button"),
                        enabled = aiTextInput.isNotBlank() && chatUiState !is ChatUiState.Loading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Gönder",
                            tint = if (aiTextInput.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        } else {
            // Genel Taraftarlar Sohbet Odası Mode Screen
            LazyColumn(
                state = generalListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
            ) {
                items(generalChat) { chatPair ->
                    val (sender, msg) = chatPair
                    val isUser = sender.contains("Kullanıcı")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        if (!isUser) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(32.dp)
                                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sender.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Surface(
                            shape = if (isUser) RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp) else RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp),
                            color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = if (!isUser) BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)) else null,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                                if (!isUser) {
                                    Text(
                                        text = sender,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(bottom = 2.dp),
                                        fontSize = 10.sp
                                    )
                                }
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Input Field bar for General Chat mode
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = generalTextInput,
                        onValueChange = { generalTextInput = it },
                        placeholder = { Text("Milyonlarca taraftarla sohbet et...", fontSize = 14.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("general_chat_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (generalTextInput.isNotBlank()) {
                                    viewModel.sendGeneralChatMessage("Kullanıcı (Siz)", generalTextInput)
                                    generalTextInput = ""
                                    focusManager.clearFocus()
                                }
                            }
                        ),
                        maxLines = 3,
                        singleLine = false
                    )

                    // Send button
                    IconButton(
                        onClick = {
                            if (generalTextInput.isNotBlank()) {
                                viewModel.sendGeneralChatMessage("Kullanıcı (Siz)", generalTextInput)
                                generalTextInput = ""
                                focusManager.clearFocus()
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (generalTextInput.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                            .testTag("general_chat_send_button"),
                        enabled = generalTextInput.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Gönder",
                            tint = if (generalTextInput.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: String, isUser: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsSoccer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Surface(
            shape = if (isUser) {
                RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
            } else {
                RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
            },
            color = if (isUser) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            },
            border = if (!isUser) {
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
            } else null,
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    if (isUser) RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
                    else RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
                )
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RowScope.SuggestionCard(chipText: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .heightIn(min = 52.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = chipText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                lineHeight = 12.sp,
                maxLines = 2
            )
        }
    }
}
