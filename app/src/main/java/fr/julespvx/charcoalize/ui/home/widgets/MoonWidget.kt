package fr.julespvx.charcoalize.ui.home.widgets

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.julespvx.charcoalize.data.moon.MoonPhaseApi
import fr.julespvx.charcoalize.ui.components.MoonPhase
import fr.julespvx.charcoalize.ui.home.CardWidget

val MoonWidget = CardWidget(
    content = { MoonWidget() },
)

@Preview
@Composable
fun MoonWidget() {
    val phases = MoonPhaseApi.moonPhases
    val phase = if (phases.moonPhases.isEmpty()) fr.julespvx.charcoalize.data.moon.MoonPhase() else phases.moonPhases[0]
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            text = DateUtils.formatDateTime(LocalContext.current, phase.moonrise.timeInMillis, DateUtils.FORMAT_SHOW_TIME) + " • " + DateUtils.formatDateTime(LocalContext.current, phase.moonset.timeInMillis, DateUtils.FORMAT_SHOW_TIME),
            style = MaterialTheme.typography.labelSmall,
        )
        MoonPhase(
            phases = phases,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )
        Text(
            text = phase.phaseName.uppercase(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}