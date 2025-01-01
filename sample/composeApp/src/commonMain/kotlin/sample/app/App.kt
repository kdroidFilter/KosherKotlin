@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package sample.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.kdroidfilter.kosherkotlin.ComplexZmanimCalendar
import io.github.kdroidfilter.kosherkotlin.ZmanDescriptionFormatter
import io.github.kdroidfilter.kosherkotlin.util.GeoLocation
import kosherkotlin.sample.composeapp.generated.resources.`NotoSansHebrew_VariableFont_wdth,wght`
import kosherkotlin.sample.composeapp.generated.resources.Res
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.Font

@Composable
fun App() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ZmanimCalendarApp()
    }
}

@Composable
fun ZmanimCalendarApp() {
    // Définition des villes et de leurs coordonnées
    val cities = listOf(
        "ירושלים" to GeoLocation("Jerusalem", 31.7683, 35.2137, 800.0, TimeZone.of("Asia/Jerusalem")),
        "בני ברק" to GeoLocation("Bnei Brak", 32.0833, 34.8333, 30.0, TimeZone.of("Asia/Jerusalem")),
        "אשדוד" to GeoLocation("Ashdod", 31.8044, 34.6553, 50.0, TimeZone.of("Asia/Jerusalem")),
        "נתניה" to GeoLocation("Netanya", 32.331, 34.8599, 14.0, TimeZone.of("Asia/Jerusalem"))
    )

    // État pour la ville sélectionnée
    val selectedCity = remember { mutableStateOf(cities.first()) }
    var zmanimCalendar by remember { mutableStateOf(ComplexZmanimCalendar(selectedCity.value.second)) }
    val descriptionFormatter = remember { ZmanDescriptionFormatter() }

    // Police pour l’affichage en hébreu
    val hebrewFont = FontFamily(Font(Res.font.`NotoSansHebrew_VariableFont_wdth,wght`))

    // Scaffolding principal
    Scaffold(
        modifier = Modifier.background(Color.White)
            .widthIn(max = 600.dp),
        topBar = {
            TopAppBar(
                title = {

                    Text(
                        text = "לוח זמנים",
                        style = MaterialTheme.typography.displayLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(innerPadding)
        , contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Ligne de boutons pour sélectionner la ville
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cities.forEach { city ->
                        Button(
                            onClick = {
                                selectedCity.value = city
                                zmanimCalendar = ComplexZmanimCalendar(city.second)
                            },
                            enabled = selectedCity.value != city,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedCity.value == city) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                contentColor = if (selectedCity.value == city) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                            ),
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = city.first,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                                fontFamily = hebrewFont
                            )
                        }
                    }
                }

                // Préparation des Zmanim
                val allZmanim = zmanimCalendar.allZmanim
                    .filter { it.momentOfOccurrence != null }
                    .sortedBy { it.momentOfOccurrence }

                // Liste des Zmanim
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(allZmanim) { zman ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = zman.definition.type.friendlyNameHebrew,
                                    fontFamily = hebrewFont,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(Modifier.height(10.dp))

                                Text(
                                    text = descriptionFormatter.formatShortDescription(zman.definition),
                                    fontFamily = hebrewFont,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(Modifier.height(10.dp))

                                val localDateTime = zman.momentOfOccurrence
                                    ?.toLocalDateTime(selectedCity.value.second.timeZone)
                                    ?.time
                                    .toString()

                                Text(
                                    text = localDateTime,
                                    fontFamily = hebrewFont,
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
