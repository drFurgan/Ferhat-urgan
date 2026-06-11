package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.ApiConnectionStatus
import com.example.ui.viewmodel.WorldCupViewModel

@Composable
fun StatsApiScreen(
    viewModel: WorldCupViewModel,
    modifier: Modifier = Modifier
) {
    val apiUrl by viewModel.apiUrl.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()
    val apiStatus by viewModel.apiStatus.collectAsState()

    var showHelpInfo by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Title
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Gerçek Veri Stats API",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Uygulamayı kendi profesyonel futbol API servislerinize (örneğin Sportmonks, FootApi veya Api-Football) bağlayın.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Configuration Form Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("api_config_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "API Erişim Ayarları",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Base URL Input
                    OutlinedTextField(
                        value = apiUrl,
                        onValueChange = { viewModel.updateApiUrl(it) },
                        label = { Text("Stats API Base URL", fontSize = 12.sp) },
                        placeholder = { Text("https://api.footballstats.com/v3") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("stats_api_url_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Auth Key Input
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { viewModel.updateApiKey(it) },
                        label = { Text("Stats API Key / Bearer Token", fontSize = 12.sp) },
                        placeholder = { Text("Bearer ...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("stats_api_key_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.testStatsApiConnection() },
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("test_api_connection_btn"),
                            shape = RoundedCornerShape(10.dp),
                            enabled = apiStatus !is ApiConnectionStatus.Testing
                        ) {
                            if (apiStatus is ApiConnectionStatus.Testing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Bağlanıyor...", fontSize = 13.sp)
                            } else {
                                Icon(Icons.Default.CloudSync, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Bağlantıyı Test Et", fontSize = 13.sp)
                            }
                        }

                        // Help toggle
                        OutlinedButton(
                            onClick = { showHelpInfo = !showHelpInfo },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = if (showHelpInfo) Icons.Default.ExpandLess else Icons.Default.Info, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Rehber", fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Live connection console / output logs
        item {
            AnimatedVisibility(
                visible = apiStatus !is ApiConnectionStatus.Idle,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sistem Bağlantı Konsolu",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Status bullet
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (apiStatus) {
                                    is ApiConnectionStatus.Success -> Color(0xFFE8F5E9)
                                    is ApiConnectionStatus.Error -> Color(0xFFFFEBEE)
                                    else -> Color.Transparent
                                },
                                modifier = Modifier.clip(RoundedCornerShape(6.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                when (apiStatus) {
                                                    is ApiConnectionStatus.Success -> Color(0xFF2E7D32)
                                                    is ApiConnectionStatus.Error -> Color(0xFFC62828)
                                                    else -> Color.Gray
                                                }, CircleShape
                                            )
                                    )
                                    Text(
                                        text = when(apiStatus) {
                                            is ApiConnectionStatus.Success -> "BAGLANTI OK"
                                            is ApiConnectionStatus.Error -> "HATA"
                                            else -> "PING"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = when(apiStatus) {
                                            is ApiConnectionStatus.Success -> Color(0xFF2E7D32)
                                            is ApiConnectionStatus.Error -> Color(0xFFC62828)
                                            else -> Color.Gray
                                        },
                                        fontSize = 8.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        when (val status = apiStatus) {
                            is ApiConnectionStatus.Success -> {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Ping Gecikmesi: ${status.responseTimeMs} ms | API Sağlık Durumu: %100",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    // Raw json console
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF1E1E1E))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = status.sampleData,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            color = Color(0xFFA5D6A7),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                            is ApiConnectionStatus.Error -> {
                                Text(
                                    text = status.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            else -> {
                                Text(
                                    text = "Yerel sunucu test sinyalleri bekleniyor...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Expanded help file
        item {
            AnimatedVisibility(
                visible = showHelpInfo,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Android Studio & API Kurulum Kılavuzu 🖥️",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Bu uygulama, Google AI Studio ortamından bilgisayarınızdaki Android Studio projesine aktarılmaya uygun şekilde tasarlanmıştır.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                        Text(
                            text = "1. Projeyi Bilgisayara İndirme:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "AI Studio penceresinin sol altındaki 'Ayarlar' simgesinden veya ekran üstü araç çubuğundan projeyi ZIP olarak indirin ya da GitHub'a aktarın. ZIP dosyasını çıkarıp Android Studio ile açın.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "2. Google AI Gemini API Kurulumu:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Uygulama zaten Gemini API v1beta servisine entegre edilmiştir. Bilgisayarınızda projenin kök dizinindeki '.env' dosyasını açın ve şu satırı ekleyin:\n" +
                                    "GEMINI_API_KEY=YOUR_ACTUAL_API_KEY_HERE\n" +
                                    "Gradle derleme esnasında Secrets plugin ile BuildConfig.GEMINI_API_KEY değerini otomatik üretecektir.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "3. Stats REST Entegrasyonu:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Uygulamada hazır olan 'Retrofit' ve birleştirilmiş 'MoshiConverter' katmanı, girdiğiniz URL veya belirteçleri doğrulamak üzere geliştirilmiştir. Canlı veritabanınızı 'WorldCupViewModel' içerisindeki Retrofit sorgu tetiğine bağlayarak yayına alabilirsiniz.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Live stats pre-seeded widgets
        item {
            Text(
                text = "Güncel Turnuva İstatistikleri (Pre-Seeded)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Simple Stats widgets representation
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top scorer widget
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                            Text("Gol Krallığı", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Kylian Mbappé 🇫🇷", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Real Madrid • 5 Gol", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }

                // Assist leader widget
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(16.dp))
                            Text("Asist Krallığı", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Arda Güler 🇹🇷", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Real Madrid • 4 Asist", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
