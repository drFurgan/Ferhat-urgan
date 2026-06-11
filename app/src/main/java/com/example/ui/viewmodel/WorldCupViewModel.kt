package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GeminiService
import com.example.data.GroupStanding
import com.example.data.Team
import com.example.data.WorldCupData
import com.example.data.WorldCupMatch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed interface ApiConnectionStatus {
    object Idle : ApiConnectionStatus
    object Testing : ApiConnectionStatus
    data class Success(val responseTimeMs: Long, val sampleData: String) : ApiConnectionStatus
    data class Error(val message: String) : ApiConnectionStatus
}

sealed interface ChatUiState {
    object Idle : ChatUiState
    object Loading : ChatUiState
    data class Error(val message: String) : ChatUiState
}

class WorldCupViewModel : ViewModel() {

    // 48 Teams
    private val _teams = MutableStateFlow<List<Team>>(WorldCupData.teams)
    val teams: StateFlow<List<Team>> = _teams.asStateFlow()

    // 12 Groups (A..L) Standings
    private val _standings = MutableStateFlow<Map<String, List<GroupStanding>>>(emptyMap())
    val standings: StateFlow<Map<String, List<GroupStanding>>> = _standings.asStateFlow()

    // All Group Matches
    private val _matches = MutableStateFlow<List<WorldCupMatch>>(emptyList())
    val matches: StateFlow<List<WorldCupMatch>> = _matches.asStateFlow()

    // Favorite Team IDs
    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    // Skin Color selection for Country Skin dynamic themes
    private val _selectedSkinTeam = MutableStateFlow<Team?>(null)
    val selectedSkinTeam: StateFlow<Team?> = _selectedSkinTeam.asStateFlow()

    // Onboarding / Country Selection state
    private val _isOnboarded = MutableStateFlow<Boolean>(true) // default true to avoid flicker on non-initialized view, will load real value in LaunchedEffect
    val isOnboarded: StateFlow<Boolean> = _isOnboarded.asStateFlow()

    private val _userCountryId = MutableStateFlow<String?>(null)
    val userCountryId: StateFlow<String?> = _userCountryId.asStateFlow()

    // Match-specific chat records map: [MatchId -> List<Pair<Sender, Message>>]
    private val _matchChats = MutableStateFlow<Map<String, List<Pair<String, String>>>>(emptyMap())
    val matchChats: StateFlow<Map<String, List<Pair<String, String>>>> = _matchChats.asStateFlow()

    // Real-Time Stats API mockup & football-data.org config
    private val _apiUrl = MutableStateFlow("https://api.football-data.org/v4")
    val apiUrl: StateFlow<String> = _apiUrl.asStateFlow()

    private val _apiKey = MutableStateFlow("c03a7ca0a8804a5da3fd56f5070eb8f5")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _apiStatus = MutableStateFlow<ApiConnectionStatus>(ApiConnectionStatus.Idle)
    val apiStatus: StateFlow<ApiConnectionStatus> = _apiStatus.asStateFlow()

    // Live AI chat records (Pair<Message, isUser>)
    private val _chatMessages = MutableStateFlow<List<Pair<String, Boolean>>>(
        listOf(
            "Merhaba! Ben WordCup26 Yapay Zekalı Dünya Kupası Asistanıyım. 🤖⚽\n\nBugün 11 Haziran 2026, turnuvanın ilk günü! Maç tahminleri, takımların güncel kadroları ve rasyonel futbol analizleri hakkında benden bilgi alabilirsin. Türkiye'nin gruptaki kaderini tartışalım mı?" to false
        )
    )
    val chatMessages: StateFlow<List<Pair<String, Boolean>>> = _chatMessages.asStateFlow()

    private val _chatUiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val chatUiState: StateFlow<ChatUiState> = _chatUiState.asStateFlow()

    // General chat records (Pair<Sender, Message>)
    private val _generalChat = MutableStateFlow<List<Pair<String, String>>>(
        listOf(
            "Alperen" to "Hoş geldiniz arkadaşlar! WordCup26 başladı! 🎉",
            "Mert" to "Türkiye Grubu çok heyecanlı olacak. Fransa maçı bugün!",
            "Sena 🇹🇷" to "Haydi beyler kupa bizim olsun bu sefer!",
            "Can" to "Açılış maçını sabırsızlıkla bekliyorum."
        )
    )
    val generalChat: StateFlow<List<Pair<String, String>>> = _generalChat.asStateFlow()

    init {
        resetAndSeed()
    }

    /**
     * Initializes or resets the tournament database, generating fixtures and group tables.
     */
    fun resetAndSeed() {
        // Generate matches and override dates of critical group-stage matches
        val generatedMatches = WorldCupData.generateMatches().map { match ->
            val home = _teams.value.find { it.id == match.homeTeamId }
            val away = _teams.value.find { it.id == match.awayTeamId }
            when {
                // Group A
                match.homeTeamId == "QAT" && match.awayTeamId == "ECU" -> match.copy(date = "Bugün (22:00)")
                match.homeTeamId == "SEN" && match.awayTeamId == "NED" -> match.copy(date = "Yarın (05:00)")
                // Group B
                match.homeTeamId == "ENG" && match.awayTeamId == "USA" -> match.copy(date = "Yarın (22:00)")
                // Group E
                match.homeTeamId == "ESP" && match.awayTeamId == "GER" -> match.copy(date = "13 Haziran Cmt (22:00)")
                // Group I (Türkiye-İtalya key match)
                match.homeTeamId == "TUR" && match.awayTeamId == "ITA" -> match.copy(date = "Bugün (18:00)")
                else -> match
            }
        }
        _matches.value = generatedMatches

        // Preseed match chats with some high-octane commentary
        val chats = mutableMapOf<String, List<Pair<String, String>>>()
        generatedMatches.forEach { m ->
            val home = _teams.value.find { it.id == m.homeTeamId }
            val away = _teams.value.find { it.id == m.awayTeamId }
            if (home != null && away != null) {
                chats[m.id] = listOf(
                    "Sistem Botu" to "🏁 Matchday başladı! Taraftarlar stadyumu dolduruyor.",
                    "${home.name} Fanı" to "Hadi ${home.name}! Bu maçı alıp gruptan çıkacağız! 🔥",
                    "${away.name} Fanı" to "Hiç şansınız yok, ${away.name} çok formda!"
                )
            }
        }
        _matchChats.value = chats

        // Initialize Standings for all groups (A to L)
        val initialStandings = mutableMapOf<String, List<GroupStanding>>()
        val groups = ('A'..'L').map { it.toString() }

        groups.forEach { groupName ->
            val groupTeams = _teams.value.filter { it.group == groupName }
            initialStandings[groupName] = groupTeams.map { team ->
                GroupStanding(
                    teamId = team.id,
                    teamName = team.name,
                    teamCode = team.code,
                    flag = team.flag
                )
            }.sortedBy { it.teamName }
        }
        _standings.value = initialStandings
    }

    fun loadOnboardingState(context: android.content.Context) {
        val prefs = context.getSharedPreferences("world_cup_prefs", android.content.Context.MODE_PRIVATE)
        val onboarded = prefs.getBoolean("has_onboarded", false)
        val countryId = prefs.getString("selected_country_id", null)
        _isOnboarded.value = onboarded
        _userCountryId.value = countryId
        if (countryId != null) {
            val team = _teams.value.find { it.id == countryId }
            if (team != null) {
                _selectedSkinTeam.value = team
            }
        }
    }

    fun selectUserCountry(team: Team, context: android.content.Context) {
        _selectedSkinTeam.value = team
        _userCountryId.value = team.id
        _isOnboarded.value = true
        val prefs = context.getSharedPreferences("world_cup_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean("has_onboarded", true)
            .putString("selected_country_id", team.id)
            .apply()
    }

    fun selectSkinTeam(team: Team?, context: android.content.Context? = null) {
        _selectedSkinTeam.value = team
        _userCountryId.value = team?.id
        if (context != null) {
            val prefs = context.getSharedPreferences("world_cup_prefs", android.content.Context.MODE_PRIVATE)
            if (team != null) {
                _isOnboarded.value = true
                prefs.edit()
                    .putBoolean("has_onboarded", true)
                    .putString("selected_country_id", team.id)
                    .apply()
            } else {
                prefs.edit()
                    .remove("selected_country_id")
                    .apply()
            }
        }
    }

    fun sendMatchChatMessage(matchId: String, sender: String, text: String) {
        val current = _matchChats.value.toMutableMap()
        val messages = current[matchId]?.toMutableList() ?: mutableListOf()
        messages.add(sender to text)
        current[matchId] = messages
        _matchChats.value = current

        // Auto reply simulation content after 1s
        viewModelScope.launch {
            delay(1000)
            val updated = _matchChats.value.toMutableMap()
            val list = updated[matchId]?.toMutableList() ?: mutableListOf()
            val match = _matches.value.find { it.id == matchId }
            val home = _teams.value.find { it.id == match?.homeTeamId }
            val away = _teams.value.find { it.id == match?.awayTeamId }
            if (home != null && away != null) {
                val funnyReplies = listOf(
                    "Spiker 🎙️" to "Harika bir pozisyon kaçtı! Taraftarlar ayakta.",
                    "Analist 📊" to "Topa sahip olma istatistiklerinde iki takım da başa baş gidiyor.",
                    "${away.name} Fanı" to "Kadromuzdaki yıldızlar ikinci yarıda fark yaratacak!",
                    "Fırtına ⚡" to "Türk taraftarlar stadyumda 'Kırmızı Beyaz' sesleriyle şov yapıyor!"
                )
                val reply = funnyReplies[Random.nextInt(funnyReplies.size)]
                list.add(reply)
                updated[matchId] = list
                _matchChats.value = updated
            }
        }
    }

    fun sendGeneralChatMessage(sender: String, text: String) {
        val current = _generalChat.value.toMutableList()
        current.add(sender to text)
        _generalChat.value = current

        // Auto random fan reply simulation after 1.5s
        viewModelScope.launch {
            delay(1500)
            val updated = _generalChat.value.toMutableList()
            val replies = listOf(
                "Emre" to "Bence de haklısın, kadro kalitesi çok belirleyici olacak.",
                "Zeynep ⚽" to "Her maç final tadında geçecek gibi duruyor. Harika bir kupa bizi bekliyor!",
                "Burak" to "İstatistikler zaten favoriyi gösteriyor ama kupada sürpriz her an kapıda!",
                "Cem" to "Bugünkü maçların hepsi inanılmaz çekişmeli geçecek.",
                "Ebru" to "Türk milli takımımızın maçını stadyumda izlemeyi çok isterdim gerçekten!"
            )
            val reply = replies[Random.nextInt(replies.size)]
            updated.add(reply)
            _generalChat.value = updated
        }
    }

    fun toggleFavorite(teamId: String) {
        val current = _favorites.value.toMutableSet()
        if (current.contains(teamId)) {
            current.remove(teamId)
        } else {
            current.add(teamId)
        }
        _favorites.value = current
    }

    fun updateApiUrl(url: String) {
        _apiUrl.value = url
    }

    fun updateApiKey(key: String) {
        _apiKey.value = key
    }

    /**
     * Connects and executes queries to football-data.org API on their live databases.
     */
    fun testStatsApiConnection() {
        if (_apiUrl.value.isBlank() || _apiKey.value.isBlank()) {
            _apiStatus.value = ApiConnectionStatus.Error("API URL ve API Key alanları boş bırakılamaz.")
            return
        }

        viewModelScope.launch {
            _apiStatus.value = ApiConnectionStatus.Testing
            val startTime = System.currentTimeMillis()

            // Determine league competition based on API free tier access. Default is PL.
            // If they query stats/euro, we can pass 'EC' or 'WC'. We will fetch PL standings.
            val result = com.example.data.FootballDataService.fetchCompetitionStandings(
                apiKey = _apiKey.value,
                competition = if (_apiUrl.value.contains("WC", ignoreCase = true)) "WC" else "PL"
            )

            val elapsed = System.currentTimeMillis() - startTime

            when (result) {
                is com.example.data.FootballDataService.Result.Success -> {
                    _apiStatus.value = ApiConnectionStatus.Success(elapsed, result.rawJson)
                }
                is com.example.data.FootballDataService.Result.Error -> {
                    _apiStatus.value = ApiConnectionStatus.Error(result.message)
                }
            }
        }
    }

    /**
     * Simulates a single match based on team ratings.
     */
    fun simulateMatch(matchId: String) {
        val matchIndex = _matches.value.indexOfFirst { it.id == matchId }
        if (matchIndex == -1) return

        val match = _matches.value[matchIndex]
        if (match.isPlayed) return

        val homeTeam = _teams.value.find { it.id == match.homeTeamId } ?: return
        val awayTeam = _teams.value.find { it.id == match.awayTeamId } ?: return

        // Simple soccer simulation algorithm using ratings
        val ratingDiff = homeTeam.rating - awayTeam.rating // range e.g. -20 to +20
        var homeExpect = 1.3 + (ratingDiff * 0.05)
        var awayExpect = 1.1 - (ratingDiff * 0.05)

        // Ensure expectancy is positive
        if (homeExpect < 0.2) homeExpect = 0.2
        if (awayExpect < 0.2) awayExpect = 0.2

        // Poisson-like distribution simulated with random
        val homeScore = simulateGoals(homeExpect)
        val awayScore = simulateGoals(awayExpect)

        val updatedMatch = match.copy(
            homeScore = homeScore,
            awayScore = awayScore,
            isPlayed = true
        )

        val currentMatches = _matches.value.toMutableList()
        currentMatches[matchIndex] = updatedMatch
        _matches.value = currentMatches

        recalculateStandingsForGroup(match.group, currentMatches)
    }

    /**
     * Simulates all unplayed matches in the entire World Cup!
     */
    fun simulateAllMatches() {
        val currentMatches = _matches.value.map { match ->
            if (match.isPlayed) {
                match
            } else {
                val homeTeam = _teams.value.find { it.id == match.homeTeamId } ?: return@map match
                val awayTeam = _teams.value.find { it.id == match.awayTeamId } ?: return@map match

                val ratingDiff = homeTeam.rating - awayTeam.rating
                var homeExpect = 1.35 + (ratingDiff * 0.05)
                var awayExpect = 1.15 - (ratingDiff * 0.05)

                if (homeExpect < 0.2) homeExpect = 0.2
                if (awayExpect < 0.2) awayExpect = 0.2

                val homeScore = simulateGoals(homeExpect)
                val awayScore = simulateGoals(awayExpect)

                match.copy(
                    homeScore = homeScore,
                    awayScore = awayScore,
                    isPlayed = true
                )
            }
        }

        _matches.value = currentMatches

        // Recalculate standings for all groups
        val groups = ('A'..'L').map { it.toString() }
        val updatedStandings = _standings.value.toMutableMap()
        groups.forEach { groupName ->
            val groupMatches = currentMatches.filter { it.group == groupName }
            val groupTeams = _teams.value.filter { it.group == groupName }

            val teamStandings = groupTeams.map { team ->
                var played = 0
                var won = 0
                var drawn = 0
                var lost = 0
                var gf = 0
                var ga = 0

                groupMatches.forEach { match ->
                    if (match.isPlayed) {
                        if (match.homeTeamId == team.id) {
                            played++
                            gf += match.homeScore ?: 0
                            ga += match.awayScore ?: 0
                            val hs = match.homeScore ?: 0
                            val ascore = match.awayScore ?: 0
                            if (hs > ascore) won++
                            else if (hs == ascore) drawn++
                            else lost++
                        } else if (match.awayTeamId == team.id) {
                            played++
                            gf += match.awayScore ?: 0
                            ga += match.homeScore ?: 0
                            val hs = match.homeScore ?: 0
                            val ascore = match.awayScore ?: 0
                            if (ascore > hs) won++
                            else if (ascore == hs) drawn++
                            else lost++
                        }
                    }
                }

                val gd = gf - ga
                val points = (won * 3) + drawn

                GroupStanding(
                    teamId = team.id,
                    teamName = team.name,
                    teamCode = team.code,
                    flag = team.flag,
                    played = played,
                    won = won,
                    drawn = drawn,
                    lost = lost,
                    goalsFor = gf,
                    goalsAgainst = ga,
                    goalsDifference = gd,
                    points = points
                )
            }.sortedWith(
                compareByDescending<GroupStanding> { it.points }
                    .thenByDescending { it.goalsDifference }
                    .thenByDescending { it.goalsFor }
                    .thenBy { it.teamName }
            )

            updatedStandings[groupName] = teamStandings
        }

        _standings.value = updatedStandings
    }

    private fun simulateGoals(expectedValue: Double): Int {
        val rand = Random.nextDouble()
        // Simple goal probability thresholds based on soccer expectations
        return when {
            rand < Math.exp(-expectedValue) -> 0
            rand < Math.exp(-expectedValue) * (1 + expectedValue) -> 1
            rand < Math.exp(-expectedValue) * (1 + expectedValue + (expectedValue * expectedValue) / 2) -> 2
            rand < 0.94 -> 3
            rand < 0.98 -> 4
            else -> 5
        }
    }

    private fun recalculateStandingsForGroup(groupName: String, allMatches: List<WorldCupMatch>) {
        val groupMatches = allMatches.filter { it.group == groupName }
        val groupTeams = _teams.value.filter { it.group == groupName }

        val teamStandings = groupTeams.map { team ->
            var played = 0
            var won = 0
            var drawn = 0
            var lost = 0
            var gf = 0
            var ga = 0

            groupMatches.forEach { match ->
                if (match.isPlayed) {
                    if (match.homeTeamId == team.id) {
                        played++
                        gf += match.homeScore ?: 0
                        ga += match.awayScore ?: 0
                        val hs = match.homeScore ?: 0
                        val ascore = match.awayScore ?: 0
                        if (hs > ascore) won++
                        else if (hs == ascore) drawn++
                        else lost++
                    } else if (match.awayTeamId == team.id) {
                        played++
                        gf += match.awayScore ?: 0
                        ga += match.homeScore ?: 0
                        val hs = match.homeScore ?: 0
                        val ascore = match.awayScore ?: 0
                        if (ascore > hs) won++
                        else if (ascore == hs) drawn++
                        else lost++
                    }
                }
            }

            val gd = gf - ga
            val points = (won * 3) + drawn

            GroupStanding(
                teamId = team.id,
                teamName = team.name,
                teamCode = team.code,
                flag = team.flag,
                played = played,
                won = won,
                drawn = drawn,
                lost = lost,
                goalsFor = gf,
                goalsAgainst = ga,
                goalsDifference = gd,
                points = points
            )
        }.sortedWith(
            compareByDescending<GroupStanding> { it.points }
                .thenByDescending { it.goalsDifference }
                .thenByDescending { it.goalsFor }
                .thenBy { it.teamName }
        )

        val updated = _standings.value.toMutableMap()
        updated[groupName] = teamStandings
        _standings.value = updated
    }

    /**
     * Calls Gemini AI Assistant to provide analysis for the soccer app.
     */
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return

        val userMsg = text.trim()
        val currentMsgs = _chatMessages.value.toMutableList()
        currentMsgs.add(userMsg to true)
        _chatMessages.value = currentMsgs

        _chatUiState.value = ChatUiState.Loading

        viewModelScope.launch {
            // Drop safety instruction message & first user greeting from raw API histories if too long,
            // but send trailing pairs to keep context!
            val payloadHistory = if (currentMsgs.size > 8) {
                currentMsgs.takeLast(8).dropLast(1)
            } else {
                currentMsgs.dropLast(1)
            }

            val reply = GeminiService.askAssistant(userMsg, payloadHistory)
            
            val updatedMsgs = _chatMessages.value.toMutableList()
            updatedMsgs.add(reply to false)
            _chatMessages.value = updatedMsgs

            _chatUiState.value = ChatUiState.Idle
        }
    }

    /**
     * Triggers AI feedback for quick questions from team profiles (e.g. "Kanaliz et", "Taktiğini İncele").
     */
    fun askAiAboutTeam(teamName: String) {
        val quickPrompt = "$teamName milli takımının 2026 Dünya Kupası gruptaki şansını, taktik dizilişini ve kilit oyuncularını 3 kısa paragrafta analiz eder misin?"
        sendChatMessage(quickPrompt)
    }
}
