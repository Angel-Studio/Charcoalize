package fr.julespvx.charcoalize.ui.home.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.julespvx.charcoalize.data.air_quality.AirQualityApi
import fr.julespvx.charcoalize.ui.home.CardWidget

val AirQualityWidget = CardWidget(
    content = { AirQualityWidget() },
)

@Preview
@Composable
fun AirQualityWidget() {
    Box(
        modifier = Modifier
            .background(AirQualityApi.airQuality.status.color)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = AirQualityApi.airQuality.city.value,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${AirQualityApi.airQuality.percentage.value}%",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.inverseOnSurface,
            )
            Text(
                text = "AQI: ${AirQualityApi.airQuality.aqi.value}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.inverseOnSurface,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(vertical = 2.dp, horizontal = 8.dp),
            ) {
                Text(
                    text = AirQualityApi.airQuality.status.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = AirQualityApi.airQuality.status.color,
                )
            }
        }
    }
}