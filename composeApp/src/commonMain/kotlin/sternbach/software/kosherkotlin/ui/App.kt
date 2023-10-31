package sternbach.software.kosherkotlin.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import sternbach.software.kosherkotlin.ComplexZmanimCalendar
import sternbach.software.kosherkotlin.ZmanDescriptionFormatter
import sternbach.software.kosherkotlin.theme.AppTheme
import sternbach.software.kosherkotlin.theme.LocalThemeIsDark

@Composable
internal fun App() = AppTheme {
    val calc = remember { ComplexZmanimCalendar() }
    val fmt = remember { ZmanDescriptionFormatter() }
    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Row(
            horizontalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.weight(1.0f))

            var isDark by LocalThemeIsDark.current
            IconButton(
                onClick = { isDark = !isDark }
            ) {
                Icon(
                    modifier = Modifier.padding(8.dp).size(20.dp),
                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = null
                )
            }
        }
        LazyColumn(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(calc.allZmanim.sortedBy { it.momentOfOccurrence }) {
                Card(Modifier.fillMaxWidth()) {
                    Text(text = it.rules.type.friendlyNameHebrew, modifier = Modifier.fillParentMaxWidth(), textAlign = TextAlign.Center)
                    Text(text = fmt.formatShortDescription(it, true), modifier = Modifier.fillParentMaxWidth(), textAlign = TextAlign.Center)
                    Text(text = it.momentOfOccurrence.toString(), modifier = Modifier.fillParentMaxWidth(), textAlign = TextAlign.Center)
                }
            }
        }
    }
}