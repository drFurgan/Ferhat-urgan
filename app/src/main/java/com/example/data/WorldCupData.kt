package com.example.data

data class Player(
    val name: String,
    val position: String,
    val club: String,
    val rating: Int
)

data class Team(
    val id: String,
    val name: String,
    val code: String,
    val flag: String,
    val primaryColor: String, // Hex color (e.g., "#E30A17")
    val secondaryColor: String, // Hex color (e.g., "#FFFFFF")
    val group: String, // "A" to "L"
    val rating: Int, // 50 to 99
    val tactician: String,
    val starPlayers: List<Player>
)

data class GroupStanding(
    val teamId: String,
    val teamName: String,
    val teamCode: String,
    val flag: String,
    val played: Int = 0,
    val won: Int = 0,
    val drawn: Int = 0,
    val lost: Int = 0,
    val goalsFor: Int = 0,
    val goalsAgainst: Int = 0,
    val goalsDifference: Int = 0,
    val points: Int = 0
)

data class WorldCupMatch(
    val id: String,
    val group: String,
    val homeTeamId: String,
    val awayTeamId: String,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val isPlayed: Boolean = false,
    val date: String,
    val stadium: String
)

object WorldCupData {
    val teams = listOf(
        // Group A (2022 World Cup Group A)
        Team("QAT", "Katar", "QAT", "🇶🇦", "#8A1538", "#FFFFFF", "A", 75, "Tintín Márquez", listOf(
            Player("Akram Afif", "FOR", "Al-Sadd", 77),
            Player("Almoez Ali", "FOR", "Al-Duhail", 74),
            Player("Hassan Al-Haydos", "MID", "Al-Sadd", 71),
            Player("Lucas Mendes", "DEF", "Al-Wakrah", 71),
            Player("Meshaal Barsham", "GK", "Al-Sadd", 71)
        )),
        Team("ECU", "Ekvador", "ECU", "🇪🇨", "#FFDD00", "#001489", "A", 81, "Sebastián Beccacece", listOf(
            Player("Moises Caicedo", "MID", "Chelsea", 84),
            Player("Piero Hincapié", "DEF", "Leverkusen", 83),
            Player("Willian Pacho", "DEF", "PSG", 82),
            Player("Enner Valencia", "FOR", "Internacional", 77),
            Player("Hernán Galíndez", "GK", "Huracán", 76)
        )),
        Team("SEN", "Senegal", "SEN", "🇸🇳", "#00853F", "#FDEF42", "A", 82, "Aliou Cissé", listOf(
            Player("Sadio Mané", "FOR", "Al-Nassr", 84),
            Player("Nicolas Jackson", "FOR", "Chelsea", 82),
            Player("Kalidou Koulibaly", "DEF", "Al-Hilal", 81),
            Player("Édouard Mendy", "GK", "Al-Ahli", 80),
            Player("Pape Matar Sarr", "MID", "Tottenham", 80)
        )),
        Team("NED", "Hollanda", "NED", "🇳🇱", "#FF4F00", "#FFFFFF", "A", 88, "Ronald Koeman", listOf(
            Player("Virgil van Dijk", "DEF", "Liverpool", 88),
            Player("Frenkie de Jong", "MID", "Barcelona", 86),
            Player("Cody Gakpo", "FOR", "Liverpool", 84),
            Player("Xavi Simons", "MID", "RB Leipzig", 84),
            Player("Bart Verbruggen", "GK", "Brighton", 81)
        )),

        // Group B (2022 World Cup Group B)
        Team("ENG", "İngiltere", "ENG", "🏴󠁧󠁢󠁥󠁮󠁧󠁿", "#FFFFFF", "#0F1D36", "B", 91, "Thomas Tuchel", listOf(
            Player("Jude Bellingham", "MID", "Real Madrid", 90),
            Player("Harry Kane", "FOR", "Bayern Munich", 89),
            Player("Bukayo Saka", "FOR", "Arsenal", 88),
            Player("Declan Rice", "MID", "Arsenal", 87),
            Player("Jordan Pickford", "GK", "Everton", 83)
        )),
        Team("IRN", "İran", "IRN", "🇮🇷", "#239F40", "#DA251D", "B", 79, "Amir Ghalenoei", listOf(
            Player("Mehdi Taremi", "FOR", "Inter Milan", 81),
            Player("Sardar Azmoun", "FOR", "Shabab Al-Ahli", 79),
            Player("Alireza Jahanbakhsh", "FOR", "Heerenveen", 74),
            Player("Milad Mohammadi", "DEF", "Adana Demirspor", 73),
            Player("Alireza Beiranvand", "GK", "Tractor", 74)
        )),
        Team("USA", "ABD", "USA", "🇺🇸", "#0A3161", "#B31942", "B", 85, "Mauricio Pochettino", listOf(
            Player("Christian Pulisic", "FOR", "AC Milan", 86),
            Player("Weston McKennie", "MID", "Juventus", 83),
            Player("Antonee Robinson", "DEF", "Fulham", 82),
            Player("Gio Reyna", "MID", "Dortmund", 80),
            Player("Matt Turner", "GK", "Crystal Palace", 79)
        )),
        Team("WAL", "Galler", "WAL", "🏴󠁧󠁢󠁷󠁬󠁳󠁿", "#00AD43", "#E30A17", "B", 78, "Craig Bellamy", listOf(
            Player("Brennan Johnson", "FOR", "Tottenham", 80),
            Player("Harry Wilson", "MID", "Fulham", 78),
            Player("Daniel James", "FOR", "Leeds", 75),
            Player("Ethan Ampadu", "MID", "Leeds", 76),
            Player("Danny Ward", "GK", "Leicester", 74)
        )),

        // Group C (2022 World Cup Group C)
        Team("ARG", "Arjantin", "ARG", "🇦🇷", "#75AADB", "#FFFFFF", "C", 93, "Lionel Scaloni", listOf(
            Player("Lionel Messi", "FOR", "Inter Miami", 89),
            Player("Lautaro Martínez", "FOR", "Inter Milan", 88),
            Player("Alexis Mac Allister", "MID", "Liverpool", 86),
            Player("Enzo Fernández", "MID", "Chelsea", 84),
            Player("Emiliano Martínez", "GK", "Aston Villa", 87)
        )),
        Team("KSA", "Suudi Arabistan", "KSA", "🇸🇦", "#006C35", "#FFFFFF", "C", 77, "Hervé Renard", listOf(
            Player("Salem Al-Dawsari", "FOR", "Al-Hilal", 77),
            Player("Firas Al-Buraikan", "FOR", "Al-Ahli", 74),
            Player("Saud Abdulhamid", "DEF", "Roma", 75),
            Player("Mohamed Kanno", "MID", "Al-Hilal", 73),
            Player("Yassine Bounou", "GK", "Al-Hilal", 84)
        )),
        Team("MEX", "Meksika", "MEX", "🇲🇽", "#006747", "#C8102E", "C", 82, "Javier Aguirre", listOf(
            Player("Santiago Giménez", "FOR", "Feyenoord", 84),
            Player("Edson Álvarez", "MID", "West Ham", 83),
            Player("Hirving Lozano", "FOR", "San Diego FC", 81),
            Player("Johan Vásquez", "DEF", "Genoa", 79),
            Player("Luis Malagón", "GK", "Club América", 78)
        )),
        Team("POL", "Polonya", "POL", "🇵🇱", "#DC143C", "#FFFFFF", "C", 81, "Michał Probierz", listOf(
            Player("Robert Lewandowski", "FOR", "Barcelona", 87),
            Player("Piotr Zieliński", "MID", "Inter Milan", 83),
            Player("Sebastian Szymański", "MID", "Fenerbahçe", 81),
            Player("Nicola Zalewski", "DEF", "Roma", 79),
            Player("Łukasz Skorupski", "GK", "Bologna", 81)
        )),

        // Group D (2022 World Cup Group D)
        Team("FRA", "Fransa", "FRA", "🇫🇷", "#002395", "#ED2939", "D", 92, "Didier Deschamps", listOf(
            Player("Kylian Mbappé", "FOR", "Real Madrid", 92),
            Player("Antoine Griezmann", "FOR", "Atletico Madrid", 86),
            Player("Aurélien Tchouaméni", "MID", "Real Madrid", 85),
            Player("William Saliba", "DEF", "Arsenal", 88),
            Player("Mike Maignan", "GK", "AC Milan", 86)
        )),
        Team("AUS", "Avustralya", "AUS", "🇦🇺", "#00008B", "#FFD700", "D", 78, "Tony Popovic", listOf(
            Player("Jackson Irvine", "MID", "St. Pauli", 76),
            Player("Harry Souttar", "DEF", "Sheffield Utd", 76),
            Player("Mathew Ryan", "GK", "Roma", 76),
            Player("Nestory Irankunda", "FOR", "Bayern Munich", 73),
            Player("Craig Goodwin", "FOR", "Al-Wehda", 75)
        )),
        Team("DEN", "Danimarka", "DEN", "🇩🇰", "#C60C30", "#FFFFFF", "D", 84, "Brian Riemer", listOf(
            Player("Christian Eriksen", "MID", "Man United", 80),
            Player("Pierre-Emile Højbjerg", "MID", "Marseille", 82),
            Player("Rasmus Højlund", "FOR", "Man United", 81),
            Player("Andreas Christensen", "DEF", "Barcelona", 82),
            Player("Kasper Schmeichel", "GK", "Celtic", 79)
        )),
        Team("TUN", "Tunus", "TUN", "🇹🇳", "#E30A17", "#FFFFFF", "D", 77, "Kais Yaâkoubi", listOf(
            Player("Elyes Skhiri", "MID", "Eintracht Frankfurt", 79),
            Player("Aissa Laïdouni", "MID", "Al-Wakrah", 75),
            Player("Hannibal Mejbri", "MID", "Burnley", 74),
            Player("Montassar Talbi", "DEF", "Lorient", 75),
            Player("Aymen Dahmen", "GK", "Al-Hazem", 72)
        )),

        // Group E (2022 World Cup Group E)
        Team("ESP", "İspanya", "ESP", "🇪🇸", "#C60B1E", "#FFC400", "E", 92, "Luis de la Fuente", listOf(
            Player("Lamine Yamal", "FOR", "Barcelona", 89),
            Player("Rodri", "MID", "Man City", 91),
            Player("Nico Williams", "FOR", "Athletic Bilbao", 85),
            Player("Dani Olmo", "MID", "Barcelona", 85),
            Player("Unai Simón", "GK", "Athletic Bilbao", 85)
        )),
        Team("CRC", "Kosta Rika", "CRC", "🇨🇷", "#002F6C", "#C8102E", "E", 76, "Claudio Vivas", listOf(
            Player("Keylor Navas", "GK", "Free Agent", 80),
            Player("Manfred Ugalde", "FOR", "Spartak Moscow", 76),
            Player("Francisco Calvo", "DEF", "Hatayspor", 74),
            Player("Joel Campbell", "FOR", "Alajuelense", 73),
            Player("Brandon Aguilera", "MID", "Rio Ave", 72)
        )),
        Team("GER", "Almanya", "GER", "🇩🇪", "#000000", "#FFCE00", "E", 90, "Julian Nagelsmann", listOf(
            Player("Jamal Musiala", "MID", "Bayern Munich", 89),
            Player("Florian Wirtz", "MID", "Leverkusen", 89),
            Player("Kai Havertz", "FOR", "Arsenal", 84),
            Player("Antonio Rüdiger", "DEF", "Real Madrid", 87),
            Player("Marc-André ter Stegen", "GK", "Barcelona", 86)
        )),
        Team("JPN", "Japonya", "JPN", "🇯🇵", "#000080", "#FFFFFF", "E", 83, "Hajime Moriyasu", listOf(
            Player("Kaoru Mitoma", "FOR", "Brighton", 84),
            Player("Takefusa Kubo", "MID", "Real Sociedad", 84),
            Player("Wataru Endo", "MID", "Liverpool", 81),
            Player("Hiroki Ito", "DEF", "Bayern Munich", 81),
            Player("Zion Suzuki", "GK", "Parma", 78)
        )),

        // Group F (2022 World Cup Group F)
        Team("BEL", "Belçika", "BEL", "🇧🇪", "#E30A17", "#FFD700", "F", 86, "Domenico Tedesco", listOf(
            Player("Kevin De Bruyne", "MID", "Man City", 90),
            Player("Romelu Lukaku", "FOR", "Napoli", 82),
            Player("Jérémy Doku", "FOR", "Man City", 82),
            Player("Amadou Onana", "MID", "Aston Villa", 81),
            Player("Koen Casteels", "GK", "Al-Qadsiah", 81)
        )),
        Team("CAN", "Kanada", "CAN", "🇨🇦", "#FF0000", "#FFFFFF", "F", 81, "Jesse Marsch", listOf(
            Player("Alphonso Davies", "DEF", "Bayern Munich", 86),
            Player("Jonathan David", "FOR", "Lille", 84),
            Player("Stephen Eustáquio", "MID", "FC Porto", 80),
            Player("Tajon Buchanan", "FOR", "Inter Milan", 78),
            Player("Alistair Johnston", "DEF", "Celtic", 79)
        )),
        Team("MAR", "Fas", "MAR", "🇲🇦", "#C1272D", "#006233", "F", 85, "Walid Regragui", listOf(
            Player("Achraf Hakimi", "DEF", "PSG", 86),
            Player("Brahim Díaz", "MID", "Real Madrid", 84),
            Player("Sofyan Amrabat", "MID", "Fenerbahçe", 81),
            Player("Youssef En-Nesyri", "FOR", "Fenerbahçe", 81),
            Player("Yassine Bounou", "GK", "Al-Hilal", 84)
        )),
        Team("CRO", "Hırvatistan", "CRO", "🇭🇷", "#FF0000", "#002F6C", "F", 85, "Zlatko Dalić", listOf(
            Player("Luka Modrić", "MID", "Real Madrid", 84),
            Player("Joško Gvardiol", "DEF", "Man City", 86),
            Player("Mateo Kovačić", "MID", "Man City", 82),
            Player("Andrej Kramarić", "FOR", "Hoffenheim", 80),
            Player("Dominik Livaković", "GK", "Fenerbahçe", 82)
        )),

        // Group G (2022 World Cup Group G)
        Team("BRA", "Brezilya", "BRA", "🇧🇷", "#FFDC02", "#009B3A", "G", 91, "Dorival Júnior", listOf(
            Player("Vinicius Júnior", "FOR", "Real Madrid", 91),
            Player("Rodrygo", "FOR", "Real Madrid", 86),
            Player("Bruno Guimarães", "MID", "Newcastle", 85),
            Player("Gabriel Magalhães", "DEF", "Arsenal", 86),
            Player("Alisson Becker", "GK", "Liverpool", 88)
        )),
        Team("SRB", "Sırbistan", "SRB", "🇷🇸", "#C8102E", "#0C1D33", "G", 82, "Dragan Stojković", listOf(
            Player("Dušan Vlahović", "FOR", "Juventus", 84),
            Player("Aleksandar Mitrović", "FOR", "Al-Hilal", 83),
            Player("Sergej Milinković-Savić", "MID", "Al-Hilal", 83),
            Player("Strahinja Pavlović", "DEF", "AC Milan", 80),
            Player("Vanja Milinković-Savić", "GK", "Torino", 80)
        )),
        Team("SUI", "İsviçre", "SUI", "🇨🇭", "#D52B1E", "#FFFFFF", "G", 83, "Murat Yakin", listOf(
            Player("Granit Xhaka", "MID", "Leverkusen", 85),
            Player("Manuel Akanji", "DEF", "Man City", 84),
            Player("Breel Embolo", "FOR", "Monaco", 79),
            Player("Denis Zakaria", "MID", "Monaco", 81),
            Player("Yann Sommer", "GK", "Inter Milan", 83)
        )),
        Team("CMR", "Kamerun", "CMR", "🇨🇲", "#43B02A", "#FFCD00", "G", 79, "Marc Brys", listOf(
            Player("André Onana", "GK", "Man United", 84),
            Player("Bryan Mbeumo", "FOR", "Brentford", 82),
            Player("Frank Anguissa", "MID", "Napoli", 80),
            Player("Vincent Aboubakar", "FOR", "Hatayspor", 77),
            Player("Christopher Wooh", "DEF", "Rennes", 76)
        )),

        // Group H (2022 World Cup Group H)
        Team("POR", "Portekiz", "POR", "🇵🇹", "#FF0000", "#006600", "H", 90, "Roberto Martínez", listOf(
            Player("Cristiano Ronaldo", "FOR", "Al-Nassr", 85),
            Player("Bruno Fernandes", "MID", "Man United", 87),
            Player("Rafael Leão", "FOR", "AC Milan", 86),
            Player("Rúben Dias", "DEF", "Man City", 88),
            Player("Diogo Costa", "GK", "FC Porto", 85)
        )),
        Team("GHA", "Gana", "GHA", "🇬🇭", "#FCD116", "#CE1126", "H", 79, "Otto Addo", listOf(
            Player("Mohammed Kudus", "MID", "West Ham", 84),
            Player("Inaki Williams", "FOR", "Athletic Bilbao", 82),
            Player("Thomas Partey", "MID", "Arsenal", 81),
            Player("Jordan Ayew", "FOR", "Leicester", 76),
            Player("Lawrence Ati-Zigi", "GK", "St. Gallen", 75)
        )),
        Team("URU", "Uruguay", "URU", "🇺🇾", "#43A047", "#FFFFFF", "H", 86, "Marcelo Bielsa", listOf(
            Player("Federico Valverde", "MID", "Real Madrid", 88),
            Player("Darwin Núñez", "FOR", "Liverpool", 83),
            Player("Ronald Araújo", "DEF", "Barcelona", 85),
            Player("Manuel Ugarte", "MID", "Man United", 82),
            Player("Sergio Rochet", "GK", "Internacional", 80)
        )),
        Team("KOR", "Güney Kore", "KOR", "🇰🇷", "#CD113B", "#112288", "H", 82, "Hong Myung-bo", listOf(
            Player("Heung-min Son", "FOR", "Tottenham", 86),
            Player("Min-jae Kim", "DEF", "Bayern Munich", 85),
            Player("Kang-in Lee", "MID", "PSG", 82),
            Player("Hee-chan Hwang", "FOR", "Wolves", 79),
            Player("Hyeon-woo Jo", "GK", "Ulsan HD", 76)
        )),

        // Group I (Custom Group)
        Team("TUR", "Türkiye", "TUR", "🇹🇷", "#E30A17", "#FFFFFF", "I", 84, "Vincenzo Montella", listOf(
            Player("Hakan Çalhanoğlu", "MID", "Inter Milan", 87),
            Player("Arda Güler", "MID", "Real Madrid", 84),
            Player("Barış Alper Yılmaz", "FOR", "Galatasaray", 82),
            Player("Ferdi Kadıoğlu", "DEF", "Brighton", 83),
            Player("Mert Günok", "GK", "Beşiktaş", 81)
        )),
        Team("ITA", "İtalya", "ITA", "🇮🇹", "#004F9F", "#FFFFFF", "I", 88, "Luciano Spalletti", listOf(
            Player("Nicolò Barella", "MID", "Inter Milan", 87),
            Player("Federico Chiesa", "FOR", "Liverpool", 83),
            Player("Alessandro Bastoni", "DEF", "Inter Milan", 86),
            Player("Gianluigi Donnarumma", "GK", "PSG", 87),
            Player("Mateo Retegui", "FOR", "Atalanta", 81)
        )),
        Team("COL", "Kolombiya", "COL", "🇨🇴", "#FCD116", "#003893", "I", 84, "Néstor Lorenzo", listOf(
            Player("Luis Díaz", "FOR", "Liverpool", 85),
            Player("James Rodríguez", "MID", "Rayo Vallecano", 81),
            Player("Daniel Muñoz", "DEF", "Crystal Palace", 81),
            Player("Jhon Durán", "FOR", "Aston Villa", 82),
            Player("Camilo Vargas", "GK", "Atlas", 79)
        )),
        Team("ALG", "Cezayir", "ALG", "🇩🇿", "#006233", "#FFFFFF", "I", 80, "Vladimir Petković", listOf(
            Player("Riyad Mahrez", "FOR", "Al-Ahli", 80),
            Player("Rayan Aït-Nouri", "DEF", "Wolves", 81),
            Player("Ismaël Bennacer", "MID", "AC Milan", 81),
            Player("Said Benrahma", "FOR", "Lyon", 78),
            Player("Anthony Mandrea", "GK", "Caen", 75)
        )),

        // Group J (Custom Group)
        Team("UKR", "Ukrayna", "UKR", "🇺🇦", "#FFD700", "#0057B7", "J", 81, "Serhiy Rebrov", listOf(
            Player("Artem Dovbyk", "FOR", "Roma", 83),
            Player("Oleksandr Zinchenko", "DEF", "Arsenal", 80),
            Player("Georgiy Sudakov", "MID", "Shakhtar", 81),
            Player("Mykhailo Mudryk", "FOR", "Chelsea", 78),
            Player("Andriy Lunin", "GK", "Real Madrid", 82)
        )),
        Team("BIH", "Bosna-Hersek", "BIH", "🇧🇦", "#002F6C", "#FCD116", "J", 78, "Sergej Barbarez", listOf(
            Player("Edin Dzeko", "FOR", "Fenerbahçe", 82),
            Player("Ermedin Demirovic", "FOR", "Stuttgart", 80),
            Player("Sead Kolasinac", "DEF", "Atalanta", 78),
            Player("Benjamin Tahirovic", "MID", "Ajax", 74),
            Player("Nikola Vasilj", "GK", "St. Pauli", 75)
        )),
        Team("CZE", "Çekya", "CZE", "🇨🇿", "#11457E", "#D71426", "J", 80, "Ivan Hašek", listOf(
            Player("Patrik Schick", "FOR", "Leverkusen", 81),
            Player("Tomas Soucek", "MID", "West Ham", 81),
            Player("Vladimir Coufal", "DEF", "West Ham", 79),
            Player("Ladislav Krejci", "DEF", "Girona", 78),
            Player("Matej Kovar", "GK", "Leverkusen", 76)
        )),
        Team("IRQ", "Irak", "IRQ", "🇮🇶", "#C8102E", "#007A3D", "J", 75, "Jesús Casas", listOf(
            Player("Aymen Hussein", "FOR", "Al-Khor", 76),
            Player("Ali Jasim", "MID", "Como", 73),
            Player("Ibrahim Bayesh", "MID", "Al-Riyadh", 71),
            Player("Rebin Sulaka", "DEF", "FC Seoul", 69),
            Player("Jalal Hassan", "GK", "Al-Zawraa", 70)
        )),

        // Group K (Custom Group)
        Team("EGY", "Mısır", "EGY", "🇪🇬", "#C8102E", "#000000", "K", 80, "Hossam Hassan", listOf(
            Player("Mohamed Salah", "FOR", "Liverpool", 88),
            Player("Mostafa Mohamed", "FOR", "Nantes", 79),
            Player("Omar Marmoush", "FOR", "Eintracht Frankfurt", 84),
            Player("Mohamed Elneny", "MID", "Al-Jazira", 75),
            Player("Mohamed El Shenawy", "GK", "Al Ahly", 77)
        )),
        Team("CHI", "Şili", "CHI", "🇨🇱", "#0039A6", "#D52B1E", "K", 78, "Ricardo Gareca", listOf(
            Player("Alexis Sánchez", "FOR", "Udinese", 77),
            Player("Ben Brereton Díaz", "FOR", "Southampton", 75),
            Player("Guillermo Maripán", "DEF", "Torino", 76),
            Player("Erick Pulgar", "MID", "Flamengo", 76),
            Player("Brayan Cortés", "GK", "Colo-Colo", 73)
        )),
        Team("RSA", "Güney Afrika", "RSA", "🇿🇦", "#007C59", "#FFB81C", "K", 76, "Hugo Broos", listOf(
            Player("Percy Tau", "FOR", "Al Ahly", 75),
            Player("Teboho Mokoena", "MID", "Mamelodi Sundowns", 76),
            Player("Ronwen Williams", "GK", "Mamelodi Sundowns", 76),
            Player("Aubrey Modiba", "DEF", "Mamelodi Sundowns", 72),
            Player("Khuliso Mudau", "DEF", "Mamelodi Sundowns", 73)
        )),
        Team("MLI", "Mali", "MLI", "🇲🇱", "#FCD116", "#CE1126", "K", 77, "Tom Saintfiet", listOf(
            Player("Yves Bissouma", "MID", "Tottenham", 81),
            Player("Amadou Haidara", "MID", "RB Leipzig", 79),
            Player("Cheick Doucouré", "MID", "Crystal Palace", 78),
            Player("Hamari Traoré", "DEF", "Real Sociedad", 78),
            Player("Djigui Diarra", "GK", "Young Africans", 72)
        )),

        // Group L (Custom Group)
        Team("UZB", "Özbekistan", "UZB", "🇺🇿", "#0099B5", "#CE1126", "L", 76, "Srečko Katanec", listOf(
            Player("Eldor Shomurodov", "FOR", "Roma", 77),
            Player("Abbosbek Fayzullaev", "MID", "CSKA Moscow", 76),
            Player("Otabek Shukurov", "MID", "Al-Fayha", 73),
            Player("Husniddin Aliqulov", "DEF", "Rizespor", 72),
            Player("Utkir Yusupov", "GK", "Foolad", 71)
        )),
        Team("VEN", "Venezuela", "VEN", "🇻🇪", "#7A1C1C", "#FCD116", "L", 77, "Fernando Batista", listOf(
            Player("Salomón Rondón", "FOR", "Pachuca", 78),
            Player("Yangel Herrera", "MID", "Girona", 80),
            Player("Jefferson Savarino", "FOR", "Botafogo", 75),
            Player("Jon Aramburu", "DEF", "Real Sociedad", 74),
            Player("Rafael Romo", "GK", "Universidad Católica", 73)
        )),
        Team("AUT", "Avusturya", "AUT", "🇦🇹", "#ED2939", "#FFFFFF", "L", 83, "Ralf Rangnick", listOf(
            Player("David Alaba", "DEF", "Real Madrid", 83),
            Player("Marcel Sabitzer", "MID", "Dortmund", 83),
            Player("Konrad Laimer", "MID", "Bayern Munich", 81),
            Player("Christoph Baumgartner", "MID", "RB Leipzig", 81),
            Player("Alexander Schlager", "GK", "Red Bull Salzburg", 76)
        )),
        Team("NZL", "Yeni Zelanda", "NZL", "🇳🇿", "#00247D", "#CC0000", "L", 74, "Darren Bazeley", listOf(
            Player("Chris Wood", "FOR", "Nottingham Forest", 78),
            Player("Sarpreet Singh", "MID", "Unattached", 71),
            Player("Liberato Cacace", "DEF", "Empoli", 73),
            Player("Matthew Garbett", "MID", "NAC Breda", 70),
            Player("Alex Paulsen", "GK", "Auckland FC", 70)
        ))
    )

    // Helper to generate group matches
    fun generateMatches(): List<WorldCupMatch> {
        val matches = mutableListOf<WorldCupMatch>()
        var matchIdCount = 1

        val stadiums = listOf(
            "Azteca Stadium (Mexico City)",
            "MetLife Stadium (New York/New Jersey)",
            "Mercedes-Benz Stadium (Atlanta)",
            "SoFi Stadium (Los Angeles)",
            "Hard Rock Stadium (Miami)",
            "Gillette Stadium (Boston)",
            "BC Place (Vancouver)",
            "BMO Field (Toronto)",
            "Estadio BBVA (Monterrey)",
            "Estadio Akron (Guadalajara)",
            "AT&T Stadium (Dallas)",
            "NRG Stadium (Houston)",
            "Arrowhead Stadium (Kansas City)",
            "Lumen Field (Seattle)",
            "Lincoln Financial Field (Philadelphia)",
            "Levi's Stadium (San Francisco)"
        )

        // Group matches: Round-robin for each group
        val groupsList = ('A'..'L').map { it.toString() }

        groupsList.forEach { groupName ->
            val groupTeams = teams.filter { it.group == groupName }
            if (groupTeams.size == 4) {
                // Round Robin: 6 matches per group
                val pairings = listOf(
                    Pair(groupTeams[0], groupTeams[1]),
                    Pair(groupTeams[2], groupTeams[3]),
                    Pair(groupTeams[0], groupTeams[2]),
                    Pair(groupTeams[1], groupTeams[3]),
                    Pair(groupTeams[0], groupTeams[3]),
                    Pair(groupTeams[1], groupTeams[2])
                )

                pairings.forEachIndexed { index, pair ->
                    matches.add(
                        WorldCupMatch(
                            id = "M_$matchIdCount",
                            group = groupName,
                            homeTeamId = pair.first.id,
                            awayTeamId = pair.second.id,
                            homeScore = null,
                            awayScore = null,
                            isPlayed = false,
                            date = "Matchday ${index / 2 + 1}",
                            stadium = stadiums[(matchIdCount - 1) % stadiums.size]
                        )
                    )
                    matchIdCount++
                }
            }
        }
        return matches
    }
}
