package fr.julespvx.charcoalize.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import fr.julespvx.charcoalize.R
import fr.julespvx.charcoalize.ui.theme.CharcoalizeTheme

@Composable
fun MusicPlayer(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    cover: Bitmap? = BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.nous),
    artist: String = "Artist",
    title: String = "Title",
    album: String = "Album",
    onPlayPause: (Boolean) -> Unit = {},
    onSkipNext: () -> Unit = {},
    onSkipPrevious: () -> Unit = {},
) {
    val bitmap = cover?.asImageBitmap() ?: BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.default_image).asImageBitmap()
    ConstraintLayout(
        modifier = modifier,
    ) {
        val (img, background, controls) = createRefs()
        // Background blur
        Image(
            bitmap = bitmap,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .blur(100.dp, BlurredEdgeTreatment.Unbounded)
                .constrainAs(background) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
        )
        // Image
        Card(
            modifier = Modifier
                .constrainAs(img) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
            shape = MaterialTheme.shapes.large,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(MaterialTheme.shapes.large)
                        .aspectRatio(1f)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 16.dp,
                        ),
                ) {
                    // Artist
                    Text(
                        text = artist,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    // Title
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    // Album
                    Text(
                        text = album,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        // Controls
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .constrainAs(controls) {
                    top.linkTo(img.bottom)
                    bottom.linkTo(img.bottom)
                    end.linkTo(img.end, margin = 16.dp)

                    height = Dimension.wrapContent
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
        ) {
            Row {
                // Previous
                IconButton(
                    onClick = onSkipPrevious,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipPrevious,
                        contentDescription = null,
                    )
                }
                // Play/Pause
                OutlinedIconToggleButton(
                    checked = isPlaying,
                    onCheckedChange = onPlayPause,
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = null,
                    )
                }
                // Next
                IconButton(
                    onClick = onSkipNext,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true,)
@Composable
fun MusicPlayerPreview() {
    CharcoalizeTheme(darkTheme = true) {
        MusicPlayer(
            modifier = Modifier.size(
                width = 600.dp,
                height = 100.dp,
            ),
        )
    }
}